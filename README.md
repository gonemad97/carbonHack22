# carbonHack22

## Introduction
This application is a task scheduler which is responsible for routing tasks to servers by keeping carbon emissions minimum.

## Use Case Diagram
![Hybrid Energy Task Scheduler (2)](https://user-images.githubusercontent.com/115038203/200086887-b4de31ab-0436-4dd3-9ac4-df8864838e4a.jpg)



## Instructions to run this project
Step 1: git clone https://github.com/gonemad97/carbonHack22.git 

Step 2: There are 5 key files to run this project end-to-end

[Client](https://github.com/gonemad97/carbonHack22/blob/main/src/main/java/Client.java), [Scheduler Server](https://github.com/gonemad97/carbonHack22/blob/main/src/main/java/SchedulerServer.java), [Processing server on-premise - EastUS1](https://github.com/gonemad97/carbonHack22/blob/main/src/main/java/EastUS1.java), [Processing server on-premise - EastUS2](https://github.com/gonemad97/carbonHack22/blob/main/src/main/java/EastUS2.java), [Processing server off-premise - UKSouth](https://github.com/gonemad97/carbonHack22/blob/main/src/main/java/UKSouth.java)

Step 3: Once the project has been built, please run this project in the below-mentioned order.

1. The processing servers are run first, using 1 command line argument :

> <schedulerServer_EastUS1_port>  OR    <schedulerServer_EastUS2_port>
> OR
> <schedulerServer_UKSouth_port>

Sample Configuration **EastUS1** : `"5665"`, **EastUS2** : `"5666"`,  **UKSouth** : `"5667"`


2. The scheduler server is run second, using 5 command line arguments : 

> <localhost_ip>   <client_schedulerServer_port>
> <schedulerServer_EastUS1_port>   <schedulerServer_EastUS2_port>
> <schedulerServer_UKSouth_port>

Sample Configuration **Scheduler Server** : `"127.0.0.1" "5664" "5665" "5666" "5667"`

3. The client is run third, using 2 arguments : 
> <localhost_ip> <client_schedulerServer_port>
> 
Sample Configuration **Client** : `"127.0.0.1" "5664" `
