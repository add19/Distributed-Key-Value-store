package RMIServer.Participants;


import RMIServer.Messages.PaxosMessage;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IProposer extends Remote {
  void sendPrepare(PaxosMessage message) throws RemoteException;

  void sendAcceptRequest(PaxosMessage message) throws RemoteException;

  void receivePromise(PaxosMessage message) throws RemoteException;

  void receiveAccept(PaxosMessage message) throws RemoteException;
}
