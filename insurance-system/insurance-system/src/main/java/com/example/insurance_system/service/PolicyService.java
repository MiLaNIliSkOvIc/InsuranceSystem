package com.example.insurance_system.service;

import com.example.insurance_system.model.Policy;
import com.example.insurance_system.model.PolicyRequestDTO;
import com.example.insurance_system.model.User;
import com.example.insurance_system.repository.PolicyRepository;
import com.example.insurance_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PolicyService {
    @Autowired
    private PolicyRepository policyRepository;
    @Autowired
    private UserRepository userRepository;

    public List<Policy> findAll() {
        return policyRepository.findAll();
    }

    public Policy save(Policy policy) {
        return policyRepository.save(policy);
    }

    public Optional<Policy> findById(Long id) {
        return policyRepository.findById(id);
    }
    public List<Policy> findByCreatedById(Long createdById) {
        return policyRepository.findByCreatedById(createdById);
    }
    public Policy createPolicy(PolicyRequestDTO policyRequestDTO) {

        User user = userRepository.findById(policyRequestDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Policy policy = new Policy();
        policy.setName(policyRequestDTO.getName());
        policy.setDescription(policyRequestDTO.getDescription());
        policy.setPrice(policyRequestDTO.getPrice());
        policy.setType(policyRequestDTO.getType());
        policy.setIcon(policyRequestDTO.getIcon());
        policy.setCreatedBy(user);

        return policyRepository.save(policy);
    }
}