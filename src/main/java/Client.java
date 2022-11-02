import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client {
    //send tasks
    //4 objects of the scp class
    //random number gen for priority
    //send them in a queue - 3 second gaps so we can simulate a real time situation

    public Client(){

    }

    public int randomPriorityGenerator(){
        int max = 10;
        int min = 1;
        int priority = (int)(Math.random()*(max-min+1)+min);;
        return priority;
    }

    public List<ServerClientProtocol> taskCreation(){
        List<ServerClientProtocol> taskList = new ArrayList<>();
        int noOfTask = 0;
        while(noOfTask < 5){
            ServerClientProtocol scp1 = new ServerClientProtocol();
            int priority = randomPriorityGenerator();
            scp1.setPriority(priority);
            if(priority >= 5){
                scp1.setTaskName("Server running");
            }
            scp1.setTaskID(noOfTask);
            taskList.add(scp1);
            noOfTask++;
        }
        return taskList;
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length != 2) {
            System.err.println(
                    "Usage: java EchoClient <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        System.out.println("Port Number " + portNumber);
        Socket echoSocket = new Socket(hostName, portNumber);
        System.out.println("Client side");
        OutputStream outputStream = echoSocket.getOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        // create an object output stream from the output stream so we can send an object through it

        int i = 0;
        while(i < 3){
            List<ServerClientProtocol> tasksList = new Client().taskCreation();
            objectOutputStream.writeObject(tasksList);
//            System.out.println("sending task " + i);
//            System.out.println("priority: " + tasksList.get(i).getPriority());
//            objectOutputStream.writeObject(tasksList.get(i));
//            Thread.sleep(3000);
//            i++;
        }
        Thread.sleep(300000000);
        echoSocket.close();
    }

}
