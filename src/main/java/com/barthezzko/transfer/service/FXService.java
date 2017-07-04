package com.barthezzko.transfer.service;

import java.math.BigDecimal;

import com.barthezzko.domain.Currency;

public interface FXService {

	public BigDecimal convert(BigDecimal amount, Currency from, Currency to);
}
