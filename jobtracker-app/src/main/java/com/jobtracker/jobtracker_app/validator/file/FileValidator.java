package com.jobtracker.jobtracker_app.validator.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileValidator {
    void validate(MultipartFile file) throws IOException;
}
