package com.barthezzko.domain;

import java.math.BigDecimal;

public final class AccountInfo {

	private final Long accountId;
	private final BigDecimal amountNet;
	private final Currency currency;

	public AccountInfo(Long accountId, BigDecimal amountNet, Currency currency) {
		this.accountId = accountId;
		this.amountNet = amountNet;
		this.currency = currency;
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
		return "AccountInfo [accountId=" + accountId + ", amountNet=" + amountNet + ", currency=" + currency + "]";
	}
}