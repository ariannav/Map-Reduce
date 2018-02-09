Author: Arianna Vacca
Purpose: CS455 PA1
Date: 9 February 2018
********************************************************************************
                                    README
********************************************************************************

COMPILE
===================
    to compile:
        make all
    to clean up:
        make clean

RUN
===================
    Registry:
        java cs455.overlay.node.Registry <portNumber>
    MessagingNode:
        java cs455.overlay.node.MessagingNode <registry-host> <registry-port>

CLASS DESCRIPTIONS
===================
- Registry: Primary Registry class. Registers nodes, acts as a server, and
            orchestrates the actions of the MessagingNodes based on the provided
            commands.
- MessagingNode: Primary thread which generates all other threads in the
            MessagingNode process. Registry interactions occur through this
            thread.
- RegistryNode: Each RegistryNode is connected to one MessagingNode. The
            RegistryNode facilitates the interactions between the Registry and
            the MessagingNode.
- ForegroundThread: Takes commands from the Registry's terminal and interacts
            with the Registry to fulfill the requests made.
- MessagingForegroundThread: Takes commands from the MessagingNode's terminal
            and interacts with the MessagingNode to fulfill the requests made.
- MNEndpoint: Each MNEndpoint connects to one entry (another MessagingNode) in
            the MessagingNode's routing table. While the overlay is constructed,
            N(R) MNEndpoints should be running concurrently per MessagingNode.
            (This is the sender thread in the overlay.)
- MNServer: Each MessagingNode has one MNServer which acts as the server thread
            for all incoming connections from other MessagingNodes.
- MNRouter: Each connection made through the MNServer is passed along to a
            MNRouter, which facilitates one interaction with another
            MessagingNode. While the overlay is constructed, N(R) MNRouters
            per MessagingNode should be running concurrently.
            (This is the receiver thread in the overlay.)
- NodeContainer: This is a container class which contains information about
            each MessagingNode in the overlay.
- Statistics: This is a container class which contains the counters for message
            sending. Contains the information displayed in the traffic summary.
- MessageCreator: Creates different message types. Each createMessageX()
            method returns a byte array.
- MessageType: Processes each message type. Reads from the socket's
            DataInputStream and separates the data into different accessible
            variables. This information can be accessed through getters.

ASSUMPTIONS
===================
- If the Registry process is terminated (via CTRL+C), connected MessagingNodes
  will exit their processes.
- If the wrong message type is received, the MessagingNode will produce an error
  message and will continue to wait for incoming messages.
- A failed Registration/Deregistration for the MessagingNode will result in no
  action taken by the Registry.
- The overlay size (N(R)) should be small enough to prevent overlapping routing
  table values.