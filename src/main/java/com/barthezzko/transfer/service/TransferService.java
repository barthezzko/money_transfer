package com.barthezzko.transfer.service;

import java.math.BigDecimal;

import com.barthezzko.domain.Account;
import com.barthezzko.domain.Client;
import com.barthezzko.domain.Currency;

/**
 * API for client-account management
 * @author barthezzko
 *
 */
public interface TransferService {

	String registerClient(String clientName);
	
	String registerAccount(String clientId, Currency curr);
	
	void transferAcc2Acc(String fromAccount, String toAccount, BigDecimal amount);
	
	void transferC2C(String fromClient, String toClient, BigDecimal amount, Currency currency);
	
	void topUpAccount(String accountId, BigDecimal amount);

	Account getAccount(String accountId);
	
	Client getClient(String clientId);
	
}
