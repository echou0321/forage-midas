package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {

    @Value("${general.kafka-topic}")
    private String topicName;

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core")
    public void handle(Transaction tx) {
        System.out.println("Listener GOT one; topic=" + topicName + ", amount=" + tx.getAmount());
        int debugNoop = 0; // <-- put breakpoint here
    }
}



