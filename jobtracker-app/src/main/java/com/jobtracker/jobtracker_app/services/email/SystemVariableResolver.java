package com.jobtracker.jobtracker_app.services.email;

import com.jobtracker.jobtracker_app.dto.requests.EmailContext;
import com.jobtracker.jobtracker_app.entities.Application;
import com.jobtracker.jobtracker_app.entities.Company;
import com.jobtracker.jobtracker_app.entities.CompanySubscription;
import com.jobtracker.jobtracker_app.entities.Interview;
import com.jobtracker.jobtracker_app.entities.Job;
import com.jobtracker.jobtracker_app.entities.Payment;
import com.jobtracker.jobtracker_app.entities.SubscriptionPlan;
import com.jobtracker.jobtracker_app.entities.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
public class SystemVariableResolver {

    private static final String APP_BASE_URL = "https://app.yourats.com";
    private static final DateTimeFormatter HUMAN_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public Object resolve(String variable, EmailContext context) {
        Company company = context.getCompany();
        User user = context.getUser();
        Job job = context.getJob() != null
                ? context.getJob()
                : Optional.ofNullable(context.getApplication()).map(Application::getJob).orElse(null);
        Application application = context.getApplication();
        Interview interview = context.getInterview();
        SubscriptionPlan subscriptionPlan = context.getSubscriptionPlan();
        CompanySubscription companySubscription = context.getCompanySubscription();
        Payment payment = context.getPayment();

        return switch (variable) {
            case "company_name" -> company != null ? company.getName() : "";

            case "hr_name" -> user != null
                    ? (nullToEmpty(user.getFirstName()) + " " + nullToEmpty(user.getLastName())).trim()
                    : "";

            case "candidate_name" -> application != null ? nullToEmpty(application.getCandidateName()) : "";

            case "job_title" -> job != null ? nullToEmpty(job.getTitle()) : "";

            case "application_status" -> application != null && application.getStatus() != null
                    ? nullToEmpty(application.getStatus().getDisplayName())
                    : "";
            case "application_link" -> buildApplicationLink(application);

            case "interview_time" -> buildInterviewTime(interview);
            case "interview_location" -> interview != null ? nullToEmpty(interview.getLocation()) : "";
            case "meeting_link" -> interview != null ? nullToEmpty(interview.getMeetingLink()) : "";

            case "plan_name" -> buildPlanName(subscriptionPlan, companySubscription);
            case "plan_price" -> buildPlanPrice(subscriptionPlan, companySubscription, payment);
            case "plan_expire_at" -> companySubscription != null && companySubscription.getEndDate() != null
                    ? formatHumanDateTime(companySubscription.getEndDate())
                    : "";

            default -> "";
        };
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private String formatHumanDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(HUMAN_DATE_TIME_FORMATTER);
    }

    private String buildApplicationLink(Application application) {
        if (application == null) {
            return "";
        }
        String token = nullToEmpty(application.getApplicationToken());
        if (!token.isBlank()) {
            return APP_BASE_URL + "/status?token=" + token;
        }
        return APP_BASE_URL + "/applications/" + nullToEmpty(application.getId());
    }

    private String buildInterviewTime(Interview interview) {
        if (interview == null || interview.getScheduledDate() == null) {
            return "";
        }
        String start = formatHumanDateTime(interview.getScheduledDate());
        if (interview.getDurationMinutes() == null) {
            return start;
        }
        String end = formatHumanDateTime(interview.getScheduledDate().plusMinutes(interview.getDurationMinutes()));
        return start + "–" + end;
    }

    private String buildPlanName(SubscriptionPlan subscriptionPlan, CompanySubscription companySubscription) {
        if (subscriptionPlan != null) {
            return nullToEmpty(subscriptionPlan.getName());
        }
        if (companySubscription != null && companySubscription.getPlan() != null) {
            return nullToEmpty(companySubscription.getPlan().getName());
        }
        return "";
    }

    private String buildPlanPrice(
            SubscriptionPlan subscriptionPlan,
            CompanySubscription companySubscription,
            Payment payment
    ) {
        SubscriptionPlan plan = subscriptionPlan;
        if (plan == null && companySubscription != null) {
            plan = companySubscription.getPlan();
        }
        if (plan == null || plan.getPrice() == null) {
            return "";
        }
        String currency = payment != null ? nullToEmpty(payment.getCurrency()) : "";
        if (!currency.isBlank()) {
            return plan.getPrice().toPlainString() + " " + currency;
        }
        return plan.getPrice().toPlainString();
    }
}

