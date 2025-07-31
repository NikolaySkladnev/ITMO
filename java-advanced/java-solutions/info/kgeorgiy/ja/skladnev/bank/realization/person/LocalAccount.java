package info.kgeorgiy.ja.skladnev.bank.realization.person;

import java.io.Serial;
import java.io.Serializable;

public class LocalAccount implements Serializable, Account {
    private final String id;
    private int amount;

    @Serial
    private static final long serialVersionUID = 1L;
    public LocalAccount(final String id, int amount) {
        this.id = id;
        this.amount = amount;
    }

    public LocalAccount(String id) {
        this.id = id;
        this.amount = 0;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public synchronized int getAmount() {
        System.out.println("Getting amount of money for account " + id);
        return amount;
    }

    @Override
    public synchronized void setAmount(final int amount) {
        System.out.println("Setting amount of money for account " + id);
        if (amount < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }
        this.amount = amount;
    }



}
