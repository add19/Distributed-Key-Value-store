package RMIServer;

import java.rmi.RemoteException;

public interface ICoordinator extends IRemoteDataStore {
  void updateParticipantInfo(IRemoteDataStore dataStore, int portNum) throws RemoteException;
  void updateWithClientRequest(String operation, String key, String value) throws RemoteException;

}
