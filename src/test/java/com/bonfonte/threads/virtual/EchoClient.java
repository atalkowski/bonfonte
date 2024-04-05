package com.bonfonte.threads.virtual;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class EchoClient {
  public static void main(String[] args) throws IOException {
    String hostName = args.length > 0 ? args[0] : "localhost";
    int portNumber = args.length > 1 ? Integer.parseInt(args[1]) : 3003;
    try (
        Socket echoSocket = new Socket(hostName, portNumber);
        PrintWriter out =
            new PrintWriter(echoSocket.getOutputStream(), true);
        BufferedReader in =
            new BufferedReader(
                new InputStreamReader(echoSocket.getInputStream()));
    ) {
      BufferedReader stdIn =
          new BufferedReader(
              new InputStreamReader(System.in));
      String userInput;
      while ((userInput = stdIn.readLine()) != null) {
        out.println(userInput);
        System.out.println("echo: " + in.readLine());
        if (userInput.equals("bye")) break;
      }
    } catch (UnknownHostException e) {
      System.err.println("Don't know about host " + hostName);
      System.exit(1);
    } catch (IOException e) {
      System.err.println("Couldn't get I/O for the connection to " +
          hostName);
      System.exit(1);
    }
  }
}
