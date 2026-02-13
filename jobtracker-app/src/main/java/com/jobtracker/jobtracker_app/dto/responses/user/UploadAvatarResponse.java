package com.jobtracker.jobtracker_app.dto.responses.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UploadAvatarResponse {
    String avatarUrl;
}
