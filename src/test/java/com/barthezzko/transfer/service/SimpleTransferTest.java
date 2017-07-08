package com.barthezzko.transfer.service;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.Before;
import org.junit.Test;

import com.barthezzko.common.MoneyTransferException;
import com.barthezzko.domain.Account;
import com.barthezzko.domain.Currency;

public class SimpleTransferTest {

	private KeyGenerator keyGen = new KeyGeneratorImpl();
	private TransferService transfService = new TransferServiceImpl(new FXServiceImpl(), keyGen, new DataStorageImpl(keyGen));
	
	/*  registering clients */
	private String yuriPetrovaId = transfService.registerClient("Petrova, Yuri");
	private String borisTheBladeId = transfService.registerClient("Boris, The Blade");
	private String turkishId = transfService.registerClient("Turkish");
	
	/*  registering accounts */
	private String yuriPetrovaUSD = transfService.registerAccount(yuriPetrovaId, Currency.USD);
	private String yuriPetrovaRUR = transfService.registerAccount(yuriPetrovaId, Currency.RUR);
	private String turkishUSD = transfService.registerAccount(turkishId, Currency.USD);
	private String borisTheBladeEUR = transfService.registerAccount(borisTheBladeId, Currency.EUR);
	
	@Before
	public void before(){
		transfService.topUpAccount(yuriPetrovaUSD, bigDec(700));
		transfService.topUpAccount(yuriPetrovaRUR, bigDec(10_000));
	}

	@Test
	public void getAccountTest() {
		Account yuriPetrovaRURAccount = transfService.getAccount(yuriPetrovaRUR);
		assertEquals("Petrova, Yuri", yuriPetrovaRURAccount.getOwner().getClientName());
		assertEquals(Currency.RUR, yuriPetrovaRURAccount.getCurrency());
		assertEquals(bigDec(10_000), yuriPetrovaRURAccount.getAmountNet());
	}

	@Test
	public void simpleTransferTest() {
		transfService.transferAcc2Acc(yuriPetrovaUSD, turkishUSD, bigDec(100));
		
		assertEquals(bigDec(600), transfService.getAccount(yuriPetrovaUSD).getAmountNet());
		assertEquals(bigDec(100), transfService.getAccount(turkishUSD).getAmountNet());
	}
	
	@Test
	public void usdToRUR() {
		transfService.transferAcc2Acc(yuriPetrovaUSD, yuriPetrovaRUR, bigDec(100));
		
		assertEquals(bigDec(600), transfService.getAccount(yuriPetrovaUSD).getAmountNet());
		assertEquals(bigDec(15938), transfService.getAccount(yuriPetrovaRUR).getAmountNet());
	}
	
	@Test
	public void eurToUSD() {
		transfService.topUpAccount(borisTheBladeEUR, bigDec(500));
		transfService.transferAcc2Acc(borisTheBladeEUR, turkishUSD, bigDec(250));
		
		assertEquals(bigDec(250), transfService.getAccount(borisTheBladeEUR).getAmountNet());
		assertEquals(bigDec(284.09), transfService.getAccount(turkishUSD).getAmountNet());
	}
	
	@Test(expected = MoneyTransferException.class)
	public void insufficientFunds() {
		transfService.transferAcc2Acc(yuriPetrovaUSD, turkishUSD, bigDec(701));
	}
	
	private BigDecimal bigDec(double amnt){
		return BigDecimal.valueOf(amnt).setScale(2, RoundingMode.HALF_UP);
	}

}
