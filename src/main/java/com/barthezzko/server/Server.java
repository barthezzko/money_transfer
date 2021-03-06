package com.barthezzko.server;

import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;

import java.math.BigDecimal;
import java.util.Objects;

import org.apache.log4j.Logger;

import com.barthezzko.domain.Currency;
import com.barthezzko.transfer.service.TransferService;
import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;

import spark.Request;
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
			StringBuilder sb = new StringBuilder("IN: ").append(q.requestMethod()).append(" | ").append(q.pathInfo())
					.append(" | payload: [");
			if (q.queryParams() != null) {
				q.queryParams().forEach(key -> {
					sb.append(String.format("%s=%s; ", key, q.queryParams(key)));
				});
			}
			if (q.params() != null) {
				q.params().entrySet().forEach(entry -> {
					sb.append(String.format("%s=%s; ", entry.getKey(), entry.getValue()));
				});
			}
			logger.info(sb.append("]").toString());

		});
		after("/*", (q, a) -> {
			logger.info("OUT: " + a.body());
		});
		exception(Exception.class, (e, req, res) -> {
			String errorMessage = toJson(error("Error during processing your request, cause: " + e.getMessage()));
			logger.error("OUT: " + errorMessage);
			res.body(errorMessage);
			res.status(500);
		});
		get("serverStatus", (req, res) -> "ok");
		addAccountMappings();
	}

	private void addAccountMappings() {
		post("/client/add", (req, res) -> {
			return success("Client [clientId=" + transferService.registerClient(param(req, "clientName"))		+ "] has been created");
		}, json());
		post("/account/add", (req, res) -> {
			String clientId = param(req, "clientId");
			return success("Account [accountId "
					+ transferService.registerAccount(clientId, toEnum(req, Currency.class, "currency"))
					+ " has been created for clientId " + clientId);
		}, json());
		post("/transfer/acc2acc", (req, res) -> {
			String sourceAccount = param(req, "sourceAcc");
			String targetAccount = param(req, "destAcc");
			BigDecimal amount = BigDecimal.valueOf(Double.valueOf(param(req, "amount")));
			transferService.transferAcc2Acc(sourceAccount, targetAccount, amount);
			return success("Account-to-Account transfer [" + sourceAccount + "->" + targetAccount + ", amount=" + amount
					+ "] has been created");
		}, json());
		post("/transfer/cli2cli", (req, res) -> {
			String sourceClient = param(req, "sourceClient");
			String targetClient = param(req, "destClient");
			BigDecimal amount = BigDecimal.valueOf(Double.valueOf(param(req, "amount")));
			Currency currency = toEnum(req, Currency.class, "currency");

			transferService.transferC2C(sourceClient, targetClient, amount, currency);
			return success("Client-To-Client transfer [" + sourceClient + "->" + targetClient + ", amount=" + amount
					+ "] has been created");
		}, json());

		post("/account/topup", (req, res) -> {
			String destAccount = param(req, "destAccount");
			BigDecimal amount = BigDecimal.valueOf(Double.valueOf(param(req, "amount")));

			transferService.topUpAccount(destAccount, amount);
			return success("Account [accountId=" + destAccount + "] was topped up by " + amount);
		}, json());

		get("/account/:accountId", (req, res) -> {
			return success(transferService.getAccount(param(req, "accountId")));
		}, json());
		get("/client/:clientId", (req, res) -> {
			return success(transferService.getClient(param(req, "clientId")));
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
		transferService.getAllClients().forEach(client -> {
			logger.info(client);
		});
	}

	public static String toJson(Object object) {
		return gson.toJson(object);
	}

	private static ResponseTransformer json() {
		return Server::toJson;
	}

	private String param(Request req, String name) {
		String value = "GET".equals(req.requestMethod()) ? req.params(name) : req.queryParams(name);
		Objects.requireNonNull(value, "Input parameter [" + name + "] should not be empty");
		return value;
	}
	@SuppressWarnings("unchecked")
	private <T extends Enum> T toEnum(Request req, Class<T> clazz, String paramName){
		String val = param(req, paramName);
		if (isValidEnum(clazz, val)){
			return (T) Enum.valueOf(clazz, val);
		} else {
			throw new IllegalArgumentException("Input value [" + val + "] is not valid for Enum class " + clazz);
		}
	}
	
	
	//don't want to add apache b for this logic:
	private <E extends Enum<E>> boolean isValidEnum(Class<E> clazz, String inputVal){
		if (inputVal ==null){
			return false;
		}
		try{
			Enum.valueOf(clazz, inputVal);
			return true;
		} catch(IllegalArgumentException e){
			return false;
		}
	}
}
