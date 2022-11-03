import java.io.Serializable;

public class ServerClientProtocol implements Serializable {
    //priority
    //task name
    //task id
    private static final long serialVersionUID = 1L;
    private int priority;
    private String taskName;
    private int taskID;

    private int stopStatus;

    //how about we set a task state here - count, no of seconds left, etc
    //set stop status

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

    public void setStopStatus(int status){
        this.stopStatus = status;
    }
    public int getStopStatus(){
        return this.stopStatus;
    }
}
