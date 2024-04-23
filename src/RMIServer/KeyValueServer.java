package RMIServer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.List;
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

    /**
     * 1. Define the Paxos Node interfaces for proposers, learners and acceptors
     * 2. Create implementations for promise, accept-request and accept messages
     * 3. Base the flow of events on log records. Define counters for promise ids.
     * 4. Define the logic for sending the previously accepted messages id along with promise messages
     * 5. Read should be from all the nodes and the majority value should be considered.
     */
    for(int i=0; i<5; i++) {
      try {
        int portNum = Integer.parseInt(args[i]);
        RemoteDataStore server = new RemoteDataStore();
        IRemoteDataStore stub = (IRemoteDataStore) UnicastRemoteObject.exportObject(server, portNum);

        String serverName = "kvstore" + (i + 1);
        registry.rebind(serverName, stub);
        System.out.println("Server at port " + portNum + " " + serverName + " ready..");
        stub.updateInstancesWithName(serverName);
      } catch (RemoteException e) {
        System.out.println("Couldn't start all the servers..." + e);
      }
    }

    try {
      List<String> servers = Arrays.asList(registry.list());
      for(int i=0; i< servers.size(); i++) {
        for(int j=0; j<servers.size(); j++) {
          if(i == j) {
            continue;
          }
          IRemoteDataStore ds = (IRemoteDataStore) registry.lookup(servers.get(i));
          ds.addPaxosParticipant((IRemoteDataStore) registry.lookup(servers.get(j)));
        }
      }
    } catch (RemoteException e) {
      System.out.println("Failed to list objects in the registry.");
    } catch (NotBoundException e) {
      throw new RuntimeException(e);
    }
  }
}
