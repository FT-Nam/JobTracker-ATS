package com.jobtracker.jobtracker_app.services.email.resolvers;

import com.jobtracker.jobtracker_app.dto.requests.EmailContext;
import com.jobtracker.jobtracker_app.enums.ManualVariable;
import com.jobtracker.jobtracker_app.services.email.VariableResolver;

import java.util.Map;

public class ManualVariableResolver implements VariableResolver {

    private final ManualVariable manualVariable;

    public ManualVariableResolver(ManualVariable manualVariable) {
        this.manualVariable = manualVariable;
    }

    @Override
    public String getKey() {
        return manualVariable.getKey();
    }

    @Override
    public Object resolve(EmailContext context) {
        Map<String, Object> manual = context.getManualValues();
        if (manual == null) return "";
        Object value = manual.get(manualVariable.getKey());
        return value != null ? value.toString() : "";
    }
}
