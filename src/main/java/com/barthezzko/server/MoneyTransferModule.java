package com.barthezzko.server;

import com.barthezzko.transfer.service.DataStorage;
import com.barthezzko.transfer.service.DataStorageImpl;
import com.barthezzko.transfer.service.FXService;
import com.barthezzko.transfer.service.FXServiceImpl;
import com.barthezzko.transfer.service.KeyGenerator;
import com.barthezzko.transfer.service.KeyGeneratorImpl;
import com.barthezzko.transfer.service.TransferService;
import com.barthezzko.transfer.service.TransferServiceImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class MoneyTransferModule extends AbstractModule {
	
	@Override
	protected void configure() {
		bind(FXService.class).to(FXServiceImpl.class).in(Scopes.SINGLETON);
		bind(TransferService.class).to(TransferServiceImpl.class).in(Scopes.SINGLETON);
		bind(KeyGenerator.class).to(KeyGeneratorImpl.class).in(Scopes.SINGLETON);
		bind(DataStorage.class).to(DataStorageImpl.class).in(Scopes.SINGLETON);
	}
}
