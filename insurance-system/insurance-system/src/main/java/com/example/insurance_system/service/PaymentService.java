package com.example.insurance_system.service;

import com.example.insurance_system.model.*;
import com.example.insurance_system.repository.PurchaseRepository;
import com.example.insurance_system.auth.service.EmailService;
import com.example.insurance_system.repository.SessionRepository;
import com.example.insurance_system.repository.UserPolicyRepository;
import com.example.insurance_system.security.MaliciousRequestFilter;
import com.example.insurance_system.security.SIEMComponent;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    private final PurchaseRepository purchaseRepository;
    private final UserPolicyRepository userPolicyRepository;

    private final SessionRepository sessionRepository;

    @Autowired
    private SIEMComponent siemComponent;


    public PaymentService(PurchaseRepository purchaseRepository,UserPolicyRepository userPolicyRepository,SessionRepository sessionRepository) {
        this.purchaseRepository = purchaseRepository;
        this.userPolicyRepository = userPolicyRepository;
        this.sessionRepository = sessionRepository;
        Stripe.apiKey = stripeApiKey;
    }

    public Purchase processPayment(User user, Double amount, String policyName, String paymentMethodId, Policy policy) throws Exception {

        if (isPotentiallyMalicious(amount)) {
            String warningMessage = String.format("Potentially malicious request detected: URI=%s Amount=%s", user.getUsername(), amount);
            siemComponent.logSecurityEvent("Malicious Request", warningMessage);

            List<Session> userSessions = sessionRepository.findByUserId(user.getId());
            for (Session session : userSessions) {
                session.setActive(false);
            }
            sessionRepository.saveAll(userSessions);
            return null;
        }

        Stripe.apiKey = stripeApiKey;
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount((long) (amount * 100))
                .setCurrency("usd")
                .setPaymentMethod(paymentMethodId)
                .setConfirm(true)
                .setReturnUrl("https://your-domain.com/payment-success")
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);


        Purchase purchase = new Purchase();
        purchase.setUser(user);
        purchase.setAmount(amount);

        purchase.setPolicy(policy);
        purchase.setTransactionId(paymentIntent.getId());
        purchase.setStatus(Purchase.Status.pending);
        purchase = purchaseRepository.save(purchase);

        UserPolicy userPolicy = new UserPolicy();
        userPolicy.setPolicy(policy);
        userPolicy.setUser(user);
        userPolicy.setPurchaseDate(LocalDate.now().atStartOfDay());
        userPolicy = userPolicyRepository.save(userPolicy);

        if ("succeeded".equals(paymentIntent.getStatus())) {
            purchase.setStatus(Purchase.Status.completed);
            purchaseRepository.save(purchase);

            // Generate PDF and send email
            byte[] pdfData = generatePdfForPolicy(policyName);
            EmailService.sendEmailWithAttachment(
                    user.getEmail(),
                    "Your Policy Purchase",
                    "Thank you for your purchase of the " + policyName + " policy.",
                    pdfData,
                    policyName + "_policy.pdf"
            );
        } else {
            purchase.setStatus(Purchase.Status.failed);
            purchaseRepository.save(purchase);
        }

        return purchase;
    }

    private byte[] generatePdfForPolicy(String policyName) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();


            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Font contentFont = new Font(Font.FontFamily.HELVETICA, 12);


            Paragraph title = new Paragraph("Insurance Policy Details", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);


            document.add(new Paragraph("\n"));


            Paragraph policyDetails = new Paragraph(
                    "Policy Name: " + policyName + "\n" +
                            "Date of Issue: " + LocalDate.now() + "\n" +
                            "\nThank you for purchasing the " + policyName + " policy. We value your trust.",
                    contentFont
            );
            document.add(policyDetails);

        } catch (DocumentException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while generating PDF for policy: " + policyName, e);
        } finally {
            document.close();
        }

        return outputStream.toByteArray();
    }

    private boolean isPotentiallyMalicious(Double amount) {
        try {
            if (amount != null) {

                return amount > 1000000;
            }
        } catch (NumberFormatException e) {
            return true;
        }
        return false;
    }

}
