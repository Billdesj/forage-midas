package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionHandler {

    private final DatabaseConduit databaseConduit;

    public TransactionHandler(DatabaseConduit databaseConduit) {
        this.databaseConduit = databaseConduit;
    }

    public void handleTransaction(Transaction transaction) {
        if (databaseConduit.isValid(transaction)) {
            databaseConduit.save(transaction);
        }
    }
}