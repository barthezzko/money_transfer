package com.barthezzko.transfer.service;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.Test;

import com.barthezzko.domain.Currency;

public class FXTest {

	private FXServiceImpl fx = new FXServiceImpl();
	
	@Test
	public void testFX(){
		assertEquals(BigDecimal.valueOf(100.0), fx.convert(BigDecimal.valueOf(100.0), Currency.USD, Currency.USD));
	}
	@Test
	public void testUsdRur(){
		assertEquals(BigDecimal.valueOf(5938).setScale(2, RoundingMode.HALF_UP), fx.convert(BigDecimal.valueOf(100.0), Currency.USD, Currency.RUR));
	}
	@Test
	public void testRurUsd(){
		assertEquals(BigDecimal.valueOf(1.68), fx.convert(BigDecimal.valueOf(100.0), Currency.RUR, Currency.USD));
	}
	@Test
	public void testCross(){
		assertEquals(BigDecimal.valueOf(6747.94), fx.convert(BigDecimal.valueOf(100.0), Currency.EUR, Currency.RUR));
	}
}
