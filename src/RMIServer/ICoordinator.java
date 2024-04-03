package RMIServer;

import java.rmi.RemoteException;

public interface ICoordinator extends IRemoteDataStore {
  void updateParticipantInfo(IRemoteDataStore dataStore, int portNum) throws RemoteException;

  void canCommit() throws RemoteException;

  void doCommit() throws RemoteException;

  void doAbort() throws RemoteException;

}
