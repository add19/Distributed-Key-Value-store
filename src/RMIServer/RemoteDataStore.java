package RMIServer;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RemoteDataStore implements IRemoteDataStore {
  private final ConcurrentMap<String, String> kvStore;
  public RemoteDataStore() throws RemoteException {
    super();
    kvStore = new ConcurrentHashMap<>();
  }

  private String getTimestamp() {
    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
  }

  @Override
  public synchronized void put(String key, String value) throws RemoteException {
    System.out.println("[" + getTimestamp() + "] => Received PUT for key - " + key + " value - " + value);
    kvStore.put(key, value);
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
  public synchronized String delete(String key) throws RemoteException {
    System.out.println("[" + getTimestamp() + "] => Received DELETE for key - " + key);

    if(kvStore.containsKey(key)) {
      kvStore.remove(key);
      return "Deleted key " + key;
    }
    return "Key " + key + " not found";
  }
}
