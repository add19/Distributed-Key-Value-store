package RMIServer.Messages;

/**
 * Represents the paxos message type.
 */
public enum MessageType {
  PREPARE,
  PROMISE,
  ACCEPT,
  ACCEPTED,
  LEARN
}
