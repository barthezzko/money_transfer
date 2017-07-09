package com.barthezzko.domain;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

public class Account {

	private String accountId;
	private BigDecimal amountNet;
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
	
	public void topUp(BigDecimal topUpAmount){
		amountNet = amountNet.add(topUpAmount);
	}
	
	@Override
	public String toString() {
		return "Account [accountId=" + accountId + ", amountNet=" + amountNet + ", currency="
				+ currency + "]";
	}

	public static class Builder {
		
		private String accountId;
		private BigDecimal amountNet;
		private Currency currency;
		
		public Builder accountId(String accountId) {
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
