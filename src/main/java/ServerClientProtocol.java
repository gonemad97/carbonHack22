import java.io.Serializable;

public class ServerClientProtocol implements Serializable {
    //priority
    //task name
    //task id
    private static final long serialVersionUID = 1L;
    private int priority;
    private String taskName;
    private int taskID;

    public ServerClientProtocol(){}

    public void setPriority(int givenPriority){
        this.priority = givenPriority;
    }
    public int getPriority(){
        return this.priority;
    }
    public void setTaskName(String givenTaskName){
        this.taskName = givenTaskName;
    }
    public String getTaskName(){
        return this.taskName;
    }
    public void setTaskID(int givenTaskID){
        this.taskID = givenTaskID;
    }
    public int getTaskID(){
        return this.taskID;
    }
}
