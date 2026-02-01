package bankify;

public class Account {
        private long accountId;          // BIGINT AUTO_INCREMENT
        private long customerId;         // FK to customer
        private long agentId;            // Fk to employee
        private String accountNumber;    // VARCHAR(20) UNIQUE
        private double balance;          // DECIMAL(15,2)
        private String accountType;      // ENUM('USER','AGENT')
        private String status;           // ENUM('ACTIVE','SUSPENDED','CLOSED')
        private java.sql.Timestamp createdAt; // DATETIME

        // Constructors
        public Account() {}

        public Account(long accountId, long customerId, long agentId, String accountNumber,
                       double balance, String accountType, String status,
                       java.sql.Timestamp createdAt) {
            this.accountId = accountId;
            this.agentId = agentId;
            this.customerId = customerId;
            this.accountNumber = accountNumber;
            this.balance = balance;
            this.accountType = accountType;
            this.status = status;
            this.createdAt = createdAt;
        }

        // Getters and Setters
        public long getAccountId() { return accountId; }
        public void setAccountId(long accountId) { this.accountId = accountId; }

        public long getCustomerId() { return customerId; }
        public void setCustomerId(long customerId) { this.customerId = customerId; }

        public long getAgentId() { return agentId; }
        public void setAgentId(long agentId) { this.agentId = agentId; }

        public String getAccountNumber() { return accountNumber; }
        public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

        public double getBalance() { return balance; }
        public void setBalance(double balance) { this.balance = balance; }

        public String getAccountType() { return accountType; }
        public void setAccountType(String accountType) { this.accountType = accountType; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public java.sql.Timestamp getCreatedAt() { return createdAt; }
        public void setCreatedAt(java.sql.Timestamp createdAt) { this.createdAt = createdAt; }
   
}