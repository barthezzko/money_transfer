package com.barthezzko.transfer.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.barthezzko.common.MoneyTransferException;
import com.barthezzko.domain.Account;
import com.barthezzko.domain.Currency;

public class SimpleTransferTest {
	
	private TransferService transfService = new TransferServiceImpl();
	private Account acc1 = null, acc2 = null;

	/*
	 * NB: test same number during register exception
	 * */
	
	@Test(expected = MoneyTransferException.class)
	public void sameAccountId(){
		transfService.registerAccount("Smith, John", 651437652L, Currency.USD);
		transfService.registerAccount("Baytsurov, Mikhail", 651437652L, Currency.RUR);
	}
	
	
	@Test
	public void getAccountTest(){
		acc1 = transfService.registerAccount("Smith, John", 651437652L, Currency.USD);
		assertEquals(acc1, transfService.getAccount(651437652L));
	}
	
	
}
