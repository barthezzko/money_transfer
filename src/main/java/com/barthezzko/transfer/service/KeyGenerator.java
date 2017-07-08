package com.barthezzko.transfer.service;

import com.barthezzko.domain.Currency;

public interface KeyGenerator {

	String nextClientId();

	String nextAccountId(String clientId, Currency curr);
	
	String extractClientFromAccountId(String accountId);

}
