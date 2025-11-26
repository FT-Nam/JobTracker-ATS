package com.jobtracker.jobtracker_app.configurations;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.jobtracker.jobtracker_app.entities.User;
import com.jobtracker.jobtracker_app.repositories.UserRepository;

@Component
public class AuditorAwareImpl implements AuditorAware<String> {
    @Autowired
    private UserRepository userRepository;

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.empty();
        }

        String id = authentication.getName();
        return userRepository.findById(id).map(User::getEmail);
    }
}
