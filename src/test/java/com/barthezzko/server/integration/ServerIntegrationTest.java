package com.barthezzko.server.integration;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpMethod;
import org.junit.BeforeClass;
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

	Account yuriPetrovaUSD = new Account.Builder().accountId("USD-1-1").currency(Currency.USD).amountNet(bigDec(0))
			.build();
	Account yuriPetrovaRUR = new Account.Builder().accountId("RUR-1-2").currency(Currency.RUR).amountNet(bigDec(0))
			.build();
	Account borisTheBladeEUR = new Account.Builder().accountId("EUR-2-4").currency(Currency.EUR).amountNet(bigDec(0))
			.build();
	Account turkishUSD = new Account.Builder().accountId("USD-3-3").currency(Currency.USD).amountNet(bigDec(0)).build();
	
	@FunctionalInterface
	interface HttpRequestCallback {
		void onResponse(String response);
	}

	@BeforeClass
	public static void beforeClass() {
		Server.main(new String[] { "addFakeData" });
	}

	private BigDecimal bigDec(double amnt){
		return BigDecimal.valueOf(amnt).setScale(0, RoundingMode.HALF_UP);
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
		invoke("account/RUR-1-2", HttpMethod.GET, (content) -> {
			String expectedJson = gson.toJson(Server.success(yuriPetrovaRUR));
			assertEquals(expectedJson, content);
		});
	}

	@Test
	public void insertThanGet() {
		invoke("account/add", HttpMethod.POST, map("clientId", "1", "currency", "EUR"), (content) -> {
			assertEquals(gson.toJson(Server.success("Account [accountId EUR-1-5 has been created for clientId 1")), content);
		});
		invoke("account/add", HttpMethod.POST, map("clientId", "4", "currency", "EUR"), (content) -> {
			assertEquals(gson.toJson(Server.error("Error during processing your request, cause: client to add the account should exist")), content);
		});
	}
	@Test
	public void acc2acc() {
		invoke("transfer/acc2acc", HttpMethod.POST, map("sourceAcc", "USD-1-1", "destAcc", "USD-3-3", "amount", "100"), (content) -> {
			assertEquals(gson.toJson(Server.error("Error during processing your request, cause: Unsufficient funds for account [id\u003dUSD-1-1]")), content);
		});
		invoke("account/topup", HttpMethod.POST, map("destAccount", "USD-1-1", "amount", "200"), (content) -> {
			assertEquals(gson.toJson(Server.success("Account [accountId\u003dUSD-1-1] was topped up by 200.0")), content);
		});
		invoke("transfer/acc2acc", HttpMethod.POST, map("sourceAcc", "USD-1-1", "destAcc", "USD-3-3", "amount", "100"), (content) -> {
			assertEquals(gson.toJson(Server.success("Account-to-Account transfer [USD-1-1-\u003eUSD-3-3, amount\u003d100.0] has been created")), content);
		});
	}
	
	@Test
	public void client2client() {
		invoke("transfer/cli2cli", HttpMethod.POST, map("sourceClient", "1", "destClient", "2", "amount", "100", "currency", "EUR"), (content) -> {
			assertEquals(gson.toJson(Server.error("Error during processing your request, cause: EUR account is not found for source client 1")), content);
		});
		invoke("account/add", HttpMethod.POST, map("clientId", "1", "currency", "EUR"), (content) -> {
			assertEquals(gson.toJson(Server.success("Account [accountId EUR-1-6 has been created for clientId 1")), content);
		});
		invoke("account/topup", HttpMethod.POST, map("destAccount", "EUR-1-6", "amount", "200"), (content) -> {
			assertEquals(gson.toJson(Server.success("Account [accountId\u003dEUR-1-6] was topped up by 200.0")), content);
		});
		invoke("transfer/cli2cli", HttpMethod.POST, map("sourceClient", "1", "destClient", "2", "amount", "100", "currency", "EUR"), (content) -> {
			assertEquals(gson.toJson(Server.success("Client-To-Client transfer [1-\u003e2, amount\u003d100.0] has been created")), content);
		});
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
