package RMIServer.Participants;

import RMIServer.Messages.PaxosMessage;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This represents the interface of a Paxos Acceptor and the operations associated with it.
 */
public interface IAcceptor extends Remote {

  /**
   * Receives a paxos prepare message from the proposer.
   *
   * @param proposer is the proposer to whom the acceptor should respond to.
   * @param message prepare paxos message.
   */
  void receivePrepare(IProposer proposer, PaxosMessage message) throws RemoteException;

  /**
   * Receives the accept request from the proposer.
   *
   * @param proposer is the proposer to whom the acceptor should respond to.
   * @param message accept request paxos message.
   */
  void receiveAcceptRequest(IProposer proposer, PaxosMessage message) throws RemoteException;
}
