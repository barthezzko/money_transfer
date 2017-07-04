package com.barthezzko.transfer.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.barthezzko.domain.Account;
import com.barthezzko.domain.Currency;

public class TransferServiceImpl implements TransferService {

	private final Map<Long, Account> accountsRegistered = new HashMap<>();
	
	public void transfer(Account from, Account to, BigDecimal amount) {
		// TODO Auto-generated method stub
		
	}

	public Account getAccount(Long accountId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Account registerAccount(String name, Long accountId, Currency curr) {
		return null;
	}

}
