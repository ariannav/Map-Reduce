Author: Arianna Vacca
Purpose: CS455 PA2
Date: 2 March 2018
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
    Server:
        java cs455.scaling.server.Server <portnum> <thread-pool-size>
    Client:
        java cs455.scaling.client.Client <server-host> <server-port> <message-rate>

CLASS DESCRIPTIONS
===================
- Server: Primary server class. Starts the Server selector, the
    server statistics printer, the thread pool manager, and the worker threads.
    Processes incoming connections and creates tasks for worker threads as
    necessary.
- ServerLogger: Server statistics printer class. Prints server output
    (server throughput, number of active client connections, average client
    throughput, standard deviation of the client throughput) every 20 seconds.
- Task: A task object contains a run() method. This run() method is
    run by the thread that is assigned to this task. Each task is created using
    a key, which is used to read and write to the socket the client is connected
    to.
- Thread: Each thread or 'Worker Thread' uses a wait-notify queue
    implemented in the ThreadPoolManager to request a task. Once a task is
    assigned, the thread calls the run() method of the task class. Upon task
    completion, the thread requests another task.
- ThreadPoolManager: The ThreadPoolManager contains two wait/notify FIFO queues
    implemented using synchronized LinkedLists. One list contains tasks waiting
    to be run, the other contains ready threads. The TPM delegates tasks to
    available threads.
- Statistics: Each Statistics object is attached to a SelectionKey via
    the key's attach() method, where it keeps track of the number of messages
    sent by the client the key is attached to, and whether the key is currently
    in use (for example, a task has been assigned to this key already).
- Client: Primary client class, starts the writing thread, client selector, and
    the client statistics printer.
- ClientLogger: Client statistics printer class. Prints client output
    (number sent, number received) every 20 seconds.

ASSUMPTIONS
===================
- No clients will be terminated while the Server process is running.