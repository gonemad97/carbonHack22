import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UKSouth implements Runnable{
    Thread t1;
    public UKSouth(){
        this.t1 = new Thread(this);
        this.t1.start();
    }
    public UKSouth(int num){
    }
    public static void main(String[] args) throws IOException, ClassNotFoundException, EOFException {

        if (args.length != 1) {
            System.err.println("Usage: java EchoServer <port number>");
            System.exit(1);
        }
        int portNumber = Integer.parseInt(args[0]);
        System.out.println("Portnumber " + portNumber);
        String input = "";
        ServerSocket serverSocket =
                new ServerSocket(Integer.parseInt(args[0]));
        Socket clientSocket = serverSocket.accept();
        InputStream inputStream = clientSocket.getInputStream();
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

        OutputStream outputStream1 = clientSocket.getOutputStream();
        ObjectOutputStream objectOutputStream1 = new ObjectOutputStream(outputStream1);
        //ackObject ackObject = new ackObject();
        //BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        try{
            while(true){
                System.out.println("Server side");
                //put a while loop, keep listening, send acks
                Map<String, List<ServerClientProtocol>> taskMap = (Map<String, List<ServerClientProtocol>>) objectInputStream.readObject();
                List<ServerClientProtocol> listOfTasks = new ArrayList<>();
                System.out.println(taskMap);
                if(taskMap.containsKey("dirty")){
                    listOfTasks = taskMap.get("dirty");
                    new UKSouth(1).dirtyEnergy(listOfTasks);
                }
                else if(taskMap.containsKey("Clean")){
                    listOfTasks = taskMap.get("Clean");
                    new UKSouth(1).cleanEnergy(listOfTasks);
                }
            }
        } catch (EOFException eofException){
            eofException.printStackTrace();
        } finally {
            System.out.println("All messages:");
            System.out.println("Closing sockets.");
            serverSocket.close();
            clientSocket.close();
        }

        // print out the text of every message

        //handle the EOF exception
        //Generate threads for multiple servers
        //Generate random functions for each thread
        //Deployment in docker
        //Look at how to handle servers with threads
    }
    @Override
    public void run() {
        //exec thread - so call a function that executes
        //else - print
        int max = 100;
        int min = 1;
        int random = (int)(Math.random()*(max-min+1)+min);
        for(int i = 0; i <= 5; i++){
            System.out.println("*** " + i);
        }
//        //add a flag/count to check when the tasks are ending
        //how to save the state
    }


    public void cleanEnergy(List<ServerClientProtocol> listOfTasks){
        for (int i = 0; i < listOfTasks.size(); i++) {
            System.out.println("task size " + listOfTasks.size());
            new UKSouth();
            System.out.println("Task " + i);
        }
    }
    public void dirtyEnergy(List<ServerClientProtocol> listOfTasks){
        //implement sequential
        System.out.println("Implementing sequential");
        for(int i = 0; i < listOfTasks.size(); i++){
            System.out.println(listOfTasks.get(i));
        }
    }

}
