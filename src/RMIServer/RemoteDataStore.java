package RMIServer;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The remote data store class.
 */
public class RemoteDataStore implements IRemoteDataStore {
  private final ConcurrentMap<String, String> kvStore;
  private final List<Transaction> transactions;

  public RemoteDataStore() throws RemoteException {
    super();
    kvStore = new ConcurrentHashMap<>();
    transactions = new ArrayList<>();
  }

  private String getTimestamp() {
    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
  }

  ICoordinator coordinator;
  String name;

  @Override
  public void updateInstancesWith2PCInfo(ICoordinator coordinator, String serverName) {
    this.name = serverName;
    this.coordinator = coordinator;
  }

  @Override
  public synchronized void put(String key, String value) throws RemoteException {
    coordinator.updateWithClientRequest("PUT", key, value);
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
  public synchronized Vote canCommit(Transaction transaction) throws RemoteException {
    if(transaction.operation.equals("DELETE") && !kvStore.containsKey(transaction.operands[0])) {
      return Vote.NO;
    }
    //preparing transaction
    transactions.add(transaction);
    return Vote.YES;
  }

  @Override
  public synchronized void doCommit() throws RemoteException {
    for(Transaction transaction:transactions) {
      System.out.println("[" + this.name + "] Committing " + transaction);
      if(transaction.operation.equals("PUT")) {
        System.out.println("[" + getTimestamp() + "] => " + this.name + " Received PUT for key - " + transaction.operands[0] + " value - " + transaction.operands[1]);
        kvStore.put(transaction.operands[0], transaction.operands[1]);
      } else {
        System.out.println("[" + getTimestamp() + "] => " + this.name + " Received DELETE for key - " + transaction.operands[0]);
        kvStore.remove(transaction.operands[0]);
      }
    }
    transactions.clear();
  }

  @Override
  public synchronized void doAbort() throws RemoteException {
    transactions.clear();
  }

  @Override
  public synchronized void delete(String key) throws RemoteException {
    coordinator.updateWithClientRequest("DELETE", key, null);
  }
}
