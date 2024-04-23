package RMIClient;

import Client.AbstractClient;
import RMIServer.IRemoteDataStore;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.Scanner;

public class ClientWorker extends AbstractClient {

  @Override
  protected void displayUserChoices() {
    System.out.println("Specify operation:");
    System.out.println("Input [1] -> PUT");
    System.out.println("Input [2] -> GET");
    System.out.println("Input [3] -> DELETE");
    System.out.print("Enter your choice: ");
  }

  private void populateKeyValue(IRemoteDataStore remoteObj) throws RemoteException {
    System.out.println("[ " + getTimestamp() + " ]" + " => Pre-populating key value store");
    for(int i=0; i<100; i++) {
      String key = "KEY::" + i;
      String value = "VALUE::" + i;
      remoteObj.put(key, value);
    }

    System.out.println("[ " + getTimestamp() + " ]" + " => Doing Gets on pre-populated data");
    for(int i=0; i<100; i++) {
      String key = "KEY::" + i;
      String value = remoteObj.get(key);
      System.out.println("Tried Key: " + key + " Response: " + value);
    }

    System.out.println("[ " + getTimestamp() + " ]" + " => Deleting first 5 keys");
    for(int i=0; i<5; i++) {
      String key = "KEY::" + i;
      remoteObj.delete(key);
    }

    System.out.println("[ " + getTimestamp() + " ]" + " => Fetching first 5 keys");
    for(int i=0; i<5; i++) {
      String key = "KEY::" + i;
      String value = remoteObj.get(key);
      System.out.println("Tried Key: " + key + " Response: " + value);
    }

    System.out.println("[ " + getTimestamp() + " ]" + " => Fetching rest of the 95 keys");
    for(int i=5; i<100; i++) {
      String key = "KEY::" + i;
      String value = remoteObj.get(key);
      System.out.println("Tried Key: " + key + " Response: " + value);
    }
  }

  @Override
  public void startClient(String serverIp, int portNum) {
    IRemoteDataStore remoteObj = null;

    try (BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {
      Registry registry = LocateRegistry.getRegistry("localhost", portNum);
      System.out.println("Servers in the cluster are");

      try {
        Arrays.asList(registry.list()).forEach(System.out::println);
      } catch (RemoteException e) {
        System.out.println("Failed to list objects in the registry.");
        return;
      }
      Scanner sc = new Scanner(System.in);
      String name = "";
      while(true) {
        System.out.println("Please choose a server to connect");
        name = sc.next();
        if(!Arrays.asList(registry.list()).contains(name)) {
          System.out.println("Please enter a valid name");
        } else {
          break;
        }
      }
      remoteObj = (IRemoteDataStore) registry.lookup(name);
      System.out.println("Connecting with server - " +  name);

      System.out.println("Do you want to automatically pre-populate data to the key value store? (y/n)");
      while(true) {
        String prepopulateChoice = userInput.readLine();
        if(prepopulateChoice.equalsIgnoreCase("y")) {
          populateKeyValue(remoteObj);
          break;
        } else if(prepopulateChoice.equalsIgnoreCase("n")) {
          break;
        } else {
          System.out.println("Enter Y for yes or N for no");
        }
      }

      while(true) {
        displayUserChoices();

        String choice = userInput.readLine();
        switch (choice) {
          case "1":
            String key = getKey(userInput);
            String value = getValue(userInput);
            if(remoteObj.put(key, value)) {
              System.out.println("[ " + getTimestamp() + " ]" + " => Key " + key + " stored with value " + value);
            } else {
              System.out.println("Failed to write to key value store");
            }

            break;
          case "2":
            key = getKey(userInput);
            String answer = remoteObj.get(key);
            System.out.println("[ " + getTimestamp() + " ]" + " => Response for GET key : " + key + " => " + answer);
            break;
          case "3":
            key = getKey(userInput);

            if(remoteObj.delete(key)) {
              System.out.println("[ " + getTimestamp() + " ]" + " => Key " + key + " not present in the store");
            } else {
              System.out.println("Failed to delete from key value store");
            }
            break;
          default:
            System.out.println("Invalid choice. Please enter 1, 2, 3");
        }
      }
    } catch (RemoteException e) {
      System.out.println("Server seems to be offline..." + e);
    } catch (MalformedURLException e) {
      System.out.println("Invalid URL..." + e.getMessage());
    } catch (NotBoundException e) {
      System.out.println("Not bound exception..." + e.getMessage());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
