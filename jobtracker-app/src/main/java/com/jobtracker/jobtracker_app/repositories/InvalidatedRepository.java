package com.jobtracker.jobtracker_app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobtracker.jobtracker_app.entities.InvalidatedToken;

public interface InvalidatedRepository extends JpaRepository<InvalidatedToken, String> {}
