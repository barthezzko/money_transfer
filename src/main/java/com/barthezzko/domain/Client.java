package com.barthezzko.domain;

import java.util.Map;

public class Client {
	
	private long clientId;
	private String clientName;
	private Map<Long, Account> accounts;
	
	public long getClientId() {
		return clientId;
	}
	public void setClientId(long clientId) {
		this.clientId = clientId;
	}
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public Map<Long, Account> getAccounts() {
		return accounts;
	}
	public void setAccounts(Map<Long, Account> accounts) {
		this.accounts = accounts;
	}

	@Override
	public String toString() {
		return "Client [clientId=" + clientId + ", clientName=" + clientName + ", accounts=" + accounts + "]";
	}
}
