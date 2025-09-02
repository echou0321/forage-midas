package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {

    private static final Logger log = LoggerFactory.getLogger(TransactionListener.class);

    private final TransactionService transactionService;

    public TransactionListener(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core")
    public void handle(Transaction tx) {
        boolean applied = transactionService.process(tx);
        if (applied) {
            log.info("Applied transaction: {}", tx);
        } else {
            log.info("Discarded invalid transaction: {}", tx);
        }
    }
}





