package com.barthezzko.transfer.service;

import java.math.BigDecimal;

import com.barthezzko.domain.AccountInfo;
import com.barthezzko.domain.Currency;

/**
 * API for client-account management
 * @author barthezzko
 *
 */
public interface TransferService {

	long registerClient(String clientName);
	
	void removeClient(long clientId);
	
	long registerAccount(long clientId, Currency curr);
	
	void removeAccount(long accountId);
	
	void transfer(long fromAccount, long toAccount, BigDecimal amount);
	
	void topUpAccount(long accountId, BigDecimal amount);

	AccountInfo getAccountInfo(long accountId);
	
	AccountInfo getClientInfo(long clientInfo);
	
}
