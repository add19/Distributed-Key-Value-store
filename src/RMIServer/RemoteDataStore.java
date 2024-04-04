package RMIServer;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RemoteDataStore implements IRemoteDataStore {
  private final ConcurrentMap<String, String> kvStore;
  private final List<String[]> transactions;

  public RemoteDataStore() throws RemoteException {
    super();
    kvStore = new ConcurrentHashMap<>();
    transactions = new ArrayList<>();
  }

  private String getTimestamp() {
    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
  }

  ICoordinator coordinator;

  @Override
  public void updateCoordinator(ICoordinator coordinator) {
    this.coordinator = coordinator;
  }

  @Override
  public synchronized void put(String key, String value) throws RemoteException {
    System.out.println("[" + getTimestamp() + "] => Received PUT for key - " + key + " value - " + value);
    // send message to coordinator using canCommit...
    coordinator.updateWithClientRequest("PUT", key, value);
//    kvStore.put(key, value);
  }

  @Override
  public String get(String key) throws RemoteException {
    System.out.println("[" + getTimestamp() + "] => Received GET for key - " + key);
    if(!kvStore.containsKey(key)) {
      return "Key " + key + " doesn't exist in the store";
    }
    return kvStore.get(key);
  }

  @Override
  public boolean canCommit(String operation, String key, String value) throws RemoteException {
    if(operation.equals("PUT"))
      transactions.add(new String[]{operation, key, value});
    else {
      if(!kvStore.containsKey(key)) {
        return false;
      }
      transactions.add(new String[]{operation, key});
    }
    return true;
  }

  @Override
  public void doCommit() throws RemoteException {
    for(String[] transaction:transactions) {
      if(transaction[0].equals("PUT")) {
        kvStore.put(transaction[1], transaction[2]);
      } else {
        kvStore.remove(transaction[1]);
      }
    }
    transactions.clear();
  }

  @Override
  public void doAbort() throws RemoteException {
    transactions.clear();
  }

  @Override
  public synchronized String delete(String key) throws RemoteException {
    System.out.println("[" + getTimestamp() + "] => Received DELETE for key - " + key);
    coordinator.updateWithClientRequest("DELETE", key, null);
    transactions.add(new String[]{"DELETE", key});

//    if(kvStore.containsKey(key)) {
//      kvStore.remove(key);
//      return "Deleted key " + key;
//    }
    return "Key " + key + " not found";
  }
}
