package RMIServer;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class Coordinator extends RemoteDataStore implements ICoordinator {
  private List<IRemoteDataStore> participants;
  int[] ports;

  public Coordinator() throws RemoteException {
    super();
    this.participants = new ArrayList<>();
    ports = new int[5];
  }

  @Override
  public void updateParticipantInfo(IRemoteDataStore dataStore, int portNum) {
    participants.add(dataStore);
    ports[participants.size() - 1] = portNum;
  }

  @Override
  public void canCommit() {
    for(IRemoteDataStore ds:participants) {
      // ds.canCommit
    }
  }

  @Override
  public void doCommit() {
    for(IRemoteDataStore ds:participants) {
      // ds.commit
    }
  }

  @Override
  public void doAbort() {
    for(IRemoteDataStore ds:participants) {
      // ds.abort
    }
  }
}
