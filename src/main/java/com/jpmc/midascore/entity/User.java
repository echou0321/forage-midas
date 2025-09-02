package com.jpmc.midascore.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User {

    @Id
    private Long id; // must match senderId/recipientId from Kafka

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance = BigDecimal.ZERO;

    protected User() {
    }

    public User(Long id, String username, BigDecimal balance) {
        this.id = id;
        this.username = username;
        this.balance = balance;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public BigDecimal getBalance() { return balance; }

    public void setUsername(String username) { this.username = username; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}

