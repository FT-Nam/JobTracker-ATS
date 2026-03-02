package com.jobtracker.jobtracker_app.dto.requests.email;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReplyTo {
    String name;
    String email;
}
