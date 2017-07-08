package com.barthezzko.transfer.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.barthezzko.domain.Account;
import com.barthezzko.domain.Client;
import com.google.inject.Inject;

/**
 * 
 * 
 * @author barthezzko
 *
 */
public class DataStorageImpl implements DataStorage {
	
	private final KeyGenerator keyGen;
	
	@Inject
	public DataStorageImpl(KeyGenerator keyGen) {
		this.keyGen = keyGen;
	}
	
	private final Map<String, Client> clientMap = new HashMap<>();
	
	@Override
	public Client getClient(String clientId) {
		return clientMap.get(clientId);
	}

	@Override
	public Account getAccount(String accountId) {
		Client client = clientMap.get(keyGen.extractClientFromAccountId(accountId));
		Objects.requireNonNull(client, "client for account [" + accountId + "] doesn't exist");
		return client.getAccounts().get(accountId);
	}

	@Override
	public void addClient(Client client) {
		clientMap.put(client.getClientId(), client);
	}

	@Override
	public void addAccount(Client client, Account account) {
		client.addAccount(account);
	}

	@Override
	public Collection<Client> getAllClients() {
		// TODO Auto-generated method stub
		return null;
	}

}
