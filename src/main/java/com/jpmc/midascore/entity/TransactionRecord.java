package com.jpmc.midascore.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "transaction_records")
public class TransactionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many transactions can reference the same sender UserRecord
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private UserRecord sender;

    // Many transactions can reference the same recipient UserRecord
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id")
    private UserRecord recipient;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected TransactionRecord() {}

    public TransactionRecord(UserRecord sender, UserRecord recipient, BigDecimal amount) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
    }

    public Long getId() { return id; }
    public UserRecord getSender() { return sender; }
    public UserRecord getRecipient() { return recipient; }
    public BigDecimal getAmount() { return amount; }
    public Instant getCreatedAt() { return createdAt; }
}


