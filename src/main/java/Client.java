import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client {
    public Client(){

    }
    public int randomPriorityGenerator(){
        int max = 10;
        int min = 1;
        int priority = (int)(Math.random()*(max-min+1)+min);;
        return priority;
    }

    public List<ServerClientProtocol> taskCreation(int i){
        List<ServerClientProtocol> taskList = new ArrayList<>();
        int noOfTask = 0;
        while(noOfTask < 5){
            ServerClientProtocol scp1 = new ServerClientProtocol();
            int priority = randomPriorityGenerator();
            scp1.setPriority(priority);
            if(priority >= 5){
                scp1.setTaskName("Low Priority Task ");
            }
            else{
                scp1.setTaskName("High Priority Task");
            }
            scp1.setTaskID(i*10+noOfTask);
            taskList.add(scp1);
            noOfTask++;
        }
        return taskList;
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("CLIENT SIDE EXECUTION");
        if (args.length != 2) {
            System.err.println(
                    "Usage: java EchoClient <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        System.out.println("Client Port Number " + portNumber);
        Socket echoSocket = new Socket(hostName, portNumber);
        OutputStream outputStream = echoSocket.getOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

        int i = 0;
        while(i < 50){
                List<ServerClientProtocol> tasksList = new Client().taskCreation(i);
                objectOutputStream.writeObject(tasksList);
                System.out.println("sending task batch " + i );
                i++;
                Thread.sleep(10000);
       }
        Thread.sleep(100000000);
        echoSocket.close();
    }

}
