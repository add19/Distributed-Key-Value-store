package RMIServer;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class Coordinator extends RemoteDataStore implements ICoordinator {
  private final List<IRemoteDataStore> participants;
  int[] ports;

  public Coordinator() throws RemoteException {
    super();
    this.participants = new ArrayList<>();
    ports = new int[5];
  }

  @Override
  public void updateParticipantInfo(IRemoteDataStore dataStore, int portNum)
    throws RemoteException {
    participants.add(dataStore);
    dataStore.updateCoordinator(this);
    ports[participants.size() - 1] = portNum;
  }

  @Override
  public void updateWithClientRequest(String operation, String key, String value)
    throws RemoteException {
    this.canCommit(operation, key, value);
  }

  @Override
  public boolean canCommit(String operation, String key, String value) throws RemoteException {
    for(IRemoteDataStore ds:participants) {
      if(!ds.canCommit(operation, key, value)) {
        this.doAbort();
        return false;
      }
    }
    this.doCommit();
    return true;
  }

  @Override
  public void doCommit() throws RemoteException {
    for(IRemoteDataStore ds:participants) {
//       ds.commit();
      System.out.println("Committing transaction");
      ds.doCommit();
    }
  }

  @Override
  public void doAbort() throws RemoteException {
    for(IRemoteDataStore ds:participants) {
      // ds.abort
      ds.doAbort();
    }
  }
}
