package com.barthezzko.transfer.service;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import com.barthezzko.domain.Currency;

public class KeyGeneratorImpl implements KeyGenerator {

	private Logger logger = Logger.getLogger(KeyGeneratorImpl.class);
	private final AtomicLong clientNumber = new AtomicLong();
	private final AtomicLong accountNumber = new AtomicLong();
	private static final String DLMTR = "-";

	@Override
	public String nextClientId() {
		return String.valueOf(clientNumber.incrementAndGet());
	}

	@Override
	public String nextAccountId(String clientId, Currency curr) {
		return curr.name() + DLMTR + clientId + DLMTR + accountNumber.incrementAndGet();
	}

	@Override
	public String extractClientFromAccountId(String accountId) {
		int first = accountId.indexOf(DLMTR);
		int last = accountId.lastIndexOf(DLMTR);
		logger.debug("account [" + accountId + "] first=" + first + ", last=" + last);
		if (first == -1 || last == -1 || accountId.indexOf(DLMTR, first + 1) != last) {
			throw new IllegalArgumentException("Account " + accountId + " is invalid");
		}
		String clientId = accountId.substring(first + 1, last);
		logger.info("extracted client [" + clientId + "] from accountId [" + accountId + "]");
		return clientId;
	}
}
