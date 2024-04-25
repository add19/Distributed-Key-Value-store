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

  /**
   * Updates the remote interface with the name of the server and the failure rate configured.
   *
   * @param name name of the server
   * @param FAILURE_RATE the rate at which the server fails. This is a pre-configured value
   *                     against which a random number is generated and random exceptions are
   *                     thrown based on the comparison of these 2.
   * @throws RemoteException
   */
  void updateInstancesWithName(String name, double FAILURE_RATE) throws  RemoteException;

  /**
   * Since each server is an extension of a paxos proposer, this method adds to a list of the
   * acceptors.
   * @param ds the data store to be added as a paxos participant.
   * @throws RemoteException
   */
  void addPaxosParticipant(IRemoteDataStore ds) throws RemoteException;
}
