package RMIServer.Participants;


import RMIServer.Messages.PaxosMessage;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This represents the interface of a Paxos Proposer and the operations associated with it.
 */
public interface IProposer extends Remote {

  /**
   * Sends a paxos prepare message to all the acceptors.
   *
   * @param message the prepare message to be sent to the acceptors.
   */
  void sendPrepare(PaxosMessage message) throws RemoteException;

  /**
   * Sends a paxos accept request to all the acceptors.
   *
   * @param message the accept request paxos message to be sent to the acceptors.
   */
  void sendAcceptRequest(PaxosMessage message) throws RemoteException;

  /**
   * Receives the promise from the paxos acceptors.
   *
   * @param message the promise message received from acceptors.
   */
  void receivePromise(PaxosMessage message) throws RemoteException;

  /**
   * Receives accept message from the paxos acceptors.
   * @param message the message received from acceptors.
   */
  void receiveAccept(PaxosMessage message) throws RemoteException;
}
