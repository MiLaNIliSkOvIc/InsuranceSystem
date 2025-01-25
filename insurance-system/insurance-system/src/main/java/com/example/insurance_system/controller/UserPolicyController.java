package com.example.insurance_system.controller;

import com.example.insurance_system.model.UserPolicy;
import com.example.insurance_system.service.UserPolicyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-policies")
public class UserPolicyController {

    private final UserPolicyService userPolicyService;

    public UserPolicyController(UserPolicyService userPolicyService) {
        this.userPolicyService = userPolicyService;
    }

    @GetMapping("/{userId}")
    public List<UserPolicy> getPoliciesByUserId(@PathVariable Long userId) {
        return userPolicyService.getPoliciesByUserId(userId);
    }

    @DeleteMapping("/{userId}/policy/{policyId}")
    public void deletePolicy(@PathVariable Long userId, @PathVariable Long policyId) {
        userPolicyService.deletePolicy(userId, policyId);
    }
}
