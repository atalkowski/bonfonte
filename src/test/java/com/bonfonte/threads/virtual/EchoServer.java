package com.bonfonte.threads.virtual;

import java.io.IOException;

public class EchoServer {
  public static void main(String[] args) throws IOException {
    int portNumber = args.length < 1 ? 3003 : Integer.parseInt(args[0]);
    /*
     * Note thread.ofVirtual is not supported in current Java build.
     * try (
     * ServerSocket serverSocket =
     * new ServerSocket(portNumber);
     * ) {
     * while (true) {
     * Socket clientSocket = serverSocket.accept();
     * // Accept incoming connections
     * // Start a service thread
     * Thread.ofVirtual().start(() -> {
     * try (
     * PrintWriter out =
     * new PrintWriter(clientSocket.getOutputStream(), true);
     * BufferedReader in = new BufferedReader(
     * new InputStreamReader(clientSocket.getInputStream()));
     * ) {
     * String inputLine;
     * while ((inputLine = in.readLine()) != null) {
     * System.out.println(inputLine);
     * out.println(inputLine);
     * }
     * 
     * } catch (IOException e) {
     * e.printStackTrace();
     * }
     * });
     * }
     * } catch (IOException e) {
     * System.out.println("Exception caught when trying to listen on port "
     * + portNumber + " or listening for a connection");
     * System.out.println(e.getMessage());
     * }
     */
  }
}
