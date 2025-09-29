package com.jpmc.midascore.entity;

// Note: This class is intentionally left as a plain POJO to avoid conflicting JPA mappings
// with the actively used UserRecord entity. If needed in the future, move it to a different
// package or re-enable JPA annotations with a clear migration path.

import java.math.BigDecimal;
import java.util.Objects;

public class User {

    private Long id;
    private String username;
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

