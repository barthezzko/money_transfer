package com.barthezzko.transfer.service;

import static org.junit.Assert.*;

import org.junit.Test;

import com.barthezzko.domain.Currency;

public class KeyGeneratorTest {

	private KeyGenerator keyGen = new KeyGeneratorImpl();
	
	@Test
	public void test(){
		String client1 = keyGen.nextClientId();
		String client2 = keyGen.nextClientId();
		String rurAcc = keyGen.nextAccountId(client1, Currency.RUR);
		String usdAcc = keyGen.nextAccountId(client1, Currency.USD);
		assertEquals("RUR-1-1", rurAcc);
		assertEquals("USD-1-2", usdAcc);
		String usdAcc2 = keyGen.nextAccountId(client2, Currency.USD);
		assertEquals("USD-2-3", usdAcc2);
		assertEquals(client1, keyGen.extractClientFromAccountId(usdAcc));
		assertEquals(client2, keyGen.extractClientFromAccountId(usdAcc2));
	}
	
	
	
}
