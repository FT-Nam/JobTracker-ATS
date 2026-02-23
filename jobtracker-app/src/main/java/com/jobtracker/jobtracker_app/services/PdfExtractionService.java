package com.jobtracker.jobtracker_app.services;

import java.io.IOException;
import java.io.InputStream;

public interface PdfExtractionService {
    String extractText(InputStream inputStream) throws IOException;
}
