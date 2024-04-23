package RMIServer;

import RMIServer.Participants.IAcceptor;
import RMIServer.Participants.ILearner;
import RMIServer.Participants.IProposer;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Represents the remote data store type instances and the possible operations on them.
 */
public interface IRemoteDataStore extends Remote, IProposer, ILearner, IAcceptor {
  /**
   * Inserts a given key and assigns it a given value in the data store.
   *
   * @param key The key to be stored.
   * @param value The associated value to be stored.
   */
  boolean put(String key, String value) throws RemoteException;

  /**
   * Gets the value for a given key from the data store.
   *
   * @param key the key whose value is to be fetched.
   * @return A message containing the corresponding value stored against the key in the data store.
   */
  String get(String key) throws RemoteException;

  /**
   * Deletes the given key and its corresponding value from the data store.
   *
   * @param key the key to be deleted.
   */
  boolean delete(String key) throws RemoteException;

  void updateInstancesWithName(String name) throws  RemoteException;

  void addPaxosParticipant(IRemoteDataStore ds) throws RemoteException; // this should be in the paxos interfaces
}
