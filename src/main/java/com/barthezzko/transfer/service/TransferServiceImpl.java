package com.barthezzko.transfer.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Objects;

import org.apache.log4j.Logger;

import com.barthezzko.common.MoneyTransferException;
import com.barthezzko.domain.Account;
import com.barthezzko.domain.Account.Builder;
import com.barthezzko.domain.Client;
import com.barthezzko.domain.Currency;
import com.google.inject.Inject;

/**
 * 
 * Thread-safe
 * 
 * @author barthezzko
 *
 */
public class TransferServiceImpl implements TransferService {

	private final FXService fxService;
	private final KeyGenerator keyGen;
	private final DataStorage ds;
	private Logger logger = Logger.getLogger(TransferServiceImpl.class);

	@Inject
	public TransferServiceImpl(FXService fxService, KeyGenerator keyGen, DataStorage ds) {
		this.fxService = fxService;
		this.keyGen = keyGen;
		this.ds = ds;
	}

	@Override
	public void transferAcc2Acc(String sourceAccountId, String destinationAccountId, BigDecimal amount) {
		Objects.requireNonNull(sourceAccountId, "fromAccountId must not be null");
		Objects.requireNonNull(destinationAccountId, "toAccountId must not be null");

		if (sourceAccountId.equals(destinationAccountId)) {
			logger.info("Skip transferring money - account [" + sourceAccountId + "] is the same");
			return;
		}
		Account sourceAccount = ds.getAccount(sourceAccountId);
		Account destinationAccount = ds.getAccount(destinationAccountId);
		Objects.requireNonNull(sourceAccount, "sourceAccount must exist");
		Objects.requireNonNull(destinationAccount, "destinationAccount must exist");
		// Concurrency in Practice
		if (sourceAccount.getAccountId().compareTo(destinationAccount.getAccountId()) > 0) {
			synchronized (sourceAccount) {
				synchronized (destinationAccount) {
					transferInt(sourceAccount, destinationAccount, amount);
				}
			}
		} else {
			synchronized (destinationAccount) {
				synchronized (sourceAccount) {
					transferInt(sourceAccount, destinationAccount, amount);
				}
			}
		}
	}

	/**
	 * internal method for account to account money transitions This method
	 * should invoked only when account locks are already acquired
	 * 
	 * @param sourceAccount
	 * @param destinationAccount
	 * @param transferAmount
	 */
	private void transferInt(Account sourceAccount, Account destinationAccount, BigDecimal transferAmount) {
		if (sourceAccount.getAmountNet().compareTo(transferAmount) < 0) {
			throw new MoneyTransferException(
					"Unsufficient funds for account [id=" + sourceAccount.getAccountId() + "]");
		}
		sourceAccount.topUp(transferAmount.negate());
		BigDecimal payeeAmount = sourceAccount.getCurrency() == destinationAccount.getCurrency() ? transferAmount
				: fxService.convert(transferAmount, sourceAccount.getCurrency(), destinationAccount.getCurrency());
		destinationAccount.topUp(payeeAmount);
	}

	@Override
	public void topUpAccount(String destinationAccountId, BigDecimal amount) {
		Objects.requireNonNull(destinationAccountId, "destinationAccountId can't be null");
		Account destinationAccount = ds.getAccount(destinationAccountId);
		Objects.requireNonNull(destinationAccount, "destinationAccount can't be null");
		synchronized (destinationAccount) {
			destinationAccount.topUp(amount);
		}
	}

	@Override
	public Account getAccount(String accountId) {
		return ds.getAccount(accountId);
	}

	@Override
	public String registerClient(String clientName) {
		String clientId = keyGen.nextClientId();
		Client client = new Client(clientId, clientName);
		ds.addClient(client);
		return clientId;
	}

	@Override
	public String registerAccount(String clientId, Currency curr) {
		Objects.requireNonNull(clientId, "clientId can't be empty");
		Client client = ds.getClient(clientId);
		Objects.requireNonNull(client, "client to add the account should exist");
		String accountId = keyGen.nextAccountId(clientId, curr);
		synchronized (client) {
			Account.Builder bldr = new Builder().accountId(accountId).currency(curr)
					.amountNet(BigDecimal.ZERO);
			Account acc = bldr.build();
			ds.addAccount(client, acc);
			//client.addAccount(acc);
		}
		return accountId;
	}

	@Override
	public void transferC2C(String sourceClientId, String destClientId, BigDecimal amount, Currency currency) {
		Objects.requireNonNull(sourceClientId, "sourceClientId must not be null");
		Objects.requireNonNull(destClientId, "destClientId must not be null");

		Client sourceClient = ds.getClient(sourceClientId);
		Client destClient = ds.getClient(destClientId);

		Objects.requireNonNull(sourceClient, "sourceClient should exist");
		Objects.requireNonNull(destClient, "sourceClient should exist");

		Account sourceAccount = accountLookup(sourceClient, currency, amount);
		Account destinationAccount = accountLookup(destClient, currency, BigDecimal.ZERO); 

		Objects.requireNonNull(sourceClient, currency + " account is not found for source client " + sourceClientId);
		Objects.requireNonNull(destinationAccount,
				currency + " account is not found for destination client " + destClientId);

		transferInt(sourceAccount, destinationAccount, amount);
	}

	private Account accountLookup(Client client, Currency curr, BigDecimal amountToLookFor) {
		return client.getAccounts().values().stream()
				.filter(acc -> curr == acc.getCurrency() && acc.getAmountNet().compareTo(amountToLookFor) > 0)
				.findFirst().orElse(null);
	}

	@Override
	public Client getClient(String clientId) {
		return ds.getClient(clientId);
	}

	@Override
	public Collection<Client> getAllClients() {
		return ds.getAllClients();
	}

}
