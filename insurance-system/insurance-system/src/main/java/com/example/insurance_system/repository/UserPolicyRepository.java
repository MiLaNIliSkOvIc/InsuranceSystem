package com.example.insurance_system.repository;

import com.example.insurance_system.model.UserPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPolicyRepository extends JpaRepository<UserPolicy, Long> {
    List<UserPolicy> findByUserId(Long userId);
    Optional<UserPolicy> findByUserIdAndPolicyId(Long userId, Long policyId);
}
