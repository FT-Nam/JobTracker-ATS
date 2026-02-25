package com.jobtracker.jobtracker_app.mappers;

import com.jobtracker.jobtracker_app.dto.requests.CommentCreationRequest;
import com.jobtracker.jobtracker_app.dto.requests.CommentUpdateRequest;
import com.jobtracker.jobtracker_app.dto.responses.CommentResponse;
import com.jobtracker.jobtracker_app.entities.Comment;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "applicationId", source = "application.id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.lastName")
    @Mapping(target = "userAvatar", source = "user.avatarUrl")
    CommentResponse toCommentResponse(Comment comment);

    Comment toComment(CommentCreationRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateComment(@MappingTarget Comment comment, CommentUpdateRequest request);
}
