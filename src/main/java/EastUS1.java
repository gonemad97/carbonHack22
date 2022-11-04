import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.lang.*;
import java.util.Map;

public class EastUS1 implements Runnable, Serializable{
    Thread t1;

    private int taskID;
    public EastUS1(int taskID){
        this.taskID = taskID;
        this.t1 = new Thread(this);
        this.t1.start();
    }
    public EastUS1(){
    }
    public static void main(String[] args) throws IOException, ClassNotFoundException, EOFException {

        if (args.length != 1) {
            System.err.println("Usage: <schedulerServer_EastUS1_port>");
            System.exit(1);
        }
        int portNumber = Integer.parseInt(args[0]);
        System.out.println("Port Number connecting first east US Server and Scheduler Server " + portNumber);
        ServerSocket serverSocket =
                new ServerSocket(Integer.parseInt(args[0]));
        Socket clientSocket = serverSocket.accept();
        InputStream inputStream = clientSocket.getInputStream();
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        try{
            while(true){
                Map<String,List<ServerClientProtocol>> taskMap = (Map<String, List<ServerClientProtocol>>) objectInputStream.readObject();
                List<ServerClientProtocol> listOfTasks = new ArrayList<>();
                if(taskMap.containsKey("dirty")){
                    listOfTasks = taskMap.get("dirty");
                    new EastUS1().dirtyEnergy(listOfTasks);
                }
                else if(taskMap.containsKey("Clean")){
                    listOfTasks = taskMap.get("Clean");
                    new EastUS1().cleanEnergy(listOfTasks);
                }
            }
        } catch (EOFException eofException){
            eofException.printStackTrace();
        } finally {
            System.out.println("Closing sockets");
            serverSocket.close();
            clientSocket.close();
        }
    }
    @Override
    public void run() {
        System.out.println("Concurrent execution in clean energy time");
        int max = 100;
        int min = 1;
        int random = (int)(Math.random()*(max-min+1)+min);
        for(int i = 0; i <= random; i++){
            //add required functionality here, below calculation is an example
            double area = 3.14 * (random*random);
        }
        System.out.println("Executed job " + this.taskID);
    }

    public void cleanEnergy(List<ServerClientProtocol> listOfTasks){
        for (int i = 0; i < listOfTasks.size(); i++) {
            new EastUS1(listOfTasks.get(i).getTaskID());
        }
    }
    public void dirtyEnergy(List<ServerClientProtocol> listOfTasks){
        System.out.println("Sequential execution in dirty energy time");
        for(int i = 0; i < listOfTasks.size(); i++){
            System.out.println("******Executed Task: " + listOfTasks.get(i).getTaskID() + "*********");
        }
    }

}
