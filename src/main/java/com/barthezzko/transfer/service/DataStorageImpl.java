package com.barthezzko.transfer.service;

import java.awt.DefaultFocusTraversalPolicy;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.log4j.Logger;

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
	private Logger logger = Logger.getLogger(DataStorageImpl.class);
	
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
		Account acc = client.getAccounts().get(accountId);
		logger.info(acc);
		return acc;
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
		return clientMap.values();
	}

}
