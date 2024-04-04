package RMIClient;

import Client.AbstractClient;

public class KeyValueClient {

  public static void main(String[] args) {
    if (args.length != 1) {
      System.out.println("Correct Usage: java RMIClient.KeyValueClient [registry port]");
      System.exit(1);
    }

    AbstractClient abstractClient = new ClientWorker();
    int portNo = Integer.parseInt(args[0]);

    abstractClient.startClient("localhost", portNo);
  }
}
