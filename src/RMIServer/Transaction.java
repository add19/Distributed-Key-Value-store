package RMIServer;

import java.util.UUID;

/**
 * This class represents the operations on key value store encapsulated as a transaction.
 */
public class Transaction {
  UUID id; // id of the transaction
  String operation; // type of operation
  String[] operands; // the operands of the transaction

  /**
   * Initializes transaction object based on the key value store operation
   * @param operation the operation for transaction
   * @param key key to be modified.
   * @param value value against the key.
   */
  public Transaction(String operation, String key, String value) {
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

  @Override
  public String toString() {
    return id + " - " + operation + " - " + operands[0];
  }
}
