package RMIServer.Participants;

import RMIServer.IRemoteDataStore;
import RMIServer.Messages.LogEntry;
import RMIServer.Messages.MessageType;
import RMIServer.Messages.PaxosMessage;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A paxos node that implements all the learner, proposer and acceptor paxos roles.
 */
public class PaxosNode implements ILearner, IProposer, IAcceptor {
  protected final ConcurrentMap<String, String> kvStore;
  protected Map<Long, LogEntry> acceptedLogs;
  protected List<IRemoteDataStore> acceptors;
  private int countPromises;
  private int countAccepts;
  private long lastAcceptedId;
  private final Random random;

  protected String name;
  protected double FAILURE_RATE;

  public PaxosNode() {
    acceptedLogs = new HashMap<>();
    acceptors = new ArrayList<>();
    kvStore = new ConcurrentHashMap<>();
    random = new Random();
  }

  protected String getTimestamp() {
    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
  }

  private void resetCounters() {
    this.countAccepts = 0;
    this.countPromises = 0;
  }

  public boolean checkConsensus(LogEntry entry) throws RemoteException {
    PaxosMessage message = new PaxosMessage(MessageType.PREPARE, System.nanoTime(), entry);
    this.sendPrepare(message);
    if(this.countPromises + 1 > (acceptors.size()) / 2) {
       this.sendAcceptRequest(new PaxosMessage(MessageType.ACCEPT, message.getProposalId(), entry));
       if(this.countAccepts + 1 > (acceptors.size()) / 2) {
         this.updateLearner(message);
         for(IRemoteDataStore ds:acceptors) {
           ds.updateLearner(message);
           lastAcceptedId = message.getProposalId();
           this.acceptedLogs.put(lastAcceptedId, entry);
         }
       } else {
         System.out.println("[ " + this.name + " ]: No consensus because received " +
            this.countAccepts + " for " + entry);
         resetCounters();
         return false;
       }
    } else {
      System.out.println("[ " + this.name + " ]: No consensus because received " +
          this.countPromises + " promise for " + entry);
      resetCounters();
      return false;
    }
    resetCounters();
    System.out.println("[ " + this.name + " ]: Consensus reached for log " + entry);
    return true;
  }

  @Override
  public void receivePrepare(IProposer proposer, PaxosMessage message) throws RemoteException {
    if(lastAcceptedId >= message.getProposalId()) {
      return;
    }

    // random failures
    double genVal = random.nextDouble();
    if (genVal < FAILURE_RATE) {
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
    if (genVal < FAILURE_RATE) {
      System.out.println("Acceptor @ " + name +  "  throwing exception!");
      throw new RuntimeException("Acceptor failure");
    }

    PaxosMessage message1 = new PaxosMessage(MessageType.ACCEPTED, message.getProposalId(), message.getLogEntry());
    proposer.receiveAccept(message1);
  }

  @Override
  public void updateLearner(PaxosMessage message) throws RemoteException {
    LogEntry entry = message.getLogEntry();
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
        System.out.println("[ PREPARE " + this.name + " ]: " + "Looks like an acceptor didn't respond");
      }
    }
  }

  @Override
  public void sendAcceptRequest(PaxosMessage message) throws RemoteException {
    for(IRemoteDataStore ds:acceptors) {
      try {
        ds.receiveAcceptRequest(this, message);
      } catch (RuntimeException e) {
        System.out.println("[ ACCEPT_REQUEST " + this.name + " ]: " + "Looks like an acceptor didn't respond");
      }
    }
  }

  @Override
  public void receivePromise(PaxosMessage message) throws RemoteException {
    this.countPromises++;
  }

  @Override
  public void receiveAccept(PaxosMessage message) throws RemoteException {
    this.countAccepts++;
  }
}
