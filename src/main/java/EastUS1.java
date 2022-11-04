import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.lang.*;
import java.util.Map;

public class EastUS1 implements Runnable{
    Thread t1;
    public EastUS1(){
        this.t1 = new Thread(this);
        this.t1.start();
    }
    public EastUS1(int num){
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
        InputStream inputStream = clientSocket.getInputStream();
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

        try{
            while(true){
                System.out.println("Server side");
                //put a while loop, keep listening, send acks
                Map<String,List<ServerClientProtocol>> taskMap = (Map<String, List<ServerClientProtocol>>) objectInputStream.readObject();
                List<ServerClientProtocol> listOfTasks = new ArrayList<>();
                System.out.println(taskMap);
                if(taskMap.containsKey("dirty")){
                    listOfTasks = taskMap.get("dirty");
                    new EastUS1(1).dirtyEnergy(listOfTasks);
                }
                else if(taskMap.containsKey("Clean")){
                    listOfTasks = taskMap.get("Clean");
                    new EastUS1(1).cleanEnergy(listOfTasks);
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
    }


    public void cleanEnergy(List<ServerClientProtocol> listOfTasks){
        for (int i = 0; i < listOfTasks.size(); i++) {
            System.out.println("task size " + listOfTasks.size());
            new EastUS1();
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
