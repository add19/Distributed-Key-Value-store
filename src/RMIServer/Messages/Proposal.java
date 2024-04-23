package RMIServer.Messages;

import java.io.Serializable;

public class Proposal implements Serializable {
  long proposalId;
  LogEntry entry;

  public Proposal(long proposalId, LogEntry entry) {
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
