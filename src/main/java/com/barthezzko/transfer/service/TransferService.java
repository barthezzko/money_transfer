package com.barthezzko.transfer.service;

import java.math.BigDecimal;

import com.barthezzko.domain.AccountInfo;
import com.barthezzko.domain.Currency;

public interface TransferService {

	void transfer(Long from, Long to, BigDecimal amount);
	
	void registerAccount(String name, Long accountId, Currency curr);
	
	void topUpAccount(Long accountId, BigDecimal amount);

	AccountInfo getAccountInfo(long accountId);
	
}
