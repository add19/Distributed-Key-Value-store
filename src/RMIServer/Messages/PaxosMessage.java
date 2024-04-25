package RMIServer.Messages;

import java.io.Serializable;

/**
 * Represents the paxos message which is passed amongst all the paxos participants.
 */
public class PaxosMessage implements Serializable {
  MessageType type;
  long proposalId;
  LogEntry entry;

  /**
   * Initializes the paxos message.
   *
   * @param type the message type.
   * @param proposalId the proposal id.
   * @param entry the log value to be agreed upon by the paxos nodes.
   */
  public PaxosMessage(MessageType type, long proposalId, LogEntry entry) {
    this.type = type;
    this.proposalId = proposalId;
    this.entry = entry;
  }

  /**
   * Gets the proposal id of the message.
   * @return the proposal id.
   */
  public long getProposalId() {
    return proposalId;
  }

  /**
   * Gets the log entry in the message.
   * @return the log entry object.
   */
  public LogEntry getLogEntry() {
    return entry;
  }
}
