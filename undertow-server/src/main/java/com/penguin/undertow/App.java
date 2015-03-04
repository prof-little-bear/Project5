package com.penguin.undertow;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;

/**
 * Main server class for 15619 Project 5.
 *
 * Team Penguin.
 */
public class App {

  static final String Q1_PATH = "/q1";
  // TODO: change port do 80 before deploying
  static final int SERVER_PORT = 8080;
  static final String SERVER_IP = "0.0.0.0";

  public static void main(String[] args) {

    // Create question 1 heartbeat handler
    HttpHandler heartbeathandler = new HeartBeatHandler();

    // Create undertow server on {@link SERVER_PORT}
    Undertow.Builder builder = Undertow.builder().addHttpListener(SERVER_PORT,
        SERVER_IP);

    // Set question 1 handler
    builder
        .setHandler(Handlers.path().addPrefixPath(Q1_PATH, heartbeathandler));

    // Build and start the server
    Undertow server = builder.build();
    server.start();
    System.out.println("Server started");
  }
}
