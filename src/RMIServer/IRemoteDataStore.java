package RMIServer;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRemoteDataStore extends Remote {
  void updateCoordinator(ICoordinator coordinator) throws RemoteException;
  /**
   * Inserts a given key and assigns it a given value in the data store.
   *
   * @param key The key to be stored.
   * @param value The associated value to be stored.
   */
  void put(String key, String value) throws RemoteException;

  /**
   * Gets the value for a given key from the data store.
   *
   * @param key the key whose value is to be fetched.
   * @return A message containing the corresponding value stored against the key in the data store.
   */
  String get(String key) throws RemoteException;

  boolean canCommit(Transaction transaction) throws RemoteException;

  void doCommit() throws RemoteException;

  void doAbort() throws RemoteException;

  /**
   * Deletes the given key and its corresponding value from the data store.
   *
   * @param key the key to be deleted.
   * @return A message indicating whether the operation is successful or not.
   */
  String delete(String key) throws RemoteException;
}
