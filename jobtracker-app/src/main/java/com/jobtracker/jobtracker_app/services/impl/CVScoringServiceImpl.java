package com.jobtracker.jobtracker_app.services.impl;

import com.jobtracker.jobtracker_app.dto.requests.JobSkillWithName;
import com.jobtracker.jobtracker_app.dto.responses.SkillResult;
import com.jobtracker.jobtracker_app.dto.responses.application.ApplicationScoringResult;
import com.jobtracker.jobtracker_app.entities.JobSkill;
import com.jobtracker.jobtracker_app.services.CVScoringService;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

@Service
@Slf4j
public class CVScoringServiceImpl implements CVScoringService {
    // cvText là text đã extract từ PDF, jobSkill là list skill của job
    // 1. CV rỗng, Job ko skill -> buildZeroResult (Tránh npe cho các hàm sau)
    // 2. normalize CV: bỏ dấu, lowercase, gom space thành 1
    // 3. Gọi evaluateSkills để add skill tương ứng vào matched và missing cho requiredSkills và optionalSkills
    // 4. Tính điểm dựa trên matched và total
    // 5. Return response
    @Override
    public ApplicationScoringResult score(String cvText,
                                          List<JobSkillWithName> jobSkills) {

        if (cvText == null || cvText.isBlank()
                || jobSkills == null || jobSkills.isEmpty()) {
            return buildZeroResult(jobSkills);
        }

        String normalizedCv = normalize(cvText);

        List<JobSkillWithName> requiredSkills = jobSkills.stream()
                .filter(s -> Boolean.TRUE.equals(s.getIsRequired()))
                .toList();

        List<JobSkillWithName> optionalSkills = jobSkills.stream()
                .filter(s -> !Boolean.TRUE.equals(s.getIsRequired()))
                .toList();

        SkillResult requiredResult = evaluateSkills(normalizedCv, requiredSkills);
        SkillResult optionalResult = evaluateSkills(normalizedCv, optionalSkills);

        int finalScore = calculateScore(
                requiredResult.getMatched().size(), requiredSkills.size(),
                optionalResult.getMatched().size(), optionalSkills.size()
        );

        log.info("CV scored: {}%", finalScore);

        return ApplicationScoringResult.builder()
                .matchScore(finalScore)
                .matchedRequiredCount(requiredResult.getMatched().size())
                .totalRequiredCount(requiredSkills.size())
                .matchedOptionalCount(optionalResult.getMatched().size())
                .totalOptionalCount(optionalSkills.size())
                .matchedRequiredSkills(requiredResult.getMatched())
                .missingRequiredSkills(requiredResult.getMissing())
                .matchedOptionalSkills(optionalResult.getMatched())
                .missingOptionalSkills(optionalResult.getMissing())
                .build();
    }

    // Lấy ra skill match và miss
    private SkillResult evaluateSkills(String normalizedCv,
                                       List<JobSkillWithName> skills) {

        List<String> matched = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        for (JobSkillWithName skill : skills) {
            if (containsSkill(normalizedCv, skill.getSkillName())) {
                matched.add(skill.getSkillName());
            } else {
                missing.add(skill.getSkillName());
            }
        }

        return SkillResult.builder()
                .matched(matched)
                .missing(missing)
                .build();
    }


    // (?<!\\S) = trước phải là space/đầu dòng
    // (?!\\S)  = sau phải là space/cuối dòng
    // Pattern.quote() = escape ký tự đặc biệt  => "C++" không bị hiểu là regex
    // (?<!\S)c\+\+(?!\S)
    private boolean containsSkill(String normalizedCv, String skillName) {
        if (skillName == null || skillName.isBlank()) return false;

        String normalizedSkill = normalize(skillName);
        String regex = "(?<!\\S)" + Pattern.quote(normalizedSkill) + "(?!\\S)";
        return Pattern.compile(regex).matcher(normalizedCv).find();
    }

    // bỏ dấu, lowercase, gom nhiều space thành 1
    // NFD là tách dấu(Ví dụ: ế => e + ^ + ')
    // \p{M} sẽ lấy tất cả dấu => bỏ dấu
    // \p{M} space nhiều hơn 1 => replace thành 1 space
    private String normalize(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .replaceAll("\\s+", " ")
                .trim();
    }

    private int calculateScore(int matchedRequired, int totalRequired,
                               int matchedOptional, int totalOptional) {

        if (totalRequired == 0 && totalOptional == 0) return 0;

        if (totalOptional == 0) {
            return percentage(matchedRequired, totalRequired);
        }

        if (totalRequired == 0) {
            return percentage(matchedOptional, totalOptional);
        }

        double requiredRatio = (double) matchedRequired / totalRequired;
        double optionalRatio = (double) matchedOptional / totalOptional;

        return (int) Math.round((requiredRatio * 0.7 + optionalRatio * 0.3) * 100);
    }

    private int percentage(int matched, int total) {
        return total == 0 ? 0 :
                (int) Math.round((double) matched / total * 100);
    }

    // Trường hợp CV rỗng hoặc Job không có skill
    private ApplicationScoringResult buildZeroResult(List<JobSkillWithName> skills) {

        if (skills == null) {
            return ApplicationScoringResult.builder()
                    .matchScore(0)
                    .build();
        }

        List<String> required = skills.stream()
                .filter(s -> Boolean.TRUE.equals(s.getIsRequired()))
                .map(JobSkillWithName::getSkillName)
                .toList();

        List<String> optional = skills.stream()
                .filter(s -> !Boolean.TRUE.equals(s.getIsRequired()))
                .map(JobSkillWithName::getSkillName)
                .toList();

        return ApplicationScoringResult.builder()
                .matchScore(0)
                .matchedRequiredCount(0)
                .totalRequiredCount(required.size())
                .matchedOptionalCount(0)
                .totalOptionalCount(optional.size())
                .missingRequiredSkills(required)
                .missingOptionalSkills(optional)
                .matchedRequiredSkills(Collections.emptyList())
                .matchedOptionalSkills(Collections.emptyList())
                .build();
    }

    private String extractText(InputStream inputStream) throws IOException {
        byte[] bytes = inputStream.readAllBytes();
        try(PDDocument pdDocument = Loader.loadPDF(bytes)){
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(pdDocument);
        }
    }
}
