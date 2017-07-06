package com.barthezzko.server.integration;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpMethod;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.barthezzko.domain.AccountInfo;
import com.barthezzko.domain.Currency;
import com.barthezzko.server.Server;
import com.google.gson.Gson;

public class ServerIntegrationTest {

	private static String SERVER_BASE_URL = "http://localhost:9000/";
	private Logger logger = Logger.getLogger(ServerIntegrationTest.class);
	private Gson gson = new Gson();
	private static Server server;
	
	@FunctionalInterface
	interface HttpRequestCallback{
		void onResponse(ContentResponse response);
	}
	
	
	@Before
	public void before(){
		Server.main(false);
	}
	
	
	private void invoke(String url, HttpMethod method, HttpRequestCallback callback){
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
			        payload.entrySet().forEach(e->{
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
			
			if(callback !=null){
				callback.onResponse(response);
			}
		} catch (Exception ex) {
			logger.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}
	
	@Test
	public void testServerSimple(){
		invoke("serverStatus", HttpMethod.GET, (content)->{
			assertEquals("ok", content.getContentAsString());
		});
	}
	
	@Test
	public void getAcccount(){
		invoke("account/810111", HttpMethod.GET, (content)->{
			assertEquals(gson.toJson(Server.Response.success(new AccountInfo("Blade, Boris", 810111L, BigDecimal.ZERO, Currency.RUR))), content.getContentAsString());
		});
	}
	
	@Test
	public void insertThanGet(){
		Map<String, String> payload = new HashMap<>();
		payload.put("clientName", "Tony");
		payload.put("accountId", "123");
		payload.put("currency", Currency.EUR.name());
		invoke("account/add", HttpMethod.POST, payload, (content)->{
			assertEquals(gson.toJson(Server.Response.success("Account 123 created")), content.getContentAsString());
		});
		invoke("account/123", HttpMethod.GET, (content)->{
			assertEquals(gson.toJson(Server.Response.success(new AccountInfo("Tony", 123L, BigDecimal.ZERO, Currency.EUR))), content.getContentAsString());
		});
	}
}
