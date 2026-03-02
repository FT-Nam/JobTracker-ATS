package com.jobtracker.jobtracker_app.repositories.httpclient;

import com.jobtracker.jobtracker_app.dto.requests.email.EmailRequest;
import com.jobtracker.jobtracker_app.dto.responses.EmailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "email-client", url = "${brevo.url}")
public interface EmailClient {
    @PostMapping(value = "/v3/smtp/email", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    EmailResponse sendEmail(@RequestHeader("api-key") String apiKey, @RequestBody EmailRequest request);
}
