package RMIServer;

import java.rmi.RemoteException;

/**
 * Represents the coordinator and the associated operations for any coordinator in order to facilitate
 * 2 phase commit protocol.
 */
public interface ICoordinator extends IRemoteDataStore {

  /**
   * Updates the coordinator about the participants in the cluster and the names of the servers.
   *
   * @param dataStore the remote data store instance.
   * @param name the name of the instance which is bound to registry.
   * @throws RemoteException
   */
  void updateParticipantInfo(IRemoteDataStore dataStore, String name) throws RemoteException;

  /**
   * Notify the coordinator about the incoming transaction/client request from any of the remote
   * data store instance.
   *
   * @param operation indicates the type of write operation - delete or put
   * @param key the key to be operated on.
   * @param value value of the associated key.
   * @throws RemoteException
   */
  void updateWithClientRequest(String operation, String key, String value) throws RemoteException;
}
