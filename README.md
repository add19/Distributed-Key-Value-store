# Project owner
Aadish Deshpande (deshpande.aa@northeastern.edu)

# Project Readme

## Brief overview of the project
* This project is an extension of the distributed key value store that we have been building. This 
  time, to ensure that there is fault-tolerance paxos based consensus is implemented.
* There are 2 packages each for client and server, namely `RMIClient` and `RMIServer`

### Sample configuration

#### Project structure
* Following are the new additions to the existing project structure.
```bash
.
├── ExecutiveSummary.txt
├── RMIClient
│   ├── ClientWorker.java
│   └── KeyValueClient.java
└── RMIServer
    ├── ConfigReader.java
    ├── IRemoteDataStore.java
    ├── KeyValueServer.java
    ├── Messages
    │   ├── LogEntry.java
    │   ├── MessageType.java
    │   ├── PaxosMessage.java
    │   └── Proposal.java
    ├── Participants
    │   ├── IAcceptor.java
    │   ├── ILearner.java
    │   ├── IProposer.java
    │   └── PaxosNode.java
    ├── RemoteDataStore.java
    └── config.txt

```
* Compile the code using `javac RMIServer/*.java RMIClient/*.java`
* To start the 5 server cluster,
  * `java RMIServer.KeyValueServer <port-number 1> <port-number 2> <port-number 3> <port-number 4> <port-number 5>`
* The above command starts 5 instances and a coordinator and binds them to the registry.
* The registry port number can be found in the config.txt, this file is used to set up the coordinator port and the registry port number.
* To run the client
  * `java RMIClient.KeyValueClient <registry-port-number>`
* Input the name of any server instance to use(from rmi registry), the names would be displayed on the console for reference.
* All the client and server logs are generated on the console

## Executive Summary

### Assignment Overview:(Purpose and Scope)

The purpose of this assignment was to go beyond what we did in 2 phase commit protocol to now
ensure fault-tolerance along with availability. Fault tolerance is ensured by using a consensus 
algorithm such as Paxos. The application supports connecting multiple clients at a time to different
instances. In this, the client connects and sends a request to any server. Each server in the cluster
acts as a Paxos Node. Each Paxos Node can act as a Proposer, Acceptor or a Learner. In case of any 
write operations, the key value store which I understand to be as an extension of the Paxos nodes
initiates a consensus and then commits the values only after there is a consensus amongst all the nodes.
In case of failures, the correctly functioning nodes try to come to a consensus on the operation
in question. Each operation requested by the client is to be treated as a value in itself to be 
agreed upon.

### Technical impressions:

### Setup:
1. Each node in the cluster acts as a Paxos Proposer, Acceptor and Learner.
2. Each node can act as a proposer and hence stores the list of acceptors from the start time, which
    it uses to carry out multicast communication.
3. All these servers are part of the RMI registry bound at the start time. 
4. Client process can start and communicate with any of the servers provided as a user choice to it.
5. The state of the database is considered as a series of operations which result into the current state. 
6. Each operation requested from the client is written into a log and the paxos run is carried to agree
   upon this entry of the log.
7. The log entry is then executed by the learners to commit the transaction/requested operation from client
    into the key value store on each node.

The inspiration for this design comes from this google tech-talk - https://www.youtube.com/watch?v=d7nAGI_NZPk

### Flow:
* Whenever a client requests for certain PUT or DELETE operation, then in that case, the node to which
   the client sends request to acts as a paxos proposer.
* The paxos proposer then sends a Prepare message to the list of acceptor nodes.
* The Prepare message sent by the proposer is having a unique proposal ID which is generated on increments
  of nanosecond time precision.
* The Acceptor on receiving the Prepare message then checks with the last proposal id that it had
   accepted. 
  * If there is no previously accepted promise, in that case, the current prepare message(or proposal)
    is accepted and a promise message is sent to the proposer, to indicate the acceptor will reject
    any requests with lesser proposal ID
  * If there is a previously accepted promise, the current prepare message is accepted and a promise
    is sent if the proposal id is greater than last accepted id. Otherwise, this proposal is rejected
    as the acceptor promised to reject any requests with lesser id.
* The proposer upon reception from responses from acceptors, then checks if it has a majority of 
  promise messages or not.
  * If there are not enough promise messages(which may be due to random failures), then the proposer
    considers that there is no consensus and aborts the process.
  * If there is a majority of promise messages received from the acceptors then the proposer proceeds
    to the next phase of the process
* Each operation request is treated as a log entry, and the log entry is next passed on in the accept-request
  message sent to the acceptor nodes.
* The acceptor nodes on the reception of the accept-request message then again performs a check on the proposal
  id it receives.
  * If there is no previously accepted request, in that case, the current accept-request message
    is accepted and an accept message is sent to the proposer as well as the learner.
    * If there is a previously accepted promise, the current accept-request message is accepted and an accept
      is sent to the proposer if the proposal id is greater than last accepted id. 
      Otherwise, this proposal is rejected as the acceptor promised to reject any requests with lesser id.
* The proposer upon reception from responses from acceptors, then checks if it has a majority of
  accept messages or not.
  * If there are not enough accept messages(which may be due to random failures again), then the proposer
    considers that there is no consensus and aborts the process.
  * If there is a majority of accept messages received from the acceptors then the proposer proceeds
    to the next phase of the process
* The proposer then sends the log entry to be committed by the learners which then do the execution 
  of the log message/client request.
* Since the key value store is an extension of the paxos node and all the paxos nodes are proposers,
  acceptors and learners, in case of random failures, there had to be some sort of exception handling
  in order to handle the simulated failures as random exceptions at the proposer side.

This implementation doesn't account for the lost updates in case of any failed nodes(i.e. the data 
may not be consistent theoretically on failed acceptor nodes). Also, since the failures are 
mocked to an extent where they just fail at random while accepting or promising, so this means that the 
acceptors will fail only momentarily and since each node can be a learner, each learner theoretically
still remains consistent for GET operations.
Overall this assignment, helped learn about the paxos algorithm for consensus by implementing the 
roles of all paxos participants. Simulating failures helped learn about the flow of the algorithm and
ensure fault tolerance of the distributed key value store.

# Output and Screenshots

## Client
Starting the cluster
![](/Users/aadishdeshpande/Documents/spring/24/distributed_systems/DistributedSystemsProject/Screenshots/p4/Screenshot 2024-04-24 at 7.15.52 PM.png)

Connecting to a server in the cluster
![](/Users/aadishdeshpande/Documents/spring/24/distributed_systems/DistributedSystemsProject/Screenshots/p4/Screenshot 2024-04-24 at 7.19.29 PM.png)


Pre-populating key value store with paxos consensus.
![](/Users/aadishdeshpande/Documents/spring/24/distributed_systems/DistributedSystemsProject/Screenshots/p4/Screenshot 2024-04-24 at 7.23.03 PM.png)

![](/Users/aadishdeshpande/Documents/spring/24/distributed_systems/DistributedSystemsProject/Screenshots/p4/Screenshot 2024-04-24 at 7.24.15 PM.png)

![](/Users/aadishdeshpande/Documents/spring/24/distributed_systems/DistributedSystemsProject/Screenshots/p4/Screenshot 2024-04-24 at 7.26.50 PM.png)


Checking if the populated key is present across different servers

![](/Users/aadishdeshpande/Documents/spring/24/distributed_systems/DistributedSystemsProject/Screenshots/p4/Screenshot 2024-04-24 at 7.38.37 PM.png)

Populating another key on another store
![](/Users/aadishdeshpande/Documents/spring/24/distributed_systems/DistributedSystemsProject/Screenshots/p4/Screenshot 2024-04-24 at 7.59.24 PM.png)


Checking if addition is reflected or not
![](/Users/aadishdeshpande/Documents/spring/24/distributed_systems/DistributedSystemsProject/Screenshots/p4/Screenshot 2024-04-24 at 8.00.34 PM.png)


Deleting key and checking across different server
![](/Users/aadishdeshpande/Documents/spring/24/distributed_systems/DistributedSystemsProject/Screenshots/p4/Screenshot 2024-04-24 at 8.01.52 PM.png)


![](/Users/aadishdeshpande/Documents/spring/24/distributed_systems/DistributedSystemsProject/Screenshots/p4/Screenshot 2024-04-24 at 8.01.52 PM.png)


Server logs showing consensus despite failures:
![](/Users/aadishdeshpande/Documents/spring/24/distributed_systems/DistributedSystemsProject/Screenshots/p4/Screenshot 2024-04-24 at 8.18.30 PM.png)