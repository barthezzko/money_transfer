package com.barthezzko.transfer.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.barthezzko.domain.Currency;

/**
 * 
 * It's done on purpose, that we don't have any price spread and convert amount with ratio only
 * Also we simplify the logic and don't add commission
 * @author barthezzko
 *
 */
public class FXServiceImpl implements FXService {

	private final Map<Currency, BigDecimal> fxRates = new HashMap<>();

	{
		fxRates.put(Currency.EUR, BigDecimal.valueOf(0.88));
		fxRates.put(Currency.RUR, BigDecimal.valueOf(59.38));

	}

	/**
	 * 
	 * For now we don't take int
	 * 
	 * @param amount
	 * @param from
	 * @param to
	 * @return
	 */
	@Override
	public BigDecimal convert(BigDecimal amount, Currency from, Currency to) {
		if (from == to) {
			return amount;
		} else {
			if (from == Currency.USD) {
				return amount.multiply(fxRates.get(to)).setScale(2, BigDecimal.ROUND_HALF_UP);
			} else if (to == Currency.USD) {
				return amount.divide(fxRates.get(from), 2, BigDecimal.ROUND_HALF_UP);
			} else {
				// neither is USD, let's do cross-rate
				BigDecimal usdValue = convert(amount, from, Currency.USD);
				return convert(usdValue, Currency.USD, to);
			}
		}
	}

}
