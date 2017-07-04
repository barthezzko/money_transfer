package com.barthezzko.server;

import static spark.Spark.*;

import org.apache.log4j.Logger;

import com.barthezzko.domain.Currency;
import com.barthezzko.transfer.service.TransferService;
import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;

import spark.ResponseTransformer;

public class Server {

	private static int SERVER_PORT = 9000;
	private static boolean ADD_FAKE_DATA = true;
	private static Logger logger = Logger.getLogger(Server.class);
	private static final Gson gson = new Gson();
	private TransferService transferService;

	public static void main(String[] args) {
		Server server = new Server();
		server.prepareInjection();
		if (ADD_FAKE_DATA) {
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
		before("/*", (q, a) -> logger.info(q.pathInfo()));
		exception(Exception.class, (e, req, res) -> {
			logger.error(e, e);
			res.body(toJson(Response.error("Error during processing your request, cause: " + e.getMessage())));
			res.status(500);
		});
		get("/getAccount/:accountId", (req, res) -> {
			res.type("application/json");
			return Response.success(transferService.getAccountInfo(Long.valueOf(req.params("accountId"))));
		}, json());
	}

	public static class Response {

		private ResponseType responseType;
		private Object payload;

		private Response(ResponseType responseType, Object payload) {
			this.responseType = responseType;
			this.payload = payload;
		}

		static Response success(Object obj) {
			return new Response(ResponseType.SUCCESS, obj);
		}

		static Response error(String error) {
			return new Response(ResponseType.ERROR, error);
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
		transferService.registerAccount("Blade, Boris", 810111L, Currency.RUR);
		transferService.registerAccount("Blade, Boris", 978111L, Currency.EUR);
		transferService.registerAccount("Blade, Boris", 840111L, Currency.USD);
	}

	public static String toJson(Object object) {
		return gson.toJson(object);
	}
	private static ResponseTransformer json() {
		return Server::toJson;
	}
}
