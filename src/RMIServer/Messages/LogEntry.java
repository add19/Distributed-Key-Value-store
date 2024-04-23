package RMIServer.Messages;

import java.io.Serializable;
import java.util.UUID;

public class LogEntry implements Serializable {
  UUID id; // id of the operation
  String operation; // type of operation
  String[] operands; // the operands of the operation

  /**
   * Initializes transaction object based on the key value store operation
   * @param operation the operation for transaction
   * @param key key to be modified.
   * @param value value against the key.
   */
  public LogEntry(String operation, String key, String value) {
    this.id = UUID.randomUUID();
    this.operation = operation;
    if(operation.equals("PUT")) {
      this.operands = new String[2];
      this.operands[0] = key;
      this.operands[1] = value;
    } else {
      this.operands = new String[1];
      this.operands[0] = key;
    }
  }

  public String getOperation() {
    return operation;
  }

  public String[] getOperands() {
    return operands;
  }

  @Override
  public String toString() {
    return id + " - " + operation + " - " + operands[0];
  }
}
