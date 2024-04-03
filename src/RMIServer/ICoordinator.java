package RMIServer;

public interface ICoordinator extends IRemoteDataStore {
  void updateParticipantInfo(IRemoteDataStore dataStore, int portNum);

  void canCommit();

  void doCommit();

  void doAbort();

}
