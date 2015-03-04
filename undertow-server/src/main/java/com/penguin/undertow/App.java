package com.penguin.undertow;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpHandler;

/**
 * Main server class for 15619 Project 5.
 *
 * Team Penguin.
 */
public class App {

  static final String Q1_PATH = "/q1";
  // TODO: change port do 80 before deploying
  static final int SERVER_PORT = 80;
  static final String SERVER_IP = "0.0.0.0";

  public static void main(String[] args) {
    HeartBeatHandler.init();

    // Create question 1 heartbeat handler
    HttpHandler heartbeathandler = new HeartBeatHandler();

    // Create undertow server on {@link SERVER_PORT}
    Undertow.Builder builder = Undertow.builder().addHttpListener(SERVER_PORT,
        SERVER_IP);

    // Set question 1 handler
    builder
        .setHandler(Handlers.path().addPrefixPath(Q1_PATH, heartbeathandler))
        // .setIoThreads(2)
        .setWorkerThreads(180)
        .setServerOption(UndertowOptions.ALWAYS_SET_KEEP_ALIVE, false);

    // Build and start the server
    Undertow server = builder.build();
    server.start();
    System.out.println("Server started");
  }
}
