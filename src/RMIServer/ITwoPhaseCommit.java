package RMIServer;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Represents the operations associated with 2 phase commit protocol implementation.
 */
public interface ITwoPhaseCommit extends Remote {

  /**
   * This method is used to update the participants in the cluster about their assigned names and
   * the coordinator with whom they have to communicate for 2 phase commit.
   *
   * @param coordinator The coordinator which is to be used to coordinate 2 phase commit protocol.
   * @param serverName the name of the server which is going to be the participant in the network.
   * @throws RemoteException
   */
  void updateInstancesWith2PCInfo(ICoordinator coordinator, String serverName)
      throws RemoteException;

  /**
   * Asks the participants if they can commit the given transaction.
   *
   * @param transaction the transaction to be executed by the participants.
   * @return A vote indicating whether the transaction can commit or abort the transaction.
   * @throws RemoteException
   */
  Vote canCommit(Transaction transaction) throws RemoteException;

  /**
   * Commits the prepared transaction by writing into permanent store.
   * @throws RemoteException
   */
  void doCommit() throws RemoteException;

  /**
   * Aborts prepared transactions.
   *
   * @throws RemoteException
   */
  void doAbort() throws RemoteException;
}
