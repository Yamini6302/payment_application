package com.example.payments.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "payments")
@Builder
public class Payment {
    @Id
    private String id;
    private Double amount;
    private String currency;
    private String username;
    private String ponumber;
    private String invoicenumber;
    private String targetBankAccount;
    private String sourceBankAccount;
    private int tds;
    private String status;
    private String paymentdate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPonumber() {
        return ponumber;
    }

    public void setPonumber(String ponumber) {
        this.ponumber = ponumber;
    }

    public String getInvoicenumber() {
        return invoicenumber;
    }

    public void setInvoicenumber(String invoicenumber) {
        this.invoicenumber = invoicenumber;
    }

    public String getTargetBankAccount() {
        return targetBankAccount;
    }

    public void setTargetBankAccount(String targetBankAccount) {
        this.targetBankAccount = targetBankAccount;
    }

    public String getSourceBankAccount() {
        return sourceBankAccount;
    }

    public void setSourceBankAccount(String sourceBankAccount) {
        this.sourceBankAccount = sourceBankAccount;
    }

    public int getTds() {
        return tds;
    }

    public void setTds(int tds) {
        this.tds = tds;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentdate() {
        return paymentdate;
    }

    public void setPaymentdate(String paymentdate) {
        this.paymentdate = paymentdate;
    }

    public Double calculateNetAmount() {
        return amount - (tds / 100.0 * amount); // Assuming TDS is a percentage
    }
}
