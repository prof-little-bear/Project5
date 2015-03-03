package com.penguin.undertow;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

/**
 * Handler for Question 1 - Heartbeat page.
 * 
 * @author mitayun
 */
public class HeartBeatHandler implements HttpHandler {

	public void handleRequest(final HttpServerExchange exchange) throws Exception {
		exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
		exchange.getResponseSender().send("Hello World");
	}
}
