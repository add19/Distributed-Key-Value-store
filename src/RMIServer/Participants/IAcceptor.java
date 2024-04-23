package RMIServer.Participants;

import RMIServer.Messages.PaxosMessage;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IAcceptor extends Remote {
  void receivePrepare(IProposer proposer, PaxosMessage message) throws RemoteException;
  void receiveAcceptRequest(IProposer proposer, PaxosMessage message) throws RemoteException;
}
