package RMIServer;

import java.util.UUID;

public class Transaction {
  UUID id;
  String operation;
  String[] operands;

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
