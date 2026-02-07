package com.jobtracker.jobtracker_app.repositories;

import com.jobtracker.jobtracker_app.entities.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, String> {
//    @Query("SELECT s FROM Skill s WHERE s.id = :id AND s.deletedAt IS NULL")
    Optional<Skill> findByIdAndDeletedAtIsNull(@Param("id") String id);

//    @Query("SELECT s FROM Skill s WHERE s.deletedAt IS NULL")
    Page<Skill> findAllByDeletedAtIsNull(Pageable pageable);
}




