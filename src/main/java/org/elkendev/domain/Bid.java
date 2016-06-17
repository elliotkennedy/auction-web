package org.elkendev.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class Bid implements Comparable<Bid> {

    private final String user;
    private final BigDecimal amount;

    @JsonCreator
    public Bid(@JsonProperty("user") String user, @JsonProperty("amount") BigDecimal amount) {
        this.user = user;
        this.amount = amount;
    }

    public String getUser() {
        return user;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public int compareTo(Bid o) {
        return this.amount.compareTo(o.amount);
    }

    @Override
    public String toString() {
        return "Bid{" +
                "user='" + user + '\'' +
                ", amount=" + amount +
                '}';
    }
}
