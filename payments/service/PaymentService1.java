package com.example.payments.service;

import com.example.payments.dto.Paymentdto;
import com.example.payments.model.Payment;
import com.example.payments.repository.PaymentRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.io.IOException; // Import IOException

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentService1 {
    @Autowired
    private PaymentRepository paymentRepository;

    public Payment initiatePayment(Paymentdto payment) {
        Payment p = Payment.builder()
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .username(payment.getUsername())
                .ponumber(payment.getPonumber())
                .invoicenumber(payment.getInvoicenumber())
                .targetBankAccount(payment.getTargetBankAccount())
                .tds(payment.getTds())
                .sourceBankAccount(payment.getSourceBankAccount())
                .status(payment.getStatus())
                .paymentdate(payment.getPaymentdate())
                .build();
        return paymentRepository.save(p);
    }

    public List<Payment> initiatePayments(List<Paymentdto> payments) {
        List<Payment> paymentList = payments.stream().map(payment -> Payment.builder()
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .username(payment.getUsername())
                .ponumber(payment.getPonumber())
                .invoicenumber(payment.getInvoicenumber())
                .targetBankAccount(payment.getTargetBankAccount())
                .tds(payment.getTds())
                .sourceBankAccount(payment.getSourceBankAccount())
                .status(payment.getStatus())
                .paymentdate(payment.getPaymentdate())
                .build()).collect(Collectors.toList());

        return paymentRepository.saveAll(paymentList);
    }

    public List<Payment> findPendingPayments() {
        return paymentRepository.findByStatus("PENDING");
    }

    public Double getTotalAmount() {
        return paymentRepository.findAll().stream().mapToDouble(Payment::getAmount).sum();
    }

    public Payment getByInvoiceNumber(String invoiceNumber) {
        return paymentRepository.findByInvoicenumber(invoiceNumber);
    }

    public Map<String, List<Payment>> getPaymentsByStatusAndDate(String paymentDate) {
        Map<String, List<Payment>> paymentsByStatus = new HashMap<>();
        paymentsByStatus.put("completed", paymentRepository.findByPaymentdateAndStatus(paymentDate, "PAID"));
        paymentsByStatus.put("pending", paymentRepository.findByPaymentdateAndStatus(paymentDate, "PENDING"));
        return paymentsByStatus;
    }

    public Payment editPayment(String id, Paymentdto paymentdto) {
        Optional<Payment> optionalPayment = paymentRepository.findById(id);
        if (optionalPayment.isPresent()) {
            Payment payment = optionalPayment.get();
            payment.setAmount(paymentdto.getAmount());
            payment.setCurrency(paymentdto.getCurrency());
            payment.setUsername(paymentdto.getUsername());
            payment.setPonumber(paymentdto.getPonumber());
            payment.setInvoicenumber(paymentdto.getInvoicenumber());
            payment.setTargetBankAccount(paymentdto.getTargetBankAccount());
            payment.setSourceBankAccount(paymentdto.getSourceBankAccount());
            payment.setTds(paymentdto.getTds());
            payment.setStatus(paymentdto.getStatus());
            payment.setPaymentdate(paymentdto.getPaymentdate());
            return paymentRepository.save(payment);
        }
        throw new RuntimeException("Payment not found");
    }

    public void deletePayment(String id) {
        paymentRepository.deleteById(id);
    }

    public byte[] generateInvoice(String id) throws JRException, IOException {
        System.out.println("Generating invoice for ID: " + id); // Debugging line
        Payment payment = paymentRepository.findByInvoicenumber(id);
//        if (optionalPayment.isEmpty()) {
//            System.out.println("Payment not found for ID: " + id); // Log not found
//            throw new RuntimeException("Payment not found");
//        }
//        Payment payment = optionalPayment.get();

        // Prepare parameters for the report
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("invoiceNumber", payment.getInvoicenumber());
        parameters.put("paymentDate", payment.getPaymentdate());
        parameters.put("description", "Invoice Description Here"); // or fetch from Payment if needed
        parameters.put("clientDetails", "Client Name, Address"); // Customize as needed
        parameters.put("bankAccountNumber", payment.getTargetBankAccount());
        parameters.put("amount", payment.getAmount());
        parameters.put("currency", payment.getCurrency());
        parameters.put("username", payment.getUsername());
        parameters.put("ponumber", payment.getPonumber());
        parameters.put("tds", payment.getTds());
        // Load JRXML file
        JasperReport jasperReport = JasperCompileManager.compileReport(new ClassPathResource("invoice_template.jrxml").getInputStream());

        // Use a stream to create a data source from the payment
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(List.of(payment));

        // Fill report with data
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        // Export to PDF
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
            return outputStream.toByteArray();
        }
    }
}
