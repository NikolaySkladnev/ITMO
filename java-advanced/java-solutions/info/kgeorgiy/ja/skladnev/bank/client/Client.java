package info.kgeorgiy.ja.skladnev.bank.client;

import info.kgeorgiy.ja.skladnev.bank.realization.person.Account;
import info.kgeorgiy.ja.skladnev.bank.realization.bank.Bank;
import info.kgeorgiy.ja.skladnev.bank.realization.person.Person;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public final class Client {
    /**
     * Utility class.
     */
    private Client() {

    }


    public static void main(final String... args) throws RemoteException {
        final Bank bank;
        try {
            bank = (Bank) Naming.lookup("//localhost/bank");
        } catch (final NotBoundException e) {
            System.out.println("Bank is not bound");
            return;
        } catch (final MalformedURLException e) {
            System.out.println("Bank URL is invalid");
            return;
        }

        final String name;
        final String surname;
        final String passport;
        final String subId;
        final int delta;
        if (args.length == 5) {
            name = args[0];
            surname = args[1];
            passport = args[2];
            subId = args[3];
            delta = Integer.parseInt(args[4]);
        } else {
            System.err.println("Usage: <name> <surname> <passport> <subId> <delta>");
            name = "Michael";
            surname = "Reeves";
            passport = "406666";
            subId = "1";
            delta = 100;
        }


        Person person = bank.getRemotePerson(passport);
        if (person == null) {
            System.out.println("Creating account");
            person = bank.createPerson(name, surname, passport);
        } else {
            System.out.println("Account already exists");
        }

        Account account = person.getAccount(subId);
        if (account == null) {
            System.out.println("Creating sub-account " + subId);
            account = person.createAccount(subId);
        }

        System.out.println("Account id: " + account.getId());
        System.out.println("Money: " + account.getAmount());
        System.out.println("Adding money");

        account.setAmount(account.getAmount() + delta);
        System.out.println("Money: " + account.getAmount());
    }
}
