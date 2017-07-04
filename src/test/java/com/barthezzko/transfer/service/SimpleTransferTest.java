package com.barthezzko.transfer.service;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

import com.barthezzko.common.MoneyTransferException;
import com.barthezzko.domain.Account;
import com.barthezzko.domain.Currency;

public class SimpleTransferTest {

	private TransferService transfService = new TransferServiceImpl();
	
	/*
	 * NB: test same number during register exception
	 */

	@Test(expected = MoneyTransferException.class)
	public void sameAccountId() {
		transfService.registerAccount("Smith, John", 651437652L, Currency.USD);
		transfService.registerAccount("Baytsurov, Mikhail", 651437652L, Currency.RUR);
	}

	@Test
	public void getAccountTest() {
		transfService.registerAccount("Smith, John", 651437652L, Currency.USD);
		Account acc1 = transfService.getAccount(651437652L);
		assertEquals(651437652L, acc1.getAccountId().longValue());
		assertEquals("Smith, John", acc1.getClientName());
		assertEquals(Currency.USD, acc1.getCurrency());
		assertEquals(BigDecimal.ZERO, acc1.getAmountNet());
		transfService.topUpAccount(651437652L, BigDecimal.valueOf(123));
		
	}

	@Test
	public void simpleTransferTest() {
		transfService.registerAccount("Smith, John", 651437652L, Currency.USD);
		transfService.topUpAccount(651437652L, BigDecimal.valueOf(1000));
		
		transfService.registerAccount("Baytsurov, Mikhail", 145325698L, Currency.USD);
		transfService.transfer(651437652L, 145325698L, BigDecimal.valueOf(100));
		
		assertEquals(BigDecimal.valueOf(900), transfService.getAccount(651437652L).getAmountNet());
		assertEquals(BigDecimal.valueOf(100), transfService.getAccount(145325698L).getAmountNet());
	}

}
