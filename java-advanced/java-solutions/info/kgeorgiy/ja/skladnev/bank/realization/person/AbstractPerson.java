package info.kgeorgiy.ja.skladnev.bank.realization.person;

import java.io.Serial;
import java.io.Serializable;
import java.rmi.RemoteException;

public abstract class AbstractPerson implements Person, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    protected final String name;
    protected final String surname;
    protected final String passport;

    protected AbstractPerson(final String name, final String surname, final String passport) {
        this.name     = name;
        this.surname  = surname;
        this.passport = passport;
    }

    @Override
    public String getName() throws RemoteException {
        return name;
    }

    @Override
    public String getSurname() throws RemoteException {
        return surname;
    }

    @Override
    public String getPassport() throws RemoteException {
        return passport;
    }

    @Override
    public abstract Account createAccount(String subId) throws RemoteException;

    @Override
    public abstract Account getAccount(String subId) throws RemoteException;
}
