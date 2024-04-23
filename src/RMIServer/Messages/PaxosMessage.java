package RMIServer.Messages;

import java.io.Serializable;

public class PaxosMessage implements Serializable {
  MessageType type;
  long proposalId;
  LogEntry entry;

  public PaxosMessage(MessageType type, long proposalId, LogEntry entry) {
    this.type = type;
    this.proposalId = proposalId;
    this.entry = entry;
  }

  public long getProposalId() {
    return proposalId;
  }

  public LogEntry getLogEntry() {
    return entry;
  }
}
