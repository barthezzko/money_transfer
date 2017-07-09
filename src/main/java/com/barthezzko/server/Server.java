package com.barthezzko.server;

import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import com.barthezzko.domain.Currency;
import com.barthezzko.transfer.service.TransferService;
import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;

import spark.ResponseTransformer;

public class Server {

	private static int SERVER_PORT = 9000;
	private static Logger logger = Logger.getLogger(Server.class);
	private static final Gson gson = new Gson();
	private TransferService transferService;

	public static void main(String[] args) {
		Server server = new Server();
		server.prepareInjection();
		if ("addFakeData".equals(args[0])) {
			logger.info("Adding fake data...");
			server.addFakeData();
		}
		server.runServer();
	}

	private void prepareInjection() {
		logger.info("Injecting dependencies from " + MoneyTransferModule.class.getSimpleName() + " module");
		Injector injector = Guice.createInjector(new MoneyTransferModule());
		logger.info("Bindings:");
		injector.getBindings().entrySet().forEach(entry -> {
			logger.info(entry);
		});
		transferService = injector.getInstance(TransferService.class);
	}

	private void runServer() {
		logger.info("Starting REST service on port:" + SERVER_PORT);
		port(SERVER_PORT);
		before("/*", (q, a) -> {
			StringBuilder sb = new StringBuilder(q.requestMethod()).append(" | ").append(q.pathInfo()).append(" | payload: [");
			if (q.queryParams() != null) {
				q.queryParams().forEach(key -> {
					sb.append(String.format("%s=%s; ", key, q.queryParams(key)));
				});
			}
			if (q.params()!=null){
				q.params().entrySet().forEach(entry -> {
					sb.append(String.format("%s=%s; ", entry.getKey(), entry.getValue()));
				});
			}
			logger.info(sb.append("]").toString());	

		});
		after("/*", (q, a) -> {
			logger.info("Server responds: " + a.body());
		});
		exception(Exception.class, (e, req, res) -> {
			logger.error(e, e);
			res.body(toJson(error("Error during processing your request, cause: " + e.getMessage())));
			res.status(500);
		});
		get("serverStatus", (req, res) -> "ok");
		addAccountMappings();
	}

	private void addAccountMappings() {
		post("/client/add", (req, res) -> {
			return success("Client [clientId=" + transferService.registerClient(req.queryParams("clientName"))
					+ "] has been created");
		}, json());
		post("/account/add", (req, res) -> {
			String clientId = req.queryParams("clientId");
			return success("Account [accountId="
					+ transferService.registerAccount(clientId, Currency.valueOf(req.queryParams("currency")))
					+ " has been created for clientId=" + clientId);
		}, json());
		post("/transfer/acc2acc", (req, res) -> {
			String sourceAccount = req.queryParams("sourceAcc");
			String targetAccount = req.queryParams("destAcc");
			BigDecimal amount = BigDecimal.valueOf(Double.valueOf(req.queryParams("amount")));
			transferService.transferAcc2Acc(sourceAccount, targetAccount, amount);
			return success("Account-to-Account transfer [" + sourceAccount + "->" + targetAccount + ", amount=" + amount
					+ "] has been created");
		}, json());
		post("/transfer/acc2acc", (req, res) -> {
			String sourceClient = req.queryParams("sourceClient");
			String targetClient = req.queryParams("destClient");
			BigDecimal amount = BigDecimal.valueOf(Double.valueOf(req.queryParams("amount")));
			Currency currency = Currency.valueOf(req.queryParams("currency"));

			transferService.transferC2C(sourceClient, targetClient, amount, currency);
			return success("Client-To-Client transfer [" + sourceClient + "->" + targetClient + ", amount=" + amount
					+ "] has been created");
		}, json());

		post("/account/topup", (req, res) -> {
			String destAccount = req.queryParams("destAccount");
			BigDecimal amount = BigDecimal.valueOf(Double.valueOf(req.queryParams("amount")));

			transferService.topUpAccount(destAccount, amount);
			return success("Account [accountId=" + destAccount + "] was topped up by " + amount);
		}, json());

		get("/account/:accountId", (req, res) -> {
			return success(transferService.getAccount(req.params("accountId")));
		}, json());
		get("/client/:clientId", (req, res) -> {
			return success(transferService.getClient(req.params("clientId")));
		});
	}

	public static Response success(Object obj) {
		return new Response(ResponseType.SUCCESS, obj);
	}

	public static Response error(String error) {
		return new Response(ResponseType.ERROR, error);
	}

	public static class Response {

		private ResponseType responseType;
		private Object payload;

		private Response(ResponseType responseType, Object payload) {
			this.responseType = responseType;
			this.payload = payload;
		}

		public ResponseType getResponseType() {
			return responseType;
		}

		public Object getPayload() {
			return payload;
		}
	}

	enum ResponseType {
		SUCCESS, ERROR
	}

	private void addFakeData() {
		/* registering clients */
		String yuriPetrovaId = transferService.registerClient("Petrova, Yuri");
		String borisTheBladeId = transferService.registerClient("Boris, The Blade");
		String turkishId = transferService.registerClient("Turkish");

		/* registering accounts */
		transferService.registerAccount(yuriPetrovaId, Currency.USD);
		transferService.registerAccount(yuriPetrovaId, Currency.RUR);
		transferService.registerAccount(turkishId, Currency.USD);
		transferService.registerAccount(borisTheBladeId, Currency.EUR);
		logger.info("Pre-loaded dataset:");
		transferService.getAllClients().forEach(client->{
			logger.info(client);
		});
	}

	public static String toJson(Object object) {
		return gson.toJson(object);
	}

	private static ResponseTransformer json() {
		return Server::toJson;
	}
}
