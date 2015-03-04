package com.penguin.undertow;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.math.BigInteger;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.time.FastDateFormat;

/**
 * Handler for Question 1 - Heartbeat page.
 *
 * @author mitayun
 */
public class HeartBeatHandler implements HttpHandler {

  /**
   * Static reference to the descryption mapping
   */
  private static Map<Integer, Map<Integer, Integer>> sSpiralMap = new HashMap<Integer, Map<Integer, Integer>>();

  /**
   * Preload up to 10x10 matrices
   */
  static {
    for (int i = 1; i < 11; i++) {
      sSpiralMap.put(i, generateRevSpiralMapping(i));
    }
  }

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
  private static final String ID4 = "3818-9577-0956"; // Kaifu

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
    return decryptMessage(z, message);
  }

  private static Map<Integer, Integer> generateRevSpiralMapping(int n) {
    Map<Integer, Integer> map = new HashMap<Integer, Integer>();

    int k = 0;
    int top = 0, bottom = n - 1, left = 0, right = n - 1;
    while (left < right && top < bottom) {
      for (int j = left; j < right; j++) {
        map.put(top * n + j, k++);
      }
      for (int i = top; i < bottom; i++) {
        map.put(i * n + right, k++);
      }
      for (int j = right; j > left; j--) {
        map.put(bottom * n + j, k++);
      }
      for (int i = bottom; i > top; i--) {
        map.put(i * n + left, k++);
      }
      left++;
      right--;
      top++;
      bottom--;
    }
    if (n % 2 != 0) {
      map.put(n / 2 * n + n / 2, k++);
    }
    return map;
  }

  private static String decryptMessage(int z, String input) {
    int length = input.length();
    int n = (int) Math.sqrt(length);
    Map<Integer, Integer> map = null;
    if (sSpiralMap.containsKey(n)) {
      map = sSpiralMap.get(n);
    } else {
      map = generateRevSpiralMapping(n);
      sSpiralMap.put(n, map);
    }

    char[] inputArray = input.toCharArray();
    char[] outputArray = new char[length];

    for (int i = 0; i < length; i++) {
      char c = inputArray[i];
      int factor = c - 'A' < z ? z - 26 : z;
      outputArray[map.get(i)] = (char) (c - factor);
    }

    return String.valueOf(outputArray) + "\n";
  }

  private String getTimeStamp() {
    return FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss\n").format(
        new Date());
  }

  private static String getName() {
    return ID_TEAM + "," + ID1 + "," + ID2 + "," + ID3 + "," + ID4 + "\n";
  }
}
