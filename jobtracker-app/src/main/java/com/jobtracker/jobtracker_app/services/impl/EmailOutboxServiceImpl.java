package com.jobtracker.jobtracker_app.services.impl;

import com.jobtracker.jobtracker_app.dto.requests.email.*;
import com.jobtracker.jobtracker_app.dto.responses.EmailResponse;
import com.jobtracker.jobtracker_app.entities.EmailOutbox;
import com.jobtracker.jobtracker_app.entities.EmailTemplate;
import com.jobtracker.jobtracker_app.enums.EmailStatus;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.repositories.EmailOutboxRepository;
import com.jobtracker.jobtracker_app.repositories.EmailTemplateRepository;
import com.jobtracker.jobtracker_app.services.EmailOutboxService;
import com.jobtracker.jobtracker_app.services.TemplateRenderer;
import com.jobtracker.jobtracker_app.services.email.EmailVariableResolver;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailOutboxServiceImpl implements EmailOutboxService {
    EmailTemplateRepository emailTemplateRepository;
    TemplateRenderer templateRenderer;
    EmailVariableResolver emailVariableResolver;
    EmailOutboxRepository emailOutboxRepository;



    @Value("${brevo.email-system}")
    @NonFinal
    String emailSystem;

    @Value("${brevo.email-system-name}")
    @NonFinal
    String emailSystemName;

    @Override
    @Transactional
    public void createEmailOutbox(SendEmailRequest request) {

        EmailTemplate template = emailTemplateRepository
                .findByCodeAndCompany_IdAndIsActiveTrueAndDeletedAtIsNull(request.getCompanyId(), request.getTemplateCode().toString())
                .orElseGet(() ->
                        emailTemplateRepository
                                .findByCodeAndCompanyIsNullAndIsActiveTrueAndDeletedAtIsNull(request.getTemplateCode().toString())
                                .orElseThrow(() ->
                                        new AppException(ErrorCode.EMAIL_TEMPLATE_NOT_FOUND)
                                )
                );

        Map<String, Object> variables = emailVariableResolver.buildAllVariables(request.getContext());

        if (request.getContext().getManualValues() != null) {
            variables.putAll(request.getContext().getManualValues());
        }

        String subject = templateRenderer.render(template.getSubject(), variables);
        String html = templateRenderer.render(template.getHtmlContent(), variables);

        EmailOutbox emailOutbox = EmailOutbox.builder()
                .toEmail(request.getRecipientEmail())
                .toName(request.getRecipientName())

                .fromEmail(emailSystem)
                .fromName(emailSystemName)

                .replyToEmail(request.getReplyToEmail())
                .replyToName(request.getReplyToName())

                .subject(subject)
                .htmlBody(html)

                .status(EmailStatus.PENDING)
                .retryCount(0)
                .build();

        emailOutboxRepository.save(emailOutbox);
    }
}
