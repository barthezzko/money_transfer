package com.barthezzko.domain;

import java.math.BigDecimal;

public final class AccountInfo {
	
	private final String clientName;
	private final Long accountId;
	private final BigDecimal amountNet;
	private final Currency currency;
	
	public AccountInfo(String clientName, Long accountId, BigDecimal amountNet, Currency currency) {
		this.clientName = clientName;
		this.accountId = accountId;
		this.amountNet = amountNet;
		this.currency = currency;
	}

	public String getClientName() {
		return clientName;
	}

	public Long getAccountId() {
		return accountId;
	}

	public BigDecimal getAmountNet() {
		return amountNet;
	}

	public Currency getCurrency() {
		return currency;
	}

	@Override
	public String toString() {
		return "AccountInfo [clientName=" + clientName + ", accountId=" + accountId + ", amountNet=" + amountNet
				+ ", currency=" + currency + "]";
	}
}