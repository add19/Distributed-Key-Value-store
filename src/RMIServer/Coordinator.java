package RMIServer;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the coordinator in the cluster. It acts like a server so extends the RemoteDataStore
 * class and implements additional functionality for acting as a coordinator.
 */
public class Coordinator extends RemoteDataStore implements ICoordinator {
  // list of the participants.
  private final List<IRemoteDataStore> participants;

  public Coordinator() throws RemoteException {
    super();
    this.participants = new ArrayList<>();
  }

  @Override
  public synchronized void updateParticipantInfo(IRemoteDataStore dataStore, String serverName)
    throws RemoteException {
    participants.add(dataStore);
    dataStore.updateInstancesWith2PCInfo(this, serverName);
  }

  @Override
  public synchronized void updateWithClientRequest(String operation, String key, String value)
    throws RemoteException {
    Transaction transaction = new Transaction(operation, key, value);
    this.canCommit(transaction);
  }

  @Override
  public synchronized Vote canCommit(Transaction transaction) throws RemoteException {
    for(IRemoteDataStore ds:participants) {
      try{
        Thread.sleep(10);
        if(ds.canCommit(transaction) == Vote.NO) {
          this.doAbort();
          System.out.println("[COORDINATOR]: Received NO from one of the participants, aborting transaction " + transaction);
          return Vote.NO;
        }
      } catch(InterruptedException ex) {
        this.doAbort();
        System.out.println("Server timed out for transaction - " + transaction);
        return Vote.NO;
      }
    }
    this.doCommit();
    return Vote.YES;
  }

  @Override
  public synchronized void doCommit() throws RemoteException {
    for(IRemoteDataStore ds:participants) {
      ds.doCommit();
    }
  }

  @Override
  public synchronized void doAbort() throws RemoteException {
    for(IRemoteDataStore ds:participants) {
      ds.doAbort();
    }
  }
}
