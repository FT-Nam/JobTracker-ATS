package com.jobtracker.jobtracker_app.services.impl;

import com.jobtracker.jobtracker_app.dto.requests.CommentCreationRequest;
import com.jobtracker.jobtracker_app.dto.requests.CommentUpdateRequest;
import com.jobtracker.jobtracker_app.dto.responses.CommentResponse;
import com.jobtracker.jobtracker_app.entities.Application;
import com.jobtracker.jobtracker_app.entities.Comment;
import com.jobtracker.jobtracker_app.entities.User;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.mappers.CommentMapper;
import com.jobtracker.jobtracker_app.repositories.ApplicationRepository;
import com.jobtracker.jobtracker_app.repositories.CommentRepository;
import com.jobtracker.jobtracker_app.services.CommentService;
import com.jobtracker.jobtracker_app.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    CommentRepository commentRepository;
    ApplicationRepository applicationRepository;
    CommentMapper commentMapper;
    SecurityUtils securityUtils;

    @Override
    public Page<CommentResponse> getComments(String applicationId, Boolean isInternal, Pageable pageable) {
        User currentUser = securityUtils.getCurrentUser();

        Application application = applicationRepository
                .findByIdAndCompanyIdAndDeletedAtIsNull(applicationId, currentUser.getCompany().getId())
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_NOT_EXISTED));

        return commentRepository.findByApplicationIdAndDeletedAtIsNull(application.getId(), isInternal, pageable)
                .map(commentMapper::toCommentResponse);
    }

    @Override
    @Transactional
    public CommentResponse create(String applicationId, CommentCreationRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        Application application = applicationRepository
                .findByIdAndCompanyIdAndDeletedAtIsNull(applicationId, currentUser.getCompany().getId())
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_NOT_EXISTED));

        Comment comment = commentMapper.toComment(request);
        comment.setApplication(application);
        comment.setUser(currentUser);
        comment = commentRepository.save(comment);

        return commentMapper.toCommentResponse(comment);
    }

    @Override
    @Transactional
    public CommentResponse update(String applicationId, String commentId, CommentUpdateRequest request) {
        User currentUser = securityUtils.getCurrentUser();
        Application application = applicationRepository
                .findByIdAndCompanyIdAndDeletedAtIsNull(applicationId, currentUser.getCompany().getId())
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_NOT_EXISTED));
        Comment comment = commentRepository.findById(commentId)
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_EXISTED));

        // Comment phải thuộc đúng Application mà client đang thao tác
        if (!comment.getApplication().getId().equals(application.getId())) {
            throw new AppException(ErrorCode.COMMENT_NOT_EXISTED);
        }

        // Comment thuộc đúng User(Ví dụ sai: User A update được comment của User B)
        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        commentMapper.updateComment(comment, request);
        return commentMapper.toCommentResponse(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public void delete(String applicationId, String commentId) {
        User currentUser = securityUtils.getCurrentUser();

        Application application = applicationRepository
                .findByIdAndCompanyIdAndDeletedAtIsNull(applicationId, currentUser.getCompany().getId())
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_NOT_EXISTED));

        Comment comment = commentRepository.findById(commentId)
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_EXISTED));

        if (!comment.getApplication().getId().equals(application.getId())) {
            throw new AppException(ErrorCode.COMMENT_NOT_EXISTED);
        }

        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        comment.softDelete();
        commentRepository.save(comment);
    }
}
