package com.example.insurance_system.auth.Controller;



import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CsrfController {


    public CsrfToken csrf(CsrfToken csrfToken) {
        return csrfToken;
    }

}

