package co.paikama.stats.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Instant;

public class Transaction {

    public static final int STALE_THRESHOLD = 60 * 1000;

    private double amount;
    private long timestamp;

    public Transaction() {
    }

    public Transaction(double amount, long timestamp) {
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long epochSecond() {
        return Instant.ofEpochMilli(timestamp).getEpochSecond();
    }

    @JsonIgnore
    public boolean isValid() {
        return timestamp > Instant.now().toEpochMilli() - STALE_THRESHOLD;
    }

}
