package com.barthezzko.common;

public class MoneyTransferException extends RuntimeException {
	private static final long serialVersionUID = 962918457609147768L;

	public MoneyTransferException(String message) {
		super(message);
	}
}
