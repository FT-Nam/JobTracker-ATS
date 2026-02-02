package com.jobtracker.jobtracker_app.services.impl;

import com.jobtracker.jobtracker_app.dto.requests.SkillRequest;
import com.jobtracker.jobtracker_app.dto.responses.SkillResponse;
import com.jobtracker.jobtracker_app.entities.Skill;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.mappers.SkillMapper;
import com.jobtracker.jobtracker_app.repositories.SkillRepository;
import com.jobtracker.jobtracker_app.services.SkillService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SkillServiceImpl implements SkillService {
    SkillRepository skillRepository;
    SkillMapper skillMapper;

    @Override
    @Transactional
    public SkillResponse create(SkillRequest request) {
        Skill skill = skillMapper.toSkill(request);
        return skillMapper.toSkillResponse(skillRepository.save(skill));
    }

    @Override
    public SkillResponse getById(String id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SKILL_NOT_EXISTED));
        return skillMapper.toSkillResponse(skill);
    }

    @Override
    public Page<SkillResponse> getAll(Pageable pageable) {
        return skillRepository.findAll(pageable).map(skillMapper::toSkillResponse);
    }

    @Override
    @Transactional
    public SkillResponse update(String id, SkillRequest request) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SKILL_NOT_EXISTED));

        skillMapper.updateSkill(skill, request);

        return skillMapper.toSkillResponse(skillRepository.save(skill));
    }

    @Override
    @Transactional
    public void delete(String id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SKILL_NOT_EXISTED));

        skill.softDelete();
        skillRepository.save(skill);
    }
}




