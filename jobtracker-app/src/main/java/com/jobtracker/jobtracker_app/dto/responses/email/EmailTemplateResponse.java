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
public class EmailTemplateResponse {
    String id;
    String companyId;
    String code;
    String name;
    String subject;
    List<String> variables;
    String fromName;
    Boolean isActive;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
