package bankify;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transfer {
    private long transfer_id;
    private long from_id;
    private long to_id;
    private double amount;
    private String description;
    private String status;
    private Timestamp transfer_at;

    public Transfer() {}

    public Transfer(long transfer_id, long from_id, long to_id, double amount, String description, Timestamp transfer_at) {
        this.transfer_id = transfer_id;
        this.from_id = from_id;
        this.to_id = to_id;
        this.amount = amount;
        this.description = description;
        this.transfer_at = transfer_at;
    }

    public long getTransferId() { return transfer_id; }
    public void setTransferId(long transfer_id) { this.transfer_id = transfer_id; }

    public long getFromId() { return from_id; }
    public void setFromId(long from_id) { this.from_id = from_id; }

    public long getToId() { return to_id; }
    public void setToId(long to_id) { this.to_id = to_id; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getTransferAt() { return transfer_at; }
    public void setTransferAt(Timestamp transfer_at) { this.transfer_at = transfer_at; }

    public String getFormattedDate() {
        if (this.transfer_at == null) return "";

        // Convert to LocalDateTime while specifying the TimeZone offset
        // This stops Java from adding or subtracting hours based on your PC settings
        LocalDateTime ldt = this.transfer_at.toInstant()
                .atZone(java.time.ZoneId.of("UTC")) // Treat DB time as UTC
                .toLocalDateTime();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");

        return ldt.format(formatter);
    }
}