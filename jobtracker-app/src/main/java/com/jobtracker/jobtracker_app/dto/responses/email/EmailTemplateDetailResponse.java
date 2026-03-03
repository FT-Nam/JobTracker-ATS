package com.jobtracker.jobtracker_app.dto.responses.email;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailTemplateDetailResponse {
    String id;
    String companyId;
    String code;
    String name;
    String subject;
    String htmlContent;
    List<String> variables;
    String fromName;
    Boolean isActive;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
