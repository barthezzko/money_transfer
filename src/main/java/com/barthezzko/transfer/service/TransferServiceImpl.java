package com.barthezzko.transfer.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.barthezzko.common.MoneyTransferException;
import com.barthezzko.domain.Account;
import com.barthezzko.domain.Account.Builder;
import com.barthezzko.domain.Currency;

public class TransferServiceImpl implements TransferService {

	private final Map<Long, Account> accountsRegistered = new HashMap<>();
	
	public void transfer(Account from, Account to, BigDecimal amount) {
		// TODO Auto-generated method stub
		
	}

	public Account getAccount(Long accountId) {
		return accountsRegistered.get(accountId);
	}

	@Override
	public Account registerAccount(String name, Long accountId, Currency curr) {
		if (!accountsRegistered.containsKey(accountId)){
			Account.Builder bldr = new Builder().clientName(name).accountId(accountId).currency(curr).amountNet(BigDecimal.ZERO);
			Account acc = bldr.build();
			accountsRegistered.putIfAbsent(accountId, acc);
			return acc;
		} 
		throw new MoneyTransferException("Account with accountId = " + accountId + " has already been registered");	
	}

	@Override
	public void topUpAccount(Long accountId, BigDecimal amount) {
		Account accToTopUp = getAccount(accountId);
		if (accToTopUp!=null){
			synchronized (accToTopUp) {
				accToTopUp.topUp(amount);
			}
		}
	}

}
