package com.jobtracker.jobtracker_app.services;

import com.jobtracker.jobtracker_app.dto.requests.email.ManualOfferRequest;
import com.jobtracker.jobtracker_app.entities.Application;
import com.jobtracker.jobtracker_app.entities.EmailVerificationToken;
import com.jobtracker.jobtracker_app.entities.Interview;
import com.jobtracker.jobtracker_app.entities.PasswordResetToken;
import com.jobtracker.jobtracker_app.entities.User;
import com.jobtracker.jobtracker_app.entities.UserInvitation;

public interface EmailService {
    void sendApplicationConfirmation(Application application);

    void sendInterviewScheduled(Interview interview, String customMessage);

    void sendInterviewRescheduled(Interview interview, String customMessage);

    void sendCandidateRejected(Application application, String customMessage);

    void sendCandidateHired(Application application, String customMessage);

    void sendManualOffer(Application application, ManualOfferRequest request);

    void sendUserInvite(User user, UserInvitation invitation);

    void sendUserInviteResend(User user, UserInvitation invitation);

    void sendEmailVerification(User user, EmailVerificationToken token);

    void sendEmailVerificationResend(User user, EmailVerificationToken token);

    void sendPasswordReset(User user, PasswordResetToken token);
}
