package com.jobtracker.jobtracker_app.services.email.resolvers;

import com.jobtracker.jobtracker_app.dto.requests.email.EmailContext;
import com.jobtracker.jobtracker_app.entities.Application;
import com.jobtracker.jobtracker_app.entities.Job;
import com.jobtracker.jobtracker_app.repositories.ApplicationRepository;
import com.jobtracker.jobtracker_app.repositories.JobRepository;
import com.jobtracker.jobtracker_app.enums.SystemVariable;
import com.jobtracker.jobtracker_app.services.email.VariableResolver;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JobTitleResolver implements VariableResolver {

    JobRepository jobRepository;
    ApplicationRepository applicationRepository;

    @Override
    public String getKey() {
        return SystemVariable.JOB_TITLE.getKey();
    }

    @Override
    public Object resolve(EmailContext context) {
        if (context.getCompanyId() == null) return "";
        String jobId = context.getJobId();
        if (jobId == null && context.getApplicationId() != null) {
            jobId = applicationRepository.findByIdAndCompany_IdWithJobAndStatus(context.getApplicationId(), context.getCompanyId())
                    .map(Application::getJob)
                    .map(Job::getId)
                    .orElse(null);
        }
        if (jobId == null) return "";
        return jobRepository.findByIdAndCompany_IdAndDeletedAtIsNull(jobId, context.getCompanyId())
                .map(Job::getTitle)
                .orElse("");
    }
}
