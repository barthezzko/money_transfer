package com.barthezzko.domain;

import java.math.BigDecimal;

public class Account {

	private String clientName;
	private Long accountId;
	private BigDecimal amountNet;
	private Currency currency;
	
	private Account(){}
	
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
}
