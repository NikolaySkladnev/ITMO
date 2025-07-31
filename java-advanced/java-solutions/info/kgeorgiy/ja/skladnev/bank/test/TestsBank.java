package info.kgeorgiy.ja.skladnev.bank.test;

import info.kgeorgiy.ja.skladnev.bank.client.Client;
import info.kgeorgiy.ja.skladnev.bank.realization.bank.Bank;
import info.kgeorgiy.ja.skladnev.bank.realization.person.Account;
import info.kgeorgiy.ja.skladnev.bank.realization.person.LocalAccount;
import info.kgeorgiy.ja.skladnev.bank.realization.person.LocalPerson;
import info.kgeorgiy.ja.skladnev.bank.realization.person.Person;
import info.kgeorgiy.ja.skladnev.bank.server.Server;
import info.kgeorgiy.ja.skladnev.bank.web.BankWebServer;
import org.junit.jupiter.api.*;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestsBank {
    private static final int REGISTRY_PORT = 1099;
    private static final int WEB_PORT = 8088;
    private static Bank bank;

    @BeforeAll
    static void startServer() throws Exception {
        try {
            LocateRegistry.createRegistry(REGISTRY_PORT);
        } catch (RemoteException _) {
        }
        Server.main();
        bank = (Bank) Naming.lookup("//localhost/bank");
        BankWebServer.main(new String[]{String.valueOf(WEB_PORT)});
    }

    @Test
    void testCreateAndFindRemotePerson() throws RemoteException {
        Person p = bank.createPerson("John", "Doe", "123");
        assertEquals("123", p.getPassport());
        assertEquals("John", p.getName());
        assertEquals("Doe", p.getSurname());
        assertEquals(p.getPassport(), bank.getRemotePerson("123").getPassport());
    }

    @Test
    void testAccountIdFormat() throws RemoteException {
        Person p = bank.createPerson("Kate", "Walker", "555");
        assertEquals("555:1", p.createAccount("1").getId());
    }

    @Test
    void testLocalSnapshot() throws RemoteException {
        bank.createPerson("Sludge", "Life", "321");
        Person remote = bank.getRemotePerson("321");
        remote.createAccount("BigMud");
        remote.getAccount("BigMud").setAmount(42);
        Person local = bank.getLocalPerson("321");
        assertEquals(42, local.getAccount("BigMud").getAmount());
        remote.getAccount("BigMud").setAmount(84);
        assertEquals(42, local.getAccount("BigMud").getAmount());
    }

    @Test
    void testSharedRemoteAccount() throws RemoteException {
        Person p1 = bank.createPerson("Doom", "Slayer", "666");
        p1.createAccount("1").setAmount(10);
        Person p2 = bank.getRemotePerson("666");
        assertEquals(10, p2.getAccount("1").getAmount());
        assertEquals("Doom", p2.getName());
        assertEquals("Slayer", p2.getSurname());
        assertEquals("666", p2.getPassport());
    }

    @Test
    void testClientMainArgs() throws Exception {
        Client.main(new String[]{"X", "Y", "888", "reallyCoolAccount", "5"});
        Person p = bank.getRemotePerson("888");
        assertNotNull(p, "RemotePerson must be not null");
        Account acc = p.getAccount("reallyCoolAccount");
        assertNotNull(acc, "reallyCoolAccount must be not null");
        assertEquals(5, acc.getAmount(), "Balance should be delta");
    }

    @Test
    void testClientMainDefaults() throws Exception {
        Client.main();
        Person p = bank.getRemotePerson("406666");
        assertNotNull(p, "Default Person must be not null");
        Account acc = p.getAccount("1");
        assertNotNull(acc, "Default account must be 1");
        assertEquals(100, acc.getAmount(), "Balance should be 100");
    }

    @Test
    void testConcurrentAccountUpdates() throws Exception {
        Person person = bank.createPerson("Multi", "Thread", "777");
        final Account account = person.createAccount("shared");

        final int threads = 100;
        Thread[] workers = new Thread[threads];

        for (int i = 0; i < threads; i++) {
            final int delta = i;
            workers[i] = new Thread(() -> {
                try {
                    synchronized (account) {
                        int current = account.getAmount();
                        account.setAmount(current + delta);
                    }
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            });
            workers[i].start();
        }

        for (Thread t : workers) {
            t.join();
        }

        int expected = (threads - 1) * threads / 2;
        assertEquals(expected, account.getAmount(), "Balance after concurrent updates must equal sum");
    }

    @Test
    void testLocalAccountIsolation() throws RemoteException {
        bank.createPerson("Lara", "Croft", "1000");
        Person remote = bank.getRemotePerson("1000");
        remote.createAccount("a1").setAmount(50);
        Person local = bank.getLocalPerson("1000");
        assertEquals(50, local.getAccount("a1").getAmount());
        remote.getAccount("a1").setAmount(200);
        assertEquals(50, local.getAccount("a1").getAmount());
    }

    @Test
    void testMultipleAccountsForSamePerson() throws RemoteException {
        Person p = bank.createPerson("Multi", "Acc", "2000");

        Account a1 = p.createAccount("main");
        Account a2 = p.createAccount("savings");
        a1.setAmount(100);
        a2.setAmount(250);

        assertEquals(100, p.getAccount("main").getAmount());
        assertEquals(250, p.getAccount("savings").getAmount());

        Person local = bank.getLocalPerson("2000");
        assertEquals(100, local.getAccount("main").getAmount());
        assertEquals(250, local.getAccount("savings").getAmount());
    }

    @Test
    void testGetAccountsMapCorrectness() throws RemoteException {
        Person remote = bank.createPerson("Map", "Test", "3000");
        remote.createAccount("one").setAmount(10);
        remote.createAccount("two").setAmount(20);

        Person local = bank.getLocalPerson("3000");
        assertNotNull(local, "LocalPerson must not be null");

        Map<String, Account> accounts = ((LocalPerson) local).getAccounts();

        assertEquals(2, accounts.size(), "Should be two accounts");
        assertTrue(accounts.containsKey("one"), "Account 'one' should exist");
        assertTrue(accounts.containsKey("two"), "Account 'two' should exist");
        assertEquals(10, accounts.get("one").getAmount(), "Amount in 'one' should be 10");
        assertEquals(20, accounts.get("two").getAmount(), "Amount in 'two' should be 20");
    }

    @Test
    void testCreateAccountWhenAlreadyExists() throws RemoteException {
        Person local = new LocalPerson("C", "D", "7000");
        Account a1 = local.createAccount("id");
        a1.setAmount(50);
        Account a2 = local.createAccount("id");
        assertSame(a1, a2, "createAccount should return existing account if already present");
        assertEquals(50, a2.getAmount());
    }

    @Test
    void testGetNonexistentAccount() throws RemoteException {
        Person local = new LocalPerson("Ghost", "Person", "8000");
        assertNull(local.getAccount("missing"), "getAccount should return null for unknown subId");
    }

    @Test
    void testGetAccountsReturnsCopy() throws RemoteException {
        LocalPerson local = new LocalPerson("E", "F", "9000");
        local.createAccount("x").setAmount(12);
        local.createAccount("y").setAmount(34);

        Map<String, Account> accounts = local.getAccounts();
        assertEquals(2, accounts.size());
        assertTrue(accounts.containsKey("x"));
        assertTrue(accounts.containsKey("y"));
    }

    @Test
    void testEmptyLocalPerson() throws RemoteException {
        Person p = new LocalPerson("Bare", "Person", "5555");
        assertEquals("Bare", p.getName());
        assertNull(p.getAccount("nothing"));
    }

    @Test
    void testGetId() {
        LocalAccount acc = new LocalAccount("0");
        assertEquals("0", acc.getId());
    }

    @Test
    void testDefaultConstructorSetsZero() {
        LocalAccount acc = new LocalAccount("1");
        assertEquals(0, acc.getAmount());
    }

    @Test
    void testConstructorWithAmount() {
        LocalAccount acc = new LocalAccount("2", 123);
        assertEquals(123, acc.getAmount());
    }

    @Test
    void testSetAndGetAmount() {
        LocalAccount acc = new LocalAccount("3");
        acc.setAmount(999);
        assertEquals(999, acc.getAmount());
    }

    @Test
    void testSetNegativeThrows() {
        LocalAccount acc = new LocalAccount("4");
        assertThrows(IllegalArgumentException.class, () -> acc.setAmount(-5));
    }
}
