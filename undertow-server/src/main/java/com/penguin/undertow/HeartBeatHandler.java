package com.penguin.undertow;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.math.BigInteger;
import java.util.Date;
import java.util.Deque;

import org.apache.commons.lang.time.FastDateFormat;

/**
 * Handler for Question 1 - Heartbeat page.
 *
 * @author mitayun
 */
public class HeartBeatHandler implements HttpHandler {

	/**
	 * Team ID
	 */
	private static final String ID_TEAM = "Penguins";

	/**
	 * AWS IDs
	 */
	private static final String ID1 = "4281-8781-3308"; // Mita
	private static final String ID2 = "8247-8059-6172"; // Zheng
	private static final String ID3 = "0053-1858-2243"; // Ji

	/**
	 * Keys for http params
	 */
	private static final String KEY_XY = "key";
	private static final String KEY_MESSAGE = "message";

	/**
	 * Secret Key
	 */
	private static final BigInteger X = new BigInteger(
			"8271997208960872478735181815578166723519929177896558845922250595511921395049126920528021164569045773");
	private static final BigInteger MODULAR_FACTOR = new BigInteger("25");

	/**
	 * Main function for handling HTTP request
	 */
	public void handleRequest(final HttpServerExchange exchange) throws Exception {

		// Get parameters
		Deque<String> xys = exchange.getQueryParameters().get(KEY_XY);
		BigInteger xy = new BigInteger(xys.peekFirst());
		BigInteger y = xy.divide(X);
		int z = 1 + y.mod(MODULAR_FACTOR).intValue();

		Deque<String> messages = exchange.getQueryParameters().get(KEY_MESSAGE);

		// Send response back
		exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
		exchange.getResponseSender().send(getResponse(z, messages.peekFirst()));
	}

	private String getResponse(int z, String message) {
		return getName() + getTimeStamp() + getDecryptedMessage(z, message);
	}

	private String getDecryptedMessage(int z, String message) {
		// TODO
		return z + "\n" + message;
	}

	private String getTimeStamp() {
		return FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss\n").format(
				new Date());
	}

	private static String getName() {
		return ID_TEAM + "," + ID1 + "," + ID2 + "," + ID3 + "\n";
	}
}
