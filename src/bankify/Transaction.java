package bankify;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private long transaction_id;
    private long account_id;
    private long employee_id;
    private String transaction_type;
    private double amount;
    private String status;
    private java.sql.Timestamp transaction_at;
    private String description;

    public Transaction() {}

    public Transaction(long transaction_id, long account_id, long employee_id, String transaction_type, double amount
            , String status, Timestamp transaction_at, String description) {
        this.transaction_id = transaction_id;
        this.account_id = account_id;
        this.employee_id = employee_id;
        this.transaction_type = transaction_type;
        this.amount = amount;
        this.status = status;
        this.transaction_at = transaction_at;
        this.description = description;
    }

    // Transaction ID
    public long getTransactionId() { return transaction_id; }
    public void setTransactionId(long transaction_id) { this.transaction_id = transaction_id; }

    // Account ID
    public long getAccountId() { return account_id; }
    public void setAccountId(long account_id) { this.account_id = account_id; }

    // Employee ID
    public long getEmployeeId() { return employee_id; }
    public void setEmployeeId(long employee_id) { this.employee_id = employee_id; }

    // Transaction Type
    public String getTransactionType() { return transaction_type; }
    public void setTransactionType(String transaction_type) { this.transaction_type = transaction_type; }

    // Amount
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    // Status
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // Transaction At
    public java.sql.Timestamp getTransactionAt() { return transaction_at; }
    public void setTransactionAt(java.sql.Timestamp transaction_at) { this.transaction_at = transaction_at; }

    // Description
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getFormattedDate() {
        if (this.transaction_at == null) return "";

        // Convert to LocalDateTime while specifying the TimeZone offset
        // This stops Java from adding or subtracting hours based on your PC settings
        LocalDateTime ldt = this.transaction_at.toInstant()
                .atZone(java.time.ZoneId.of("UTC")) // Treat DB time as UTC
                .toLocalDateTime();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");

        return ldt.format(formatter);
    }
}