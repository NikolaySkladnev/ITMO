package info.kgeorgiy.ja.skladnev.bank.realization.person;

import info.kgeorgiy.ja.skladnev.bank.realization.bank.RemoteBank;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

public class RemotePerson extends AbstractPerson {

    private final RemoteBank bank;

    public RemotePerson(final String name, final String surname, final String passport, final int port, final RemoteBank bank) throws RemoteException {
        super(name, surname, passport);
        this.bank = bank;
        UnicastRemoteObject.exportObject(this, port);
    }

    @Override
    public Account createAccount(final String subId) throws RemoteException {
        return bank.createAccount(passport, subId);
    }

    @Override
    public Account getAccount(final String subId) throws RemoteException {
        return bank.getAccount(passport, subId);
    }

    public Map<String, Account> getAccounts() {
        return bank.getAccountsForPassport(passport);
    }
}
