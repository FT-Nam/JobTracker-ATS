package com.jobtracker.jobtracker_app.repositories;

import com.jobtracker.jobtracker_app.entities.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, String> {
}




