package RMIServer;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

public class KeyValueServer {

  public static void main(String[] args) {
    if (args.length != 6) {
      System.out.println("Correct Usage: java RMIServer.KeyValueServer [registry port] [port1] [port2] [port3] [port4] [port5]");
      System.exit(1);
    }
    int portNum = Integer.parseInt(args[0]);
    Registry registry = null;

    try {
      registry = LocateRegistry.createRegistry(portNum);
    } catch (RemoteException e) {
      System.out.println("Couldn't create registry...");
      return;
    }

    ICoordinator coordinator;
    try {
      coordinator = new Coordinator();
      ICoordinator stub = (ICoordinator) UnicastRemoteObject.exportObject(coordinator, 8080);

      registry.rebind("coordinator", stub);
      System.out.println("Coordinator at port " + 8080 + "  ready..");
    } catch (RemoteException e) {
      System.out.println(e.getMessage());
      return;
    }


    for(int i=1; i<6; i++) {
      try {
        portNum = Integer.parseInt(args[i]);
        RemoteDataStore server = new RemoteDataStore();
        IRemoteDataStore stub = (IRemoteDataStore) UnicastRemoteObject.exportObject(server, 0);

        registry.rebind("kvstore" + i, stub);
        coordinator.updateParticipantInfo(server, portNum);
        System.out.println("Server at port " + portNum + "  ready..");
      } catch (RemoteException e) {
        System.out.println("Couldn't start all the servers...");
      }
    }

      // initialize all 5 servers here
      // assign 1 of those servers as the coordinators
      // Client will send request to any of the server which will pass this to coordinator server
      // Co-ordinator server will then send a message asking for votes(canCommit)
      // participants will send yes or no depending on their situation.
      // Co-ordinator sends abort(if any no) or commit to all the participants which then write to the store.

      /**
       * This will involve the following,
       * 1. Creating a coordinator
       * 2. Creating a request type object
       * 3. Passing this request across the participant to co-ordinator and vice versa
       * 4. There would be a thread sleep involved in case of waiting for commit messages(check example implementations)
       * 5. Temporary storage of the participant store
       * 6. Writing out contents of this store into the key value store upon commit message reception/clearing out in case of abort.
       */
      //trying to print
      try {
        Arrays.asList(registry.list()).forEach(System.out::println);
      } catch (RemoteException e) {
        System.out.println("Failed to list objects in the registry.");
      }

  }
}
