package com.jobtracker.jobtracker_app.dto.responses.email;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailTemplatePreviewResponse {
    String subject;
    String htmlContent;
}
