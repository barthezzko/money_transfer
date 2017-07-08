package com.barthezzko.domain;

import java.math.BigDecimal;

public class Account {

	private String accountId;
	private BigDecimal amountNet;
	private Client owner;
	private Currency currency;
	
	private Account(){}
	
	public String getAccountId() {
		return accountId;
	}
	public BigDecimal getAmountNet() {
		return amountNet;
	}
	public Currency getCurrency() {
		return currency;
	}
	public Client getOwner() {
		return owner;
	}
	public void topUp(BigDecimal topUpAmount){
		amountNet = amountNet.add(topUpAmount);
	}
	
	public static class Builder {
		
		private String accountId;
		private BigDecimal amountNet;
		private Currency currency;
		private Client owner;
		
		public Builder accountId(String accountId) {
			this.accountId = accountId;
			return this;
		}
		public Builder owner(Client owner) {
			this.owner = owner;
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
			acc.owner = owner;
			return acc;
		}
	} 
}
