package com.barthezzko.transfer.service;

import java.util.Collection;

import com.barthezzko.domain.Account;
import com.barthezzko.domain.Client;

public interface DataStorage {

	Client getClient(String clientId);
	
	Account getAccount(String accountId);
	
	void addClient(Client client);
	
	void addAccount(Client client, Account account);
	
	Collection<Client> getAllClients();
}
