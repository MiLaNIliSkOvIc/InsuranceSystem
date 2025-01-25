package com.example.insurance_system.controller;

import com.example.insurance_system.model.Policy;
import com.example.insurance_system.model.PolicyRequestDTO;
import com.example.insurance_system.service.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {
    @Autowired
    private PolicyService policyService;

    @GetMapping
    public List<Policy> getAllPolicies() {
        return policyService.findAll();
    }

    @PostMapping
    public ResponseEntity<Policy> createPolicy(@RequestBody PolicyRequestDTO policyRequestDTO) {
        Policy createdPolicy = policyService.createPolicy(policyRequestDTO);
        return ResponseEntity.ok(createdPolicy);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Policy> getPolicyById(@PathVariable Long id) {
        Optional<Policy> policy = policyService.findById(id);
        return policy.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/createdBy/{userId}")
    public List<Policy> getPoliciesByCreatedById(@PathVariable Long userId) {
        return policyService.findByCreatedById(userId);
    }
}