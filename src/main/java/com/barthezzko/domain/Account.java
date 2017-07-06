package com.barthezzko.domain;

import java.math.BigDecimal;

public class Account {

	private long accountId;
	private long clientId;
	private BigDecimal amountNet;
	private Currency currency;
	
	private Account(){}
	
	public Long getAccountId() {
		return accountId;
	}
	public BigDecimal getAmountNet() {
		return amountNet;
	}
	public Currency getCurrency() {
		return currency;
	}
	
	public AccountInfo getAccountInfo(){
		return new AccountInfo(accountId, amountNet, currency);
	}
	
	public void topUp(BigDecimal topUpAmount){
		amountNet = amountNet.add(topUpAmount);
	}
	
	public static class Builder {
		
		private Long accountId;
		private BigDecimal amountNet;
		private Currency currency;
		
		public Builder accountId(Long accountId) {
			this.accountId = accountId;
			return this;
		}
		public Builder amountNet(BigDecimal amountNet) {
			this.amountNet = amountNet;
			return this;
		}
		public Builder currency(Currency currency) {
			this.currency = currency;
			return this;
		}
		public Account build(){
			Account acc = new Account();
			acc.accountId = accountId;
			acc.amountNet = amountNet;
			acc.currency = currency;
			return acc;
		}
	} 
}
