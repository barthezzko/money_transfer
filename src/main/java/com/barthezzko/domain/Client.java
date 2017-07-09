package com.barthezzko.domain;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.annotations.Expose;

public class Client {
	private String clientId;
	private String clientName;
	@Expose
	private Map<String, Account> accounts = new HashMap<>();
	
	public Client(String clientId, String clientName) {
		this.clientId = clientId;
		this.clientName = clientName;
	}
	
	public String getClientId() {
		return clientId;
	}
	public String getClientName() {
		return clientName;
	}
	public Map<String, Account> getAccounts() {
		return Collections.unmodifiableMap(accounts);
	}
	public void addAccount(Account account) {
		accounts.put(account.getAccountId(), account);
	}

	@Override
	public String toString() {
		return "Client [clientId=" + clientId + ", clientName=" + clientName + ", accounts=" + accounts + "]";
	}
	
}
