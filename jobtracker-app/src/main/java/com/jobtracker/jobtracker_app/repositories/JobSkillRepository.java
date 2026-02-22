package com.jobtracker.jobtracker_app.repositories;

import com.jobtracker.jobtracker_app.dto.requests.JobSkillWithName;
import com.jobtracker.jobtracker_app.entities.Job;
import com.jobtracker.jobtracker_app.entities.JobSkill;
import com.jobtracker.jobtracker_app.entities.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JobSkillRepository extends JpaRepository<JobSkill, String> {
    // SELECT * FROM job_skills js JOIN skill s ON js.skill_id = s.id ...
    @Query("SELECT js FROM JobSkill js " +
            "JOIN FETCH js.skill s " +
            "WHERE js.job.id = :jobId " +
            "AND js.isDeleted = false " +
            "ORDER BY s.name ASC"
    )
    List<JobSkill> findByJobIdWithSkill(@Param("jobId") String jobId);

    @Query("""
       SELECT new com.jobtracker.jobtracker_app.dto.requests.JobSkillWithName(
           s.id,
           s.name,
           js.isRequired,
           js.proficiencyLevel
       )
       FROM JobSkill js
       JOIN js.skill s
       WHERE js.job.id = :jobId
         AND js.isDeleted = false
         AND s.isActive = true
         AND s.deletedAt IS NULL
       """)
    List<JobSkillWithName> findSkillsByJobId(String jobId);

    Optional<JobSkill> findByJobAndSkill(Job job, Skill skill);

    void deleteByJobAndSkill(Job job, Skill skill);

    boolean existsByJobAndSkill(Job job, Skill skill);
}



