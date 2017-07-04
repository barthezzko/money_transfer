package com.barthezzko.domain;

import java.math.BigDecimal;

import com.barthezzko.common.MoneyTransferException;

public class Transfer {
	
	private long payerId;
	private long payeeId;
	private BigDecimal amount;
	
	private Transfer(){}

	public static Transfer of(long payerId, long payeeId, BigDecimal amount){
		Transfer transfer = new Transfer();
		if(amount.compareTo(BigDecimal.ZERO)<0){
			throw new MoneyTransferException("Transfer amount can't be negative");
		}
		return null;
	}
	
}
