package com.jobtracker.jobtracker_app.dto.responses.company;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanyRegisterResponse {
    String id;
    String name;
}
