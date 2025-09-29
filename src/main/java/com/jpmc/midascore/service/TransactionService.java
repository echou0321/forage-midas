package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

    private final UserRepository userRepository;
    private final TransactionRecordRepository txRepo;

    public TransactionService(UserRepository userRepository, TransactionRecordRepository txRepo) {
        this.userRepository = userRepository;
        this.txRepo = txRepo;
    }

    /**
     * Validate and, if valid, atomically apply the transaction:
     *  - both users must exist
     *  - sender balance >= amount
     *  - persist updated balances and a TransactionRecord
     *
     * @return true if applied, false if discarded
     */
    @Transactional
    public boolean process(Transaction incoming) {
        // look up users by id (use Optional from repository)
        java.util.Optional<UserRecord> senderOpt = userRepository.findById(incoming.getSenderId());
        java.util.Optional<UserRecord> recipientOpt = userRepository.findById(incoming.getRecipientId());

        if (senderOpt.isEmpty() || recipientOpt.isEmpty()) {
            return false; // invalid ids
        }

        UserRecord sender = senderOpt.get();
        UserRecord recipient = recipientOpt.get();

        java.math.BigDecimal amount = incoming.getAmount();
        if (sender.getBalance().compareTo(amount) < 0) {
            return false; // insufficient funds
        }

        // apply balances
        sender.setBalance(sender.getBalance().subtract(amount));
        recipient.setBalance(recipient.getBalance().add(amount));

        // persist user updates
        userRepository.save(sender);
        userRepository.save(recipient);

        // record the transaction
        TransactionRecord record = new TransactionRecord(sender, recipient, amount);
        txRepo.save(record);

        return true;
    }
}

