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
	
	//remove public. no explicit account exposure.
	//AccountInfo helper class or protection copy?
	public void topUp(BigDecimal topUpAmount){
		amountNet = amountNet.add(topUpAmount);
	}
	
	public static class Builder {
		
		private String clientName;
		private Long accountId;
		private BigDecimal amountNet;
		private Currency currency;
		
		public Builder clientName(String clientName) {
			this.clientName = clientName;
			return this;
		}
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
			acc.clientName = clientName;
			acc.amountNet = amountNet;
			acc.currency = currency;
			return acc;
		}
	} 
}
