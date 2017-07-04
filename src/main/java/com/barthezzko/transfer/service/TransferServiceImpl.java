package com.barthezzko.transfer.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.barthezzko.common.MoneyTransferException;
import com.barthezzko.domain.Account;
import com.barthezzko.domain.Account.Builder;
import com.barthezzko.domain.AccountInfo;
import com.barthezzko.domain.Currency;

public class TransferServiceImpl implements TransferService {

	private final Map<Long, Account> accountsRegistered = new HashMap<>();
	private final FXService fxService = new FXService();

	public void transfer(Long from, Long to, BigDecimal amount) {
		if (from == to) {
			// logger.info("");
			return;
		}
		Account fromAccount = getAccount(from);
		Account toAccount = getAccount(to);
		if (fromAccount == null) {
			throw new MoneyTransferException("Payer account for [id=" + from + "] doesn't exist");
		} else if (toAccount == null) {
			throw new MoneyTransferException("Payee account for [id=" + from + "] doesn't exist");
		} else {
			// Concurrency in Practice
			if (fromAccount.getAccountId() > toAccount.getAccountId()) {
				synchronized (from) {
					synchronized (to) {
						transferInt(fromAccount, toAccount, amount);
					}
				}
			} else {
				synchronized (to) {
					synchronized (from) {
						transferInt(fromAccount, toAccount, amount);
					}
				}
			}

		}
	}

	private void transferInt(Account from, Account to, BigDecimal amount){
		if (from.getAmountNet().compareTo(amount)<0){
			throw new MoneyTransferException("Unsufficient funds for account [id="+from.getAccountId()+"]");
		}
		from.topUp(amount.negate());
		BigDecimal payeeAmount = from.getCurrency() == to.getCurrency() ? amount : fxService.convert(amount, from.getCurrency(), to.getCurrency());
		to.topUp(payeeAmount);
	}

	@Override
	public void registerAccount(String name, Long accountId, Currency curr) {
		if (!accountsRegistered.containsKey(accountId)) {
			Account.Builder bldr = new Builder().clientName(name).accountId(accountId).currency(curr)
					.amountNet(BigDecimal.ZERO);
			Account acc = bldr.build();
			accountsRegistered.putIfAbsent(accountId, acc);
			return;
		}
		throw new MoneyTransferException("Account with accountId = " + accountId + " has already been registered");
	}

	@Override
	public void topUpAccount(Long accountId, BigDecimal amount) {
		Account accToTopUp = getAccount(accountId);
		if (accToTopUp != null) {
			synchronized (accToTopUp) {
				accToTopUp.topUp(amount);
			}
		}
	}

	
	private Account getAccount(long accountId) {
		return accountsRegistered.get(accountId);
	}

	@Override
	public AccountInfo getAccountInfo(long accountId) {
		Account targetAcc = getAccount(accountId);
		if (targetAcc!=null){
			return targetAcc.getAccountInfo();
		}
		return null;
	}

}
