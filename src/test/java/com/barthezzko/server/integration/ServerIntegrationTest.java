package com.barthezzko.server.integration;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpMethod;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.barthezzko.domain.Account;
import com.barthezzko.domain.Client;
import com.barthezzko.domain.Currency;
import com.barthezzko.server.Server;
import com.google.gson.Gson;

public class ServerIntegrationTest {

	private static String SERVER_BASE_URL = "http://localhost:9000/";
	private Logger logger = Logger.getLogger(ServerIntegrationTest.class);
	private Gson gson = new Gson();
	
	Client petrovaClient = new Client("1", "Petrova, Yuri");
	Client borisClient = new Client("2", "Boris, The Blade");
	Client turkishClient = new Client("3", "Turkish");

	Account yuriPetrovaUSD = new Account.Builder().owner(petrovaClient).accountId("USD-1-1").currency(Currency.USD)
			.build();
	Account yuriPetrovaRUR = new Account.Builder().owner(petrovaClient).accountId("RUR-1-2").currency(Currency.RUR)
			.build();
	Account turkishUSD = new Account.Builder().owner(turkishClient).accountId("RUR-2-3").currency(Currency.USD).build();
	Account borisTheBladeEUR = new Account.Builder().owner(borisClient).accountId("RUR-3-4").currency(Currency.EUR)
			.build();

	@FunctionalInterface
	interface HttpRequestCallback {
		void onResponse(String response);
	}

	@Before
	public void before() {
		Server.main(new String[] { "addFakeData" });
	}

	private void invoke(String url, HttpMethod method, HttpRequestCallback callback) {
		invoke(url, method, null, callback);
	}

	private void invoke(String url, HttpMethod method, Map<String, String> payload, HttpRequestCallback callback) {
		try {
			HttpClient client = new HttpClient();
			client.start();
			ContentResponse response = null;
			switch (method) {
			case GET:
				response = client.GET(SERVER_BASE_URL + url);
				break;
			case POST:
				Request request = client.POST(SERVER_BASE_URL + url);
				payload.entrySet().forEach(e -> {
					request.param(e.getKey(), e.getValue());
				});
				response = request.send();
				logger.info("invoked POST for " + url + ", params =" + payload);
				break;
			case PUT:
				break;
			case CONNECT:
			case DELETE:
			case HEAD:
			case MOVE:
			case OPTIONS:
			case PRI:
			case PROXY:
			case TRACE:
				throw new RuntimeException("HttpMethod = " + method.name() + " is not supported by test client");
			default:
				break;
			}

			if (callback != null) {
				callback.onResponse(response.getContentAsString());
			}
		} catch (Exception ex) {
			logger.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	@Test
	public void testServerSimple() {
		invoke("serverStatus", HttpMethod.GET, (content) -> {
			assertEquals("ok", content);
		});
	}

	@Test
	public void getAccount() {
		invoke("account/USD-1-1", HttpMethod.GET, (content) -> {
			String expectedJson = gson.toJson(Server.success(yuriPetrovaUSD));
			assertEquals(expectedJson, content);
		});
		invoke("account/USD-1-2", HttpMethod.GET, (content) -> {
			String expectedJson = gson.toJson(Server.success(yuriPetrovaRUR));
			assertEquals(expectedJson, content);
		});
		invoke("account/USD-2-3", HttpMethod.GET, (content) -> {
			String expectedJson = gson.toJson(Server.success(turkishUSD));
			assertEquals(expectedJson, content);
		});
		invoke("account/USD-3-4", HttpMethod.GET, (content) -> {
			String expectedJson = gson.toJson(Server.success(borisTheBladeEUR));
			assertEquals(expectedJson, content);
		});
	}

	@Test
	@Ignore
	public void insertThanGet() {
		invoke("account/add", HttpMethod.POST, map(), (content) -> {
			assertEquals(gson.toJson(Server.success("Account 123 created")), content);
		});
		/*
		 * invoke("account/123", HttpMethod.GET, (content)->{
		 * assertEquals(gson.toJson(Server.success(new AccountInfo("Tony", 123L,
		 * BigDecimal.ZERO, Currency.EUR))), content.getContentAsString()); });
		 */
	}

	private Map<String, String> map(String... strings) {
		if (strings.length % 2 != 0) {
			throw new RuntimeException("Invalid payload array");
		}
		Map<String, String> payload = new HashMap<>();
		for (int i = 0; i < strings.length; i += 2) {
			payload.put(strings[i], strings[i + 1]);
		}
		return payload;
	}
}
