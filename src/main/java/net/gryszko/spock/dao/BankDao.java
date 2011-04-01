package net.gryszko.spock.dao;

import net.gryszko.spock.model.Bank;

import java.util.List;

public interface BankDao {
	Bank findByName(String name);

    void save(Bank bank);
}
