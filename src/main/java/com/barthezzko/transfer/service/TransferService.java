package com.barthezzko.transfer.service;

import java.math.BigDecimal;

import com.barthezzko.domain.Account;
import com.barthezzko.domain.Currency;

public interface TransferService {

	void transfer(Account from, Account to, BigDecimal amount);
	
	Account getAccount(Long accountId);
	
	Account registerAccount(String name, Long accountId, Currency curr);
	
}
