package com.jobtracker.jobtracker_app.dto.requests.email;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailRequest {
    Sender sender;

    List<Recipient> to;

    ReplyTo replyTo;

    String subject;

    String htmlContent;

}
