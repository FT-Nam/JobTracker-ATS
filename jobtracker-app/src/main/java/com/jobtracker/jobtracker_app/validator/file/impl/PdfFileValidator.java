package com.jobtracker.jobtracker_app.validator.file.impl;

import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.validator.file.FileValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Component
public class PdfFileValidator implements FileValidator {
    @Value("${file.max-pdf-size}")
    long maxSize;

    @Override
    public void validate(MultipartFile file) throws IOException {
        if(file == null || file.isEmpty()){
            throw new AppException(ErrorCode.FILE_EMPTY);
        }

        if(file.getSize() > maxSize){
            throw new AppException(ErrorCode.FILE_TOO_LARGE);
        }

        if (!"application/pdf".equals(file.getContentType())) {
            throw new AppException(ErrorCode.INVALID_FILE_TYPE);
        }

        // Check valid file, pdf luôn bắt đầu bằng %PDF-...
        byte[] header = new byte[5];
        // try-with-resources, dùng với file,db,stream và sẽ tự động đóng mà ko cần .close() trong finally như trước
        // Không dùng catch vì muốn trả lỗi đẹp, ko muốn trả lỗi hệ thống
        try(InputStream inputStream = file.getInputStream()){
            if(inputStream.read(header) != 5){
                throw new AppException(ErrorCode.INVALID_FILE_TYPE);
            }
        }

        if (!new String(header).startsWith("%PDF-")) {
            throw new AppException(ErrorCode.INVALID_FILE_TYPE);
        }
    }
}
