package info.kgeorgiy.ja.skladnev.bank.realization.bank;

import info.kgeorgiy.ja.skladnev.bank.realization.person.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RemoteBank implements Bank {
    private final int port;

    private final ConcurrentMap<String, RemotePerson> persons = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Account> accounts = new ConcurrentHashMap<>();

    public RemoteBank(final int port) {
        this.port = port;
    }

    @Override
    public Person createPerson(String name, String surname, String passport) throws RemoteException {
        System.out.println("Creating person " + passport);
        RemotePerson newPerson = new RemotePerson(name, surname, passport, port, this);
        if (persons.putIfAbsent(passport, newPerson) != null) {
            return newPerson;
        } else {
            return persons.get(passport);
        }
    }

    @Override
    public Person getRemotePerson(String passport) throws RemoteException {
        System.out.println("Retrieving remote person " + passport);
        return persons.get(passport);
    }

    @Override
    public Person getLocalPerson(String passport) throws RemoteException {
        RemotePerson person = persons.get(passport);
        if (person != null) {
            return new LocalPerson(person);
        }
        return null;
    }

    public Account createAccount(String passport, String subId) throws RemoteException {
        String accountId = passport + ":" + subId;
        Account account = new RemoteAccount(accountId);
        if (accounts.putIfAbsent(accountId, account) == null) {
            UnicastRemoteObject.exportObject(account, port);
        }
        return accounts.get(accountId);
    }

    public Account getAccount(String passport, String subId) {
        return accounts.get(passport + ":" + subId);
    }


    public Map<String, Account> getAccountsForPassport(final String passport) {
        final String prefix = passport + ":";
        final Map<String, Account> result = new HashMap<>();

        for (Map.Entry<String, Account> entry : accounts.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(prefix)) {
                String subId = key.substring(prefix.length());
                result.put(subId, entry.getValue());
            }
        }
        return Collections.unmodifiableMap(result);
    }
}
