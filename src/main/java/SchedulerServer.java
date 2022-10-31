import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SchedulerServer {
    public SchedulerServer(){

    }
    public List<ServerClientProtocol> dirtyEnergyList = new ArrayList<>();
    public List<ServerClientProtocol> cleanEnergyList = new ArrayList<>();
    public void segregateTasksByPriority(ServerClientProtocol taskList) {
        //outer while loop
        //add try catch blocks
        ServerClientProtocol scp = taskList;
        int taskPriority = scp.getPriority();

        if(taskPriority < 5){
            dirtyEnergyList.add(scp);
        }
        else{
            cleanEnergyList.add(scp);
        }
        System.out.println("dirty energy: " + dirtyEnergyList + "clean energy " + cleanEnergyList);
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        if (args.length != 2) {
            System.err.println(
                    "Usage: java EchoClient <host name> <port number>");
            System.exit(1);
        }
        //1 - client port number, 2- Scheduler server port number, 3- LocationBasedServer port number
        //listening
        int clientPortNumber = Integer.parseInt(args[1]);
        System.out.println("Portnumber " + clientPortNumber);
        ServerSocket serverSocket =
                new ServerSocket(Integer.parseInt(args[1]));
        Socket clientSocket = serverSocket.accept();
        InputStream inputStream = clientSocket.getInputStream();
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        SchedulerServer dummy = new SchedulerServer();
        while(clientSocket.isConnected()){
            ServerClientProtocol taskList = (ServerClientProtocol) objectInputStream.readObject();
            System.out.println("Task List: " + taskList);
            dummy.segregateTasksByPriority(taskList);
        }
        //finished listening

        //Get the carbon emission forecasts for the 3 locations - westus, eastus and uksouth
        //Get the average of all the emissions in the interval - figure this out
        //sort the average values and do load balancing - figure out what kind of load balancing is better
        //send the queues accordingly
        //Scheduler server - java schedulerServer 127.0.0.1 ClientSchedulerServerportnum locationportnum1 locationportnum2 locationportnum3

        String hostName = args[0];
        int SchedulerL1PortNumber = Integer.parseInt(args[2]);
        int SchedulerL2PortNumber = Integer.parseInt(args[3]);
        int SchedulerL3PortNumber = Integer.parseInt(args[4]);
        System.out.println("Port Number " + SchedulerL1PortNumber);
        Socket echoSocket1 = new Socket(hostName, SchedulerL1PortNumber);
        Socket echoSocket2 = new Socket(hostName, SchedulerL2PortNumber);
        Socket echoSocket3 = new Socket(hostName, SchedulerL3PortNumber);
        System.out.println("Client side");
        OutputStream outputStream1 = echoSocket1.getOutputStream();
        OutputStream outputStream2 = echoSocket2.getOutputStream();
        OutputStream outputStream3 = echoSocket3.getOutputStream();

        ObjectOutputStream objectOutputStream1 = new ObjectOutputStream(outputStream1);
        ObjectOutputStream objectOutputStream2 = new ObjectOutputStream(outputStream2);
        ObjectOutputStream objectOutputStream3 = new ObjectOutputStream(outputStream3);

//        URL url = new URL("https://carbon-aware-api.azurewebsites.net/emissions/bylocations?location=westus&location=eastus&time=2022-03-01T15%3A30%3A00Z&toTime=2022-03-01T18%3A30%3A00Z");
//
//        // Open a connection(?) on the URL(??) and cast the response(???)
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//        connection.setRequestProperty("accept", "application/json");
//        connection.setRequestMethod("GET");
//        InputStream responseStream = connection.getInputStream();
//        ObjectMapper mapper = new ObjectMapper();
//        while(responseStream.available() == 1){
//            APIResponseClass responseClass = mapper.readValue(responseStream, APIResponseClass.class);
//            System.out.println("rating " + responseClass.getRating());
//        }


        serverSocket.close();
        clientSocket.close();
    }
}
