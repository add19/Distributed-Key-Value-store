# Project owner
Aadish Deshpande (deshpande.aa@northeastern.edu)

# Project Readme

## Brief overview of the project
* This project is an extension of the previous version of this project of RMI based client-server 
key value store application. RMI registry is leveraged for making remote procedural 
calls for client server communication, with servers replicated for scaling the server and 
2 phase commit protocol was implemented to ensure data consistency across replicated servers.
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
    ├── Coordinator.java
    ├── ICoordinator.java
    ├── IRemoteDataStore.java
    ├── ITwoPhaseCommit.java
    ├── KeyValueServer.java
    ├── RemoteDataStore.java
    ├── Transaction.java
    ├── Vote.java
    └── config.txt


```
* Compile the code using `javac RMIServer/*.java RMIClient/*.java`
* To start the 5 server cluster including the coordinator
  * `java RMIServer.KeyValueServer <port-number 1> <port-number 2> <port-number 3> <port-number 4> <port-number 5>`
* The above command starts 5 instances and a coordinator and binds them to the registry.
* The registry port number can be found in the config.txt, this file is used to set up the coordinator port and the registry port number.
* To run the client
  * `java RMIClient.KeyValueClient <registry-port-number>`
* Input the name of any server instance to use(from rmi registry), the names would be displayed on the console for reference.
* All the client and server logs are generated on the console

## Executive Summary

### Assignment Overview:(Purpose and Scope)

The purpose of this assignment was to go on and further scale the existing key value store to increase 
fault tolerance and availability. We do this by extending our server to be replicated across different machines
and endpoints in a cluster. Clients can communicate with any of these servers, thus ensuring if there is any server which 
goes down it still is able to get data from the store. However, this kind of architecture brings in challenges of its own, that is, to ensure
that the data is consistent across all the replica instances, and ensuring all the write operations are
atomic and durable, i.e. all the servers are storing same set of data and the data remains consistent across all the operations.
The existing RMI implementation already simplify a lot of client-server communication constructs like socket creation, socket
management, and packet level management of data transmission. In addition, the use of registry facilitated the selection
of any particular server to connect by any of the clients.
Implementation of the 2 phase commit protocol ensured that there is consistency across the instances. This involved in creation of 
additional coordinator(which is also a separate key value store instance). The write operations, received by 
any participant is first forwarded to the coordinator, the coordinator, which at the start-up time stores all the participant instances with it,
forwards the transaction and asks for their votes. Any read request ensured that the latest data is returned from any given instance.

### Technical impressions:
Implementing RPC communication using Java RMI provided a straightforward way to achieve part of the 
assignment's objectives. For implementing the clustering logic, additional interfaces for coordinator
functionalities had to be implemented on top of the existing remote key value stores. 
Existing rmi registry was used to keep track of all the instances in the cluster and this registry 
was then used conveniently by the client to connect to any of the available servers. The client 
then provides the user to connect to any of the given server, connects to the mentioned server
and then invokes any of GET, PUT or DELETE operations. Each of the server keeps track of the 
coordinator server to communicate with. Whenever there is an invocation of any sort of write operations
an RMI call is made to coordinator which then in turn initiates the 2 phase commit protocol next.
The associated method is synchronized to ensure there is no race in the order of executions of transactions.
At the client side, an in-memory list of transactions to be executed by the servers is maintained, 
which gets populated, once the servers update the coordinator. In case of write operations, the coordinator,
which has all the participant instances, invokes the canCommit(). The servers then check and update
their transactions list to prepare for transaction and then sends a vote, Yes or No, to coordinator.
Based on the response, the coordinator then asks to abort(in case of No) and commit(in case of Yes) from
all the participants. The servers log the information about the transaction being executed on console. 
There is a timeout implemented on coordinator side to ensure that the 2 PC doesn't  stall if any 
server goes down.
This assignment helped learn more about 2 phase commit protocol and scaling any server while ensuring
consistency of the data.

# Output and Screenshots

## Client
Starting the cluster
![](/Users/aadishdeshpande/Documents/spring/24/distributed_systems/DistributedSystemsProject/Screenshots/p3/Screenshot 2024-04-06 at 6.12.31 PM.png)

Connecting to a server in the cluster
![](/Users/aadishdeshpande/Documents/spring/24/distributed_systems/DistributedSystemsProject/Screenshots/p3/Screenshot 2024-04-06 at 6.13.31 PM.png)


Connecting with another server through a different instance of a client and writing something into the store
![](/Users/aadishdeshpande/Documents/spring/24/distributed_systems/DistributedSystemsProject/Screenshots/p3/Screenshot 2024-04-06 at 6.16.47 PM.png)

Checking if the populated key is present across different servers

![](/Users/aadishdeshpande/Documents/spring/24/distributed_systems/DistributedSystemsProject/Screenshots/p3/Screenshot 2024-04-06 at 6.16.53 PM.png)

Deleting the key on another client
![](/Users/aadishdeshpande/Documents/spring/24/distributed_systems/DistributedSystemsProject/Screenshots/p3/Screenshot 2024-04-06 at 6.17.37 PM.png)


Checking if deletion is reflected or not
![](/Users/aadishdeshpande/Documents/spring/24/distributed_systems/DistributedSystemsProject/Screenshots/p3/Screenshot 2024-04-06 at 6.17.52 PM.png)


Server logs
![](/Users/aadishdeshpande/Documents/spring/24/distributed_systems/DistributedSystemsProject/Screenshots/p3/Screenshot 2024-04-06 at 6.21.29 PM.png)
