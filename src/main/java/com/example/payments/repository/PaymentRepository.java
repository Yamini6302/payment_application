package com.example.payments.repository;

import com.example.payments.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PaymentRepository extends MongoRepository<Payment, String> {
    List<Payment> findByStatus(String status);

    // Find all payments to calculate total amount
    List<Payment> findAll();


    Payment findByInvoicenumber(String invoiceNumber);

    List<Payment> findByPaymentdateAndStatus(String paymentDate, String status);
}