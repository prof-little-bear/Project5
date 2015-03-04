package com.penguin.undertow;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.math.BigInteger;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.time.FastDateFormat;

/**
 * Handler for Question 1 - Heartbeat page.
 *
 * @author mitayun
 */
public class HeartBeatHandler implements HttpHandler {

  /**
   * LRU Cache implementation.
   *
   * @author mitayun
   */
  public class Cache<K, V> extends LinkedHashMap<K, V> {

    private final int cache_size;

    public Cache(final int size) {
      super(size + 1, 1f, true);
      cache_size = size;
    }

    @Override
    protected boolean removeEldestEntry(Entry<K, V> eldest) {
      return size() > cache_size;
    }
  }

  /**
   * Static reference to the descryption mapping
   */
  private static Map<Integer, int[]> sSpiralMap = new HashMap<Integer, int[]>();

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

  private static String sName = null;

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

  public static void init() {
    for (int i = 1; i < 11; i++) {
      sSpiralMap.put(i, generateRevSpiralMapping(i));
    }
  }

  private final Cache<BigInteger, Integer> keyCache = new Cache<BigInteger, Integer>(
      1000);

  public static int keyHit = 0;
  public static int messageHit = 0;

  /**
   * Main function for handling HTTP request
   */
  public void handleRequest(final HttpServerExchange exchange) throws Exception {

    // Get parameters
    Deque<String> xys = exchange.getQueryParameters().get(KEY_XY);
    BigInteger xy = new BigInteger(xys.peekFirst());

    int z = 0;
    if (keyCache.containsKey(xy)) {
      keyHit++;
      z = keyCache.get(xy);
    } else {
      BigInteger y = xy.divide(X);
      z = 1 + y.mod(MODULAR_FACTOR).intValue();
      keyCache.put(xy, z);
    }

    Deque<String> messages = exchange.getQueryParameters().get(KEY_MESSAGE);

    // Send response back
    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
    exchange.getResponseSender().send(getResponse(z, messages.peekFirst()));

    exchange.endExchange();
  }

  private String getResponse(int z, String message) {
    return getName() + getTimeStamp() + getDecryptedMessage(z, message);
  }

  private static int[] generateRevSpiralMapping(int n) {
    int[] result = new int[n * n];

    int k = 0;
    int top = 0, bottom = n - 1, left = 0, right = n - 1;
    while (left < right && top < bottom) {
      for (int j = left; j < right; j++) {
        result[top * n + j] = k++;
      }
      for (int i = top; i < bottom; i++) {
        result[i * n + right] = k++;
      }
      for (int j = right; j > left; j--) {
        result[bottom * n + j] = k++;
      }
      for (int i = bottom; i > top; i--) {
        result[i * n + left] = k++;
      }
      left++;
      right--;
      top++;
      bottom--;
    }
    if (n % 2 != 0) {
      result[n / 2 * n + n / 2] = k++;
    }
    return result;
  }

  private static String getDecryptedMessage(int z, String input) {
    int length = input.length();
    int n = (int) Math.sqrt(length);
    int[] map = null;
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
      outputArray[map[i]] = (char) (c - factor);
    }

    return String.valueOf(outputArray) + "\n";
  }

  private String getTimeStamp() {
    return FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss\n").format(
        new Date());
  }

  private static String getName() {
    if (sName == null) {
      sName = ID_TEAM + "," + ID1 + "," + ID2 + "," + ID3 + "," + ID4 + "\n";
    }
    return sName;
  }
}
