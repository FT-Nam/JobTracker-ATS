package com.jobtracker.jobtracker_app.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobtracker.jobtracker_app.dto.requests.JobSkillWithName;
import com.jobtracker.jobtracker_app.dto.requests.application.ApplyToJobRequest;
import com.jobtracker.jobtracker_app.dto.requests.application.UploadAttachmentsRequest;
import com.jobtracker.jobtracker_app.dto.responses.application.*;
import com.jobtracker.jobtracker_app.entities.Application;
import com.jobtracker.jobtracker_app.entities.ApplicationStatus;
import com.jobtracker.jobtracker_app.entities.Attachment;
import com.jobtracker.jobtracker_app.entities.Job;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.repositories.*;
import com.jobtracker.jobtracker_app.services.ApplicationService;
import com.jobtracker.jobtracker_app.services.CVScoringService;
import com.jobtracker.jobtracker_app.validator.file.impl.PdfFileValidator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationServiceImpl implements ApplicationService {
    PdfFileValidator pdfFileValidator;
    Cloudinary cloudinary;
    JobRepository jobRepository;
    CVScoringService cvScoringService;
    JobSkillRepository jobSkillRepository;
    ObjectMapper objectMapper;
    ApplicationRepository applicationRepository;
    ApplicationStatusRepository applicationStatusRepository;
    AttachmentRepository attachmentRepository;

    private static final String STATUS_NEW = "NEW";
    private static final String STATUS_INTERVIEWING = "INTERVIEWING";
    private static final String STATUS_SCREENING = "SCREENING";

    @Override
    @Transactional
    public void ApplyToJob(ApplyToJobRequest request, String jobId) throws IOException {
        Job job = jobRepository.findByIdAndDeletedAtIsNull(jobId)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_EXISTED));

        ApplicationStatus newStatus = applicationStatusRepository.findByNameAndDeletedAtIsNull(STATUS_NEW)
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_STATUS_NOT_EXISTED));


        pdfFileValidator.validate(request.getResume());

        String applicationToken = UUID.randomUUID().toString();

        String folderPath = "jobtracker_ats/applications/" + applicationToken + "/cv";

        Map<?,?> result = cloudinary.uploader().upload(request.getResume().getBytes(),
                ObjectUtils.asMap(
                        "use_filename", true,
                        "unique_filename", true,
                        "folder", folderPath,
                        "type", "authenticated",
                        "resource_type", "raw"
                ));

        if(result.get("asset_id") == null || result.get("secure_url") == null){
            throw new AppException(ErrorCode.UPLOAD_FILE_FAILED);
        }

        String extractText = extractText(request.getResume().getInputStream());

        List<JobSkillWithName> jobSkillWithNames = jobSkillRepository.findSkillsByJobId(jobId);

        ApplicationScoringResult scoreResult = cvScoringService.score(extractText,jobSkillWithNames);

        MatchedSkillsJson matchedSkillsJson = MatchedSkillsJson.builder()
                .matchedRequired(scoreResult.getMatchedRequiredSkills())
                .missingRequired(scoreResult.getMissingRequiredSkills())
                .matchedOptional(scoreResult.getMatchedOptionalSkills())
                .missingOptional(scoreResult.getMissingOptionalSkills())
                .build();

        // To JSON
        String matchedSkills = objectMapper.writeValueAsString(matchedSkillsJson);

        Application application = Application.builder()
                .job(job)
                .company(job.getCompany())
                .candidateName(request.getCandidateName())
                .candidateEmail(request.getCandidateEmail())
                .candidatePhone(request.getCandidatePhone())
                .coverLetter(request.getCoverLetter())
                .status(newStatus)
                .applicationToken(applicationToken)
                .resumeFilePath((String) result.get("secure_url"))
                .appliedDate(LocalDate.now())
                .extractedText(extractText)
                .matchScore(scoreResult.getMatchScore())
                .matchedSkills(matchedSkills)
                .build();

        String publicId = (String) result.get("public_id");

        Application saved = null;

        try{
            saved = applicationRepository.save(application);
        } catch (Exception e) {
            // rollback file nếu DB fail
            cloudinary.uploader().destroy(publicId,
                    ObjectUtils.asMap("resource_type", "raw", "type", "authenticated"));
            throw e;
        }
    }

    @Override
    public UploadAttachmentsResponse UploadAttachments(UploadAttachmentsRequest request, String applicationToken)
            throws IOException {
        Application application = applicationRepository.findByApplicationToken(applicationToken)
                .orElseThrow(()-> new AppException(ErrorCode.APPLICATION_NOT_EXISTED));

        boolean allowedByStatus = application.getStatus().getName().equals(STATUS_SCREENING) ||
                        application.getStatus().getName().equals(STATUS_INTERVIEWING);

        if (!application.getAllowAdditionalUploads() && !allowedByStatus) {
            throw new AppException(ErrorCode.UPLOAD_NOT_ALLOWED);
        }

        String folderPath = "jobtracker_ats/applications/" + applicationToken + "/attachment";

        Map<?,?> result = cloudinary.uploader().upload(request.getFile().getBytes(),
                ObjectUtils.asMap(
                        "use_filename", true,
                        "unique_filename", true,
                        "folder", folderPath,
                        "type", "authenticated",
                        "resource_type", "raw"
                ));

        if(result.get("asset_id") == null || result.get("secure_url") == null) {
            throw new AppException(ErrorCode.UPLOAD_FILE_FAILED);
        }

        String publicId = (String) result.get("public_id");

        Attachment attachment = Attachment.builder()
                .id(publicId)
                .application(application)
                .filename((String) result.get("display_name"))
                .originalFilename((String) result.get("original_filename"))
                .filePath((String) result.get("secure_url"))
                .fileSize((Long) result.get("bytes"))
                .fileType(request.getFile().getContentType())
                .attachmentType(request.getAttachmentType())
                .description(request.getDescription())
                .uploadedAt(LocalDateTime.now())
                .build();

        Attachment saved = null;

        try{
            saved = attachmentRepository.save(attachment);
        } catch (Exception e) {
            // rollback file nếu DB fail
            cloudinary.uploader().destroy(publicId,
                    ObjectUtils.asMap("resource_type", "raw", "type", "authenticated"));
            throw e;
        }

        return UploadAttachmentsResponse.builder()
                .id(attachment.getId())
                .applicationId(attachment.getApplication().getId())
                .fileName(attachment.getFilename())
                .attachmentType(attachment.getAttachmentType())
                .fileSize(attachment.getFileSize())
                .uploadedAt(attachment.getUploadedAt())
                .build();
    }

    @Override
    public TrackStatusResponse TrackStatus(String applicationToken) {
        Application application = applicationRepository.findByApplicationToken(applicationToken)
                .orElseThrow(()-> new AppException(ErrorCode.APPLICATION_NOT_EXISTED));

        ApplicationStatusDetail statusDetail = ApplicationStatusDetail.builder()
                .name(application.getStatus().getName())
                .displayName(application.getStatus().getDisplayName())
                .color(application.getStatus().getColor())
                .build();

        return TrackStatusResponse.builder()
                .id(application.getId())
                .jobTitle(application.getJob().getTitle())
                .candidateName(application.getCandidateName())
                .candidateEmail(application.getCandidateEmail())
                .status(statusDetail)
                .appliedDate(application.getAppliedDate())
                .updatedAt(application.getUpdatedAt())
                .build();
    }

    private String extractText(InputStream inputStream) throws IOException {
        byte[] bytes = inputStream.readAllBytes();
        try(PDDocument pdDocument = Loader.loadPDF(bytes)){
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(pdDocument);
        }
    }
}
