package com.example.insurance_system.service;

import com.example.insurance_system.model.UserPolicy;
import com.example.insurance_system.repository.UserPolicyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserPolicyService {

    private final UserPolicyRepository userPolicyRepository;

    public UserPolicyService(UserPolicyRepository userPolicyRepository) {
        this.userPolicyRepository = userPolicyRepository;
    }

    public List<UserPolicy> getPoliciesByUserId(Long userId) {
        return userPolicyRepository.findByUserId(userId);
    }

    public void deletePolicy(Long userId, Long policyId) {

        Optional<UserPolicy> userPolicy = userPolicyRepository.findByUserIdAndPolicyId(userId, policyId);

        if (userPolicy.isPresent()) {
            userPolicyRepository.delete(userPolicy.get());
        } else {
            throw new IllegalArgumentException("Policy with ID " + policyId + " for User with ID " + userId + " not found.");
        }
    }
}
