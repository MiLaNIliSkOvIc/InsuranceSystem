package com.example.insurance_system.controller;



import com.example.insurance_system.model.*;
import com.example.insurance_system.repository.UserRepository;
import com.example.insurance_system.service.PaymentService;
import com.example.insurance_system.service.PolicyService;
import com.example.insurance_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/api/payment")
public class StripeController {
    @Autowired
    private UserService userService;
    @Autowired
    private PolicyService policyService;

    private final PaymentService paymentService;

    @Autowired
    public StripeController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/process")
    public Purchase processPayment(@RequestBody Payment paymentRequest) throws Exception {
        Optional<User> user = userService.findById(paymentRequest.getUserId());
        Optional<Policy> policy = policyService.findById(paymentRequest.getPolicyId());
        Purchase a = paymentService.processPayment(user.get(), paymentRequest.getAmount(), paymentRequest.getPolicyName(), paymentRequest.getPaymentMethodId(),policy.get());
        if (a == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Payment processing failed.");
        }

        return a;
    }
}
