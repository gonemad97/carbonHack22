import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.lang.*;
import java.util.Map;

public class LocationBasedServer1 implements Runnable{
    Thread t1;
    public LocationBasedServer1(){
        this.t1 = new Thread(this);
        t1.start();
    }
    public LocationBasedServer1(int num){
    }
    public static void main(String[] args) throws IOException, ClassNotFoundException, EOFException {

        if (args.length != 1) {
            System.err.println("Usage: java EchoServer <port number>");
            System.exit(1);
        }
        int portNumber = Integer.parseInt(args[0]);
        System.out.println("Portnumber " + portNumber);
        ServerSocket serverSocket =
                new ServerSocket(Integer.parseInt(args[0]));
        Socket clientSocket = serverSocket.accept();
        try{
            System.out.println("Server side");
            InputStream inputStream = clientSocket.getInputStream();
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            List<ServerClientProtocol> taskMap = (List<ServerClientProtocol>) objectInputStream.readObject();
            List<ServerClientProtocol> listOfTasks = new ArrayList<>();
            System.out.println(taskMap);
//            if(taskMap.containsKey("dirty")){
//                listOfTasks = taskMap.get("dirty");
//                new LocationBasedServer1(1).cleanEnergy(listOfTasks);
//            }
//            else{
//                listOfTasks = taskMap.get("clean");
//                new LocationBasedServer1(1).dirtyEnergy(listOfTasks);
//            }
        } catch (EOFException eofException){
            eofException.printStackTrace();
        }
        // print out the text of every message
        System.out.println("All messages:");
        System.out.println("Closing sockets.");
        serverSocket.close();
        clientSocket.close();

        //handle the EOF exception
        //Generate threads for multiple servers
        //Generate random functions for each thread
        //Deployment in docker
        //Look at how to handle servers with threads
    }
    @Override
    public void run() {
        int max = 100;
        int min = 1;
        int random = (int)(Math.random()*(max-min+1)+min);;
        for(int i = 0; i <= random; i++){
            System.out.println(this.t1.getId());
        }
    }

    public void cleanEnergy(List<ServerClientProtocol> listOfTasks){
        for (int i = 0; i < listOfTasks.size(); i++) {
            System.out.println("task size " + listOfTasks.size());
            new LocationBasedServer1();
            System.out.println("Task " + i);
        }
        //while(!queue.empty())
    }
    public void dirtyEnergy(List<ServerClientProtocol> listOfTasks){
        //implement round-robin
    }

}
