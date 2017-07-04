package com.barthezzko.transfer.service;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Test;

import com.barthezzko.domain.Currency;

public class FXTest {

	private FXService fx = new FXService();
	
	@Test
	public void testFX(){
		assertEquals(BigDecimal.valueOf(100.0), fx.convert(BigDecimal.valueOf(100.0), Currency.USD, Currency.USD));
	}
	
	
	
}
