package com.jobtracker.jobtracker_app.dto.responses;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentResponse {
    String id;
    String applicationId;
    String userId;
    String userName;
    String userAvatar;
    String commentText;
    Boolean isInternal;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
