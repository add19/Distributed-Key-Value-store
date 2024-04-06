package RMIServer;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

public class KeyValueServer {
  public static void main(String[] args) {
    if (args.length != 5) {
      System.out.println("Correct Usage: java RMIServer.KeyValueServer [port1] [port2] [port3] [port4] [port5]");
      System.exit(1);
    }
    ConfigReader reader = new ConfigReader();
    String filePath = "config.txt";
    Map<String, String> configMap = reader.readConfig(filePath);

    if(configMap.size() == 0) return;
    Registry registry = null;

    try {
      int registryPort = Integer.parseInt(configMap.get("REGISTRY_PORT"));
      registry = LocateRegistry.createRegistry(registryPort);
    } catch (RemoteException e) {
      System.out.println("Couldn't create registry...");
      return;
    }

    ICoordinator coordinator;
    try {
      coordinator = new Coordinator();
      int coordinatorPort = Integer.parseInt(configMap.get("COORDINATOR_PORT"));
      ICoordinator stub = (ICoordinator) UnicastRemoteObject.exportObject(coordinator, coordinatorPort);

      registry.rebind(configMap.get("COORDINATOR_NAME"), stub);
      System.out.println("Coordinator at port " + coordinatorPort + "  ready..");
    } catch (RemoteException e) {
      System.out.println(e.getMessage());
      return;
    }

    for(int i=0; i<5; i++) {
      try {
        int portNum = Integer.parseInt(args[i]);
        RemoteDataStore server = new RemoteDataStore();
        IRemoteDataStore stub = (IRemoteDataStore) UnicastRemoteObject.exportObject(server, portNum);

        String serverName = "kvstore" + (i + 1);
        registry.rebind(serverName, stub);
        coordinator.updateParticipantInfo(server, serverName);
        System.out.println("Server at port " + portNum + " " + serverName + " ready..");
      } catch (RemoteException e) {
        System.out.println("Couldn't start all the servers...");
      }
    }
  }
}
