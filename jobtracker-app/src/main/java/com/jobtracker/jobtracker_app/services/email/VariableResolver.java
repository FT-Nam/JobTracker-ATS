package com.jobtracker.jobtracker_app.services.email;

import com.jobtracker.jobtracker_app.dto.requests.EmailContext;

public interface VariableResolver {

    String getKey();

    Object resolve(EmailContext context);
}
