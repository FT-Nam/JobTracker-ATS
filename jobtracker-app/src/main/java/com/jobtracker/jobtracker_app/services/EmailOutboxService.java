package com.jobtracker.jobtracker_app.services;

import com.jobtracker.jobtracker_app.dto.requests.email.SendEmailRequest;
import com.jobtracker.jobtracker_app.dto.responses.EmailResponse;

public interface EmailOutboxService {
    void createEmailOutbox(SendEmailRequest request);
}
