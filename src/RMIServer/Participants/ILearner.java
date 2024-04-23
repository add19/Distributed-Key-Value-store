package RMIServer.Participants;

import RMIServer.Messages.PaxosMessage;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ILearner extends Remote {
  void updateLearner(PaxosMessage message) throws RemoteException;
}
