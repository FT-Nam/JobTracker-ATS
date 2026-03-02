package com.jobtracker.jobtracker_app.dto.requests.email;

import com.jobtracker.jobtracker_app.dto.requests.EmailContext;
import com.jobtracker.jobtracker_app.enums.EmailType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SendEmailRequest {

    EmailType templateCode;
    String companyId;

    String recipientEmail;
    String recipientName;

    String replyToEmail;
    String replyToName;

    EmailContext context;
}

