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
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PaxosNode implements ILearner, IProposer, IAcceptor {
  protected final ConcurrentMap<String, String> kvStore;
  protected Map<Long, LogEntry> acceptedLogs;
  protected List<IRemoteDataStore> acceptors;
  private int countPromises;
  private int countAccepts;
  private long lastAcceptedId;
  private Random random;

  protected String name;

  public PaxosNode() {
    acceptedLogs = new HashMap<>();
    acceptors = new ArrayList<>();
    kvStore = new ConcurrentHashMap<>();
    random = new Random();

  }

  public boolean checkConsensus(LogEntry entry) throws RemoteException {
    PaxosMessage message = new PaxosMessage(MessageType.PREPARE, System.nanoTime(), entry);
    this.sendPrepare(message); // returns the number of promises
    // if majority, then sendAccept
    if(this.countPromises + 1 >= (acceptors.size()) / 2) {
       this.sendAcceptRequest(new PaxosMessage(MessageType.ACCEPT, message.getProposalId(), entry));
       //TODO: Handling lost updates.
       if(this.countAccepts + 1 >= (acceptors.size()) / 2) {
         this.updateLearner(message); // calling commit.
         for(IRemoteDataStore ds:acceptors) {
           ds.updateLearner(message);
           lastAcceptedId = message.getProposalId();
           this.acceptedLogs.put(lastAcceptedId, entry);
         }
       } else {
         System.out.println("[ " + this.name + " ]: Couldn't accept message " + entry);
         System.out.println(this.countAccepts);
         System.out.println(this.countPromises);
         return false;
       }
    } else {
      System.out.println("[ " + this.name + " ]: Couldn't reach consensus " + entry);
      return false;
    }
    System.out.println("[ " + this.name + " ]: Consensus reached for log " + entry);
    // if no majority, abort
    // if majority for sendAccept, then call for commit and updateLearner.
    return true;
  }

  @Override
  public void receivePrepare(IProposer proposer, PaxosMessage message) throws RemoteException {
    //check the last accepted id as well.
    if(lastAcceptedId >= message.getProposalId()) {
      return;
    }

    // random failures
    double genVal = random.nextDouble();
    if (genVal < 0.4) {
      System.out.println("Acceptor @ " + name +  "  throwing exception!");
      throw new RuntimeException("Acceptor failure");
    }

    PaxosMessage message1 = new PaxosMessage(MessageType.PROMISE, message.getProposalId(), message.getLogEntry());
    proposer.receivePromise(message1);
  }

  @Override
  public void receiveAcceptRequest(IProposer proposer, PaxosMessage message) throws RemoteException {
    if(lastAcceptedId >= message.getProposalId()) {
      return;
    }

    // random failures
    double genVal = random.nextDouble();
    if (genVal < 0.4) {
      System.out.println("Acceptor @ " + name +  "  throwing exception!");
      throw new RuntimeException("Acceptor failure");
    }

    proposer.receiveAccept(message);
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
      try {
        ds.receivePrepare(this, message);
      } catch (RuntimeException e) {
        System.out.println("Looks like an acceptor didn't respond");
      }
    }
  }

  @Override
  public void sendAcceptRequest(PaxosMessage message) throws RemoteException {
    for(IRemoteDataStore ds:acceptors) {
      try {
        ds.receiveAcceptRequest(this, message);
      } catch (RuntimeException e) {
        System.out.println("[ " + this.name + " ]: " + "Looks like an acceptor didn't respond");
      }
    }
  }

  @Override
  public void receivePromise(PaxosMessage message) throws RemoteException {
    // check the log value and reach consensus on any missed out values.
    this.countPromises++;
  }

  @Override
  public void receiveAccept(PaxosMessage message) throws RemoteException {
    //check the last accepted id as well.
    this.countAccepts++;
  }
}
