package com.jobtracker.jobtracker_app.services;

import com.jobtracker.jobtracker_app.dto.requests.application.ApplyToJobRequest;
import com.jobtracker.jobtracker_app.dto.requests.application.UploadAttachmentsRequest;
import com.jobtracker.jobtracker_app.dto.responses.application.TrackStatusResponse;
import com.jobtracker.jobtracker_app.dto.responses.application.UploadAttachmentsResponse;

import java.io.IOException;

public interface ApplicationService {
    void ApplyToJob(ApplyToJobRequest request, String jobId) throws IOException;

    UploadAttachmentsResponse UploadAttachments(UploadAttachmentsRequest request, String applicationToken)
            throws IOException;

    TrackStatusResponse TrackStatus(String applicationToken);
}
