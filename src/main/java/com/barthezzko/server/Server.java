package com.barthezzko.server;

import static spark.Spark.get;
import static spark.Spark.port;

import com.barthezzko.transfer.service.TransferService;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class Server {

	private TransferService transferService;
	
	public static void main(String[] args) {
		Server server = new Server();
		server.prepareInjection();
		server.runServer();
    }
	
	private void prepareInjection() {
		Injector injector = Guice.createInjector(new MoneyTransferModule());
		transferService = injector.getInstance(TransferService.class);
	}

	private void runServer(){
		port(9000);
        get("/account", (req, res) -> "Hello World");
	}
	
	
	
	
}
