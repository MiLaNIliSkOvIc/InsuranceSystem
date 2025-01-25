package com.example.insurance_system.service;

import com.example.insurance_system.model.Purchase;
import com.example.insurance_system.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PurchaseService {
    @Autowired
    private PurchaseRepository purchaseRepository;

    public List<Purchase> findAll() {
        return purchaseRepository.findAll();
    }

    public Purchase save(Purchase purchase) {
        return purchaseRepository.save(purchase);
    }
}
