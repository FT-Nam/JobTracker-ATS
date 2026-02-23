package com.jobtracker.jobtracker_app.services.impl;

import com.jobtracker.jobtracker_app.services.PdfExtractionService;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.io.InputStream;

public class PdfExtractionServiceImpl implements PdfExtractionService {
    @Override
    public String extractText(InputStream inputStream) throws IOException {
        byte[] bytes = inputStream.readAllBytes();
        try(PDDocument pdDocument = Loader.loadPDF(bytes)){
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(pdDocument);
        }
    }
}
