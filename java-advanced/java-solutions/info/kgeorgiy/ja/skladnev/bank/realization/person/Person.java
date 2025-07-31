package info.kgeorgiy.ja.skladnev.bank.realization.person;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Person extends Remote {
    String getName() throws RemoteException;
    String getSurname() throws RemoteException;
    String getPassport() throws RemoteException;
    Account createAccount(String subId) throws RemoteException;
    Account getAccount(String subId) throws RemoteException;
}
