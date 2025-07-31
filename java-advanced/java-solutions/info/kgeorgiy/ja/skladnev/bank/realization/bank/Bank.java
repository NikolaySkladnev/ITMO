package info.kgeorgiy.ja.skladnev.bank.realization.bank;

import info.kgeorgiy.ja.skladnev.bank.realization.person.Person;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Bank extends Remote {
    /**
     * Creates a new account with specified identifier if it does not already exist.
     *
     * @param passport account id
     * @return created or existing account.
     */
    Person createPerson(String name, String surname, String passport) throws RemoteException;

    /**
     * Returns remote account by identifier.
     *
     * @param passport account id
     * @return account with specified identifier or {@code null} if such account does not exist.
     */
    Person getRemotePerson(String passport) throws RemoteException;

    /**
     * Returns local account by identifier.
     *
     * @param passport account id
     * @return account with specified identifier or {@code null} if such account does not exist.
     */
    Person getLocalPerson(String passport) throws RemoteException;
}
