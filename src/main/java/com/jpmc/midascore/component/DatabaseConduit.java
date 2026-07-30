package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConduit {

    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;

    public DatabaseConduit(
            UserRepository userRepository,
            TransactionRecordRepository transactionRecordRepository) {

        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
    }

    // Used by UserPopulator to create the starting users.
    public void save(UserRecord userRecord) {
        userRepository.save(userRecord);
    }

    // Checks all three rules from the assignment.
    public boolean isValid(Transaction transaction) {
        UserRecord sender = queryUser(transaction.getSenderId());

        if (sender == null) {
            return false;
        }

        UserRecord recipient = queryUser(transaction.getRecipientId());

        if (recipient == null) {
            return false;
        }

        return sender.getBalance() >= transaction.getAmount();
    }

    // Saves a valid transaction and updates both balances.
    public void save(Transaction transaction) {
        UserRecord sender = queryUser(transaction.getSenderId());
        UserRecord recipient = queryUser(transaction.getRecipientId());

        TransactionRecord transactionRecord =
                new TransactionRecord(
                        sender,
                        recipient,
                        transaction.getAmount());

        transactionRecordRepository.save(transactionRecord);

        sender.setBalance(
                sender.getBalance() - transaction.getAmount());

        recipient.setBalance(
                recipient.getBalance() + transaction.getAmount());

        userRepository.save(sender);
        userRepository.save(recipient);
    }

    public UserRecord queryUser(long userId) {
        return userRepository.findById(userId);
    }

    public float queryUserBalance(long userId) {
        UserRecord userRecord = queryUser(userId);

        if (userRecord == null) {
            return 0;
        }

        return userRecord.getBalance();
    }
}