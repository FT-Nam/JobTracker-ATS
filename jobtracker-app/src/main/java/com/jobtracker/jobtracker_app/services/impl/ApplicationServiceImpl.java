package com.jobtracker.jobtracker_app.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobtracker.jobtracker_app.dto.requests.JobSkillWithName;
import com.jobtracker.jobtracker_app.dto.requests.application.ApplyToJobRequest;
import com.jobtracker.jobtracker_app.dto.responses.application.ApplicationScoringResult;
import com.jobtracker.jobtracker_app.dto.responses.application.MatchedSkillsJson;
import com.jobtracker.jobtracker_app.entities.Application;
import com.jobtracker.jobtracker_app.entities.ApplicationStatus;
import com.jobtracker.jobtracker_app.entities.Job;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.repositories.ApplicationRepository;
import com.jobtracker.jobtracker_app.repositories.ApplicationStatusRepository;
import com.jobtracker.jobtracker_app.repositories.JobRepository;
import com.jobtracker.jobtracker_app.repositories.JobSkillRepository;
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

    @Override
    @Transactional
    public void ApplyToJob(ApplyToJobRequest request, String jobId) throws IOException {
        Job job = jobRepository.findByIdAndDeletedAtIsNull(jobId)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_EXISTED));

        ApplicationStatus newStatus = applicationStatusRepository.findByNameAndDeletedAtIsNull("NEW")
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_STATUS_NOT_EXISTED));


        pdfFileValidator.validate(request.getResume());

        String folderPath = "jobtracker_ats/applications/" + request.getCandidateEmail() + "/cv";

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
                .applicationToken(UUID.randomUUID().toString())
                .resumeFilePath((String) result.get("secure_url"))
                .appliedDate(LocalDate.now())
                .extractedText(extractText)
                .matchScore(scoreResult.getMatchScore())
                .matchedSkills(matchedSkills)
                .build();

        applicationRepository.save(application);
    }

    private String extractText(InputStream inputStream) throws IOException {
        byte[] bytes = inputStream.readAllBytes();
        try(PDDocument pdDocument = Loader.loadPDF(bytes)){
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(pdDocument);
        }
    }
}
