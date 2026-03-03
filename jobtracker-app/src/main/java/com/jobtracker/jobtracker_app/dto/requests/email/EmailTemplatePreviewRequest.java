package com.jobtracker.jobtracker_app.dto.requests.email;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailTemplatePreviewRequest {

    Map<String, Object> sampleData;

    String applicationId;

    String interviewId;
}
