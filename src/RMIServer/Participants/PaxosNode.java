package RMIServer.Participants;

import RMIServer.IRemoteDataStore;
import RMIServer.Messages.LogEntry;
import RMIServer.Messages.MessageType;
import RMIServer.Messages.PaxosMessage;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PaxosNode implements ILearner, IProposer, IAcceptor {
  protected final ConcurrentMap<String, String> kvStore;
  protected Map<Long, LogEntry> acceptedLogs;
  protected List<IRemoteDataStore> acceptors;

  private int countPromises;
  private int countAccepts;
  public PaxosNode() {
    acceptedLogs = new HashMap<>();
    acceptors = new ArrayList<>();
    kvStore = new ConcurrentHashMap<>();
  }

  public void checkConsensus(LogEntry entry) throws RemoteException {
    PaxosMessage message = new PaxosMessage(MessageType.PREPARE, System.nanoTime(), entry);
    this.sendPrepare(message); // returns the number of promises
    // if majority, then sendAccept
    if(this.countPromises + 1 >= (acceptors.size()) / 2) {
       this.sendAcceptRequest(new PaxosMessage(MessageType.ACCEPT, message.getProposalId(), entry));
       if(this.countAccepts + 1 >= (acceptors.size()) / 2) {
         this.updateLearner(message); // calling commit.
       } else {
         System.out.println("Couldn't accept message " + entry);
         System.out.println(this.countAccepts);
         System.out.println(this.countPromises);
         return;
       }
    } else {
      System.out.println("Couldn't reach consensus " + entry);
      return;
    }
    System.out.println("Consensus reached for log " + entry);
    // if no majority, abort
    // if majority for sendAccept, then call for commit and updateLearner.
  }

  @Override
  public void receivePrepare(IProposer proposer, PaxosMessage message) throws RemoteException {
    //check the last accepted id as well.
    PaxosMessage message1 = new PaxosMessage(MessageType.PROMISE, message.getProposalId(), message.getLogEntry());
    proposer.receivePromise(message1);
  }

  @Override
  public void receiveAcceptRequest(IProposer proposer, PaxosMessage message) throws RemoteException {
    proposer.receiveAccept(message);
  }

  @Override
  public void receiveAccept(PaxosMessage message) throws RemoteException {
    //check the last accepted id as well.
    this.countAccepts++;
  }

  @Override
  public void updateLearner(PaxosMessage message) throws RemoteException {
    LogEntry entry = message.getLogEntry();
    //writing into kv store.
    String[] operands = entry.getOperands();
    if(entry.getOperation().equals("PUT")) {
      kvStore.put(operands[0], operands[1]);
    } else {
      kvStore.remove(operands[0]);
    }
  }

  @Override
  public void sendPrepare(PaxosMessage message) throws RemoteException {
    for(IRemoteDataStore ds:acceptors) {
      ds.receivePrepare(this, message);
    }
  }

  @Override
  public void sendAcceptRequest(PaxosMessage message) throws RemoteException {
    for(IRemoteDataStore ds:acceptors) {
      ds.receiveAcceptRequest(this, message);
    }
  }

  @Override
  public void receivePromise(PaxosMessage message) throws RemoteException {
    // check the log value and reach consensus on any missed out values.
    this.countPromises++;
  }
}
