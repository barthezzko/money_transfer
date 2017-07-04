package com.barthezzko.transfer.service;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.Before;
import org.junit.Test;

import com.barthezzko.common.MoneyTransferException;
import com.barthezzko.domain.AccountInfo;
import com.barthezzko.domain.Currency;

public class SimpleTransferTest {

	private TransferService transfService = new TransferServiceImpl();
	
	@Before
	public void before(){
		transfService.registerAccount("Smith, John", 651437652L, Currency.USD);
		transfService.topUpAccount(651437652L, BigDecimal.valueOf(1000));
	}
	

	@Test(expected = MoneyTransferException.class)
	public void sameAccountId() {
		transfService.registerAccount("Baytsurov, Mikhail", 651437652L, Currency.RUR);
	}

	@Test
	public void getAccountTest() {
		AccountInfo acc1 = transfService.getAccountInfo(651437652L);
		assertEquals(651437652L, acc1.getAccountId().longValue());
		assertEquals("Smith, John", acc1.getClientName());
		assertEquals(Currency.USD, acc1.getCurrency());
		assertEquals(BigDecimal.valueOf(1000), acc1.getAmountNet());
		transfService.topUpAccount(651437652L, BigDecimal.valueOf(123));
		
	}

	@Test
	public void simpleTransferTest() {
		transfService.registerAccount("Baytsurov, Mikhail", 145325698L, Currency.USD);
		transfService.transfer(651437652L, 145325698L, BigDecimal.valueOf(100));
		
		assertEquals(BigDecimal.valueOf(900), transfService.getAccountInfo(651437652L).getAmountNet());
		assertEquals(BigDecimal.valueOf(100), transfService.getAccountInfo(145325698L).getAmountNet());
	}
	
	@Test
	public void usdToRUR() {
		transfService.registerAccount("Baytsurov, Mikhail", 145325698L, Currency.RUR);
		transfService.transfer(651437652L, 145325698L, BigDecimal.valueOf(100));
		
		assertEquals(BigDecimal.valueOf(900), transfService.getAccountInfo(651437652L).getAmountNet());
		assertEquals(BigDecimal.valueOf(5938).setScale(2, RoundingMode.HALF_UP), transfService.getAccountInfo(145325698L).getAmountNet());
	}
	
	@Test
	public void eurToUSD() {
		transfService.registerAccount("Blade, Boris", 35489698L, Currency.EUR);
		transfService.topUpAccount(35489698L, BigDecimal.valueOf(1234.56));
		
		transfService.registerAccount("Petrova, Yuri", 98376289L, Currency.USD);
		transfService.transfer(35489698L, 98376289L, BigDecimal.valueOf(250));
		
		assertEquals(BigDecimal.valueOf(984.56).setScale(2, RoundingMode.HALF_UP), transfService.getAccountInfo(35489698L).getAmountNet());
		assertEquals(BigDecimal.valueOf(284.09).setScale(2, RoundingMode.HALF_UP), transfService.getAccountInfo(98376289L).getAmountNet());
	}
	
	@Test(expected = MoneyTransferException.class)
	public void insufficientFunds() {
		transfService.registerAccount("Turkish", 98326488L, Currency.EUR);
		transfService.transfer(651437652L, 98326488L, BigDecimal.valueOf(1001));
	}

}
