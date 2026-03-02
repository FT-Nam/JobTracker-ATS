package com.jobtracker.jobtracker_app.services.email.resolvers;

import com.jobtracker.jobtracker_app.dto.requests.EmailContext;
import com.jobtracker.jobtracker_app.entities.Application;
import com.jobtracker.jobtracker_app.entities.Job;
import com.jobtracker.jobtracker_app.repositories.ApplicationRepository;
import com.jobtracker.jobtracker_app.repositories.JobRepository;
import com.jobtracker.jobtracker_app.enums.SystemVariable;
import com.jobtracker.jobtracker_app.services.email.VariableResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobTitleResolver implements VariableResolver {

    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;

    @Override
    public String getKey() {
        return SystemVariable.JOB_TITLE.getKey();
    }

    @Override
    public Object resolve(EmailContext context) {
        String jobId = context.getJobId();
        if (jobId == null && context.getApplicationId() != null) {
            jobId = applicationRepository.findByIdWithJobAndStatus(context.getApplicationId())
                    .map(Application::getJob)
                    .map(Job::getId)
                    .orElse(null);
        }
        if (jobId == null) return "";
        return jobRepository.findById(jobId)
                .map(Job::getTitle)
                .orElse("");
    }
}
