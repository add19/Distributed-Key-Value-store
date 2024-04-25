package RMIServer;

import RMIServer.Messages.LogEntry;
import RMIServer.Participants.PaxosNode;
import java.rmi.RemoteException;

/**
 * The remote data store class.
 */
public class RemoteDataStore extends PaxosNode implements IRemoteDataStore {
  public RemoteDataStore() throws RemoteException {
    super();
  }

  public void updateInstancesWithName(String serverName, double rate) {
    FAILURE_RATE = rate;
    name = serverName;
  }

  @Override
  public synchronized boolean put(String key, String value) throws RemoteException {
    // propose a value to all the acceptors which is a LogEntry
    LogEntry entry = new LogEntry("PUT", key, value);
    System.out.println("Received request " + entry);
    return super.checkConsensus(entry);
  }

  @Override
  public synchronized String get(String key) throws RemoteException {
    System.out.println("[" + getTimestamp() + "] => Received GET for key - " + key);
    if(!kvStore.containsKey(key)) {
      return "Key " + key + " doesn't exist in the store";
    }
    return kvStore.get(key);
  }

  @Override
  public synchronized boolean delete(String key) throws RemoteException {
    LogEntry entry = new LogEntry("DELETE", key, null);
    System.out.println("Received request " + entry);
    return super.checkConsensus(entry);
  }

  @Override
  public void addPaxosParticipant(IRemoteDataStore ds) {
    acceptors.add(ds);
  }
}
