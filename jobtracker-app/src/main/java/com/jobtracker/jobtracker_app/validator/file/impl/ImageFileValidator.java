package com.jobtracker.jobtracker_app.validator.file.impl;

import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.validator.file.FileValidator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Component
public class ImageFileValidator implements FileValidator {
    @Value("${file.max-image-size}")
    long maxSize;

    @Override
    public void validate(MultipartFile file) throws IOException {
        if(file == null || file.isEmpty()){
            throw new AppException(ErrorCode.FILE_EMPTY);
        }

        if(file.getSize() > maxSize){
            throw new AppException(ErrorCode.FILE_TOO_LARGE);
        }

        String contentType = file.getContentType();
        if (contentType == null ||
                (!contentType.equals("image/jpeg") &&
                        !contentType.equals("image/png") &&
                        !contentType.equals("image/webp"))){
            throw new AppException(ErrorCode.INVALID_FILE_TYPE);
        }

        // Xác minh file thực sự là ảnh hợp lệ, không phải file gắn đuôi image giả
        BufferedImage image = ImageIO.read(file.getInputStream());
        if (image == null) {
            throw new AppException(ErrorCode.INVALID_FILE_TYPE);
        }
    }
}
