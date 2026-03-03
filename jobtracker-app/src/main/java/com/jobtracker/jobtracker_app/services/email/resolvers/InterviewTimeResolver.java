package com.jobtracker.jobtracker_app.services.email.resolvers;

import com.jobtracker.jobtracker_app.dto.requests.EmailContext;
import com.jobtracker.jobtracker_app.entities.Interview;
import com.jobtracker.jobtracker_app.repositories.InterviewRepository;
import com.jobtracker.jobtracker_app.enums.SystemVariable;
import com.jobtracker.jobtracker_app.services.email.VariableResolver;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class InterviewTimeResolver implements VariableResolver {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final InterviewRepository interviewRepository;

    @Override
    public String getKey() {
        return SystemVariable.INTERVIEW_TIME.getKey();
    }

    @Override
    public Object resolve(EmailContext context) {
        if (context.getInterviewId() == null || context.getCompanyId() == null) return "";
        return interviewRepository.findByIdAndCompany_IdAndDeletedAtIsNull(context.getInterviewId(), context.getCompanyId())
                .map(this::formatTime)
                .orElse("");
    }

    private String formatTime(Interview i) {
        if (i.getScheduledDate() == null) return "";
        String start = i.getScheduledDate().format(FORMATTER);
        if (i.getDurationMinutes() == null) return start;
        String end = i.getScheduledDate().plusMinutes(i.getDurationMinutes()).format(FORMATTER);
        return start + "–" + end;
    }
}
