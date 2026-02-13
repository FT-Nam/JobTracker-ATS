package com.jobtracker.jobtracker_app.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.jobtracker.jobtracker_app.dto.requests.AttachmentRequest;
import com.jobtracker.jobtracker_app.dto.requests.AttachmentUploadRequest;
import com.jobtracker.jobtracker_app.dto.responses.attachment.AttachmentCreationResponse;
import com.jobtracker.jobtracker_app.dto.responses.attachment.AttachmentResponse;
import com.jobtracker.jobtracker_app.entities.Application;
import com.jobtracker.jobtracker_app.entities.Attachment;
import com.jobtracker.jobtracker_app.entities.User;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.mappers.AttachmentMapper;
import com.jobtracker.jobtracker_app.repositories.ApplicationRepository;
import com.jobtracker.jobtracker_app.repositories.AttachmentRepository;
import com.jobtracker.jobtracker_app.repositories.CompanyRepository;
import com.jobtracker.jobtracker_app.repositories.UserRepository;
import com.jobtracker.jobtracker_app.services.AttachmentService;
import com.jobtracker.jobtracker_app.validator.file.FileValidator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AttachmentServiceImpl implements AttachmentService {
    AttachmentRepository attachmentRepository;
    AttachmentMapper attachmentMapper;
    ApplicationRepository applicationRepository;
    CompanyRepository companyRepository;
    UserRepository userRepository;
    Cloudinary cloudinary;
    FileValidator pdfFileValidator;

    @Override
    @Transactional
    public AttachmentCreationResponse uploadAttachment(String applicationId,
                                                       AttachmentUploadRequest request) throws IOException {
        pdfFileValidator.validate(request.getFile());

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        String folderPath = "jobtracker_ats/applications/" + applicationId + "/cv";

        Map<?,?> result = cloudinary.uploader().upload(request.getFile().getBytes(),
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

        String publicId = (String) result.get("public_id");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Application application = applicationRepository.findByIdAndCompanyId(applicationId, user.getCompany().getId())
                .orElseThrow(()-> new AppException(ErrorCode.APPLICATION_NOT_EXISTED));

        Attachment attachment = Attachment.builder()
                .id(publicId)
                .application(application)
                .company(user.getCompany())
                .user(user)
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

        return attachmentMapper.toAttachmentCreationResponse(saved);
    }

    @Override
    public URI downloadAttachment(String id) {

        String userId = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ATTACHMENT_NOT_EXISTED));

        if (!attachment.getCompany().getId().equals(user.getCompany().getId())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String signedUrl = cloudinary.url()
                .resourceType("raw")
                .type("authenticated")
                .signed(true)
                .transformation(new Transformation().flags("attachment"))
                .generate(id);


        return URI.create(signedUrl);
        }


    @Override
    public Page<AttachmentResponse> getApplicationAttachments(String applicationId, Pageable pageable) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean exists = applicationRepository.existsByIdAndCompanyId(applicationId, user.getCompany().getId());

        if (!exists) {
            throw new AppException(ErrorCode.APPLICATION_NOT_EXISTED);
        }

        return attachmentRepository.findByApplication_Id(pageable,applicationId)
                .map(attachmentMapper::toAttachmentResponse);
    }

    @Override
    @Transactional
    public void delete(String id) throws IOException {
        if(!attachmentRepository.existsById(id)){
            throw new AppException(ErrorCode.ATTACHMENT_NOT_EXISTED);
        }

        Map result = cloudinary.uploader().destroy(id,
                ObjectUtils.asMap(
                        "resource_type", "raw",
                        "type", "authenticated"));

        if ("ok".equals(result.get("result"))) {
            attachmentRepository.deleteById(id);
        } else {
            throw new AppException(ErrorCode.DELETE_FILE_FAILED);
        }
    }
}
