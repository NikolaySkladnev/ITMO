package info.kgeorgiy.ja.skladnev.bank.realization.person;

import java.io.Serial;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LocalPerson extends AbstractPerson implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final ConcurrentMap<String, Account> accounts = new ConcurrentHashMap<>();

    public LocalPerson(final String name, final String surname, final String passport) {
        super(name, surname, passport);
    }

    public LocalPerson(final Person remote) throws RemoteException {
        super(remote.getName(), remote.getSurname(), remote.getPassport());
        copyAccounts(remote);
    }

    @Override
    public Account createAccount(final String subId) {
        Account account = accounts.get(subId);
        if (account == null) {
            account = new LocalAccount(passport + ":" + subId, 0);
            Account previous = accounts.putIfAbsent(subId, account);
            if (previous != null) {
                account = previous;
            }
        }
        return account;
    }

    @Override
    public Account getAccount(final String subId) {
        return accounts.get(subId);
    }

    public Map<String, Account> getAccounts() {
        return Map.copyOf(accounts);
    }

    private void copyAccounts(final Person remote) throws RemoteException {
        if (remote instanceof RemotePerson person) {
            for (Map.Entry<String, Account> e : person.getAccounts().entrySet()) {
                Account source = e.getValue();
                int amount = 0;
                try {
                    amount = source.getAmount();
                } catch (RemoteException _) {
                }
                accounts.put(e.getKey(), new LocalAccount(source.getId(), amount));
            }
        }
    }
}
