package com.example.insurance_system.repository;

import com.example.insurance_system.model.Policy;
import com.example.insurance_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PolicyRepository extends JpaRepository<Policy, Long> {
    Optional<Policy> findById(Long id);
    List<Policy> findByCreatedById(Long createdById);
}
