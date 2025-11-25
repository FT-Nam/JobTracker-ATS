package com.jobtracker.jobtracker_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobtracker.jobtracker_app.entity.InvalidatedToken;

public interface InvalidatedRepository extends JpaRepository<InvalidatedToken, String> {}
