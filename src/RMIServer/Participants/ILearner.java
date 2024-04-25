package RMIServer.Participants;

import RMIServer.Messages.PaxosMessage;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This represents the interface of a Paxos Learner and the operations associated with it.
 */
public interface ILearner extends Remote {

  /**
   * Updates the learner with the latest message or log entry to be committed to the data store.
   * @param message the message which has the log entry to be committed.
   */
  void updateLearner(PaxosMessage message) throws RemoteException;
}
