import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;

public class SchedulerServerThreadObject {
    private List<ServerClientProtocol> taskList;
    private HashMap<String, HttpURLConnection> connMap;
    private HttpURLConnection dayWiseConn;
    private HashMap<String, Integer> casesMap;
    private java.io.ObjectOutputStream eastus1;
    private java.io.ObjectOutputStream uksouth;
    private java.io.ObjectOutputStream eastus2;
    private List<ServerClientProtocol> energyListLow;
    private List<ServerClientProtocol> energyListHigh;

    private ObjectInputStream eastusInput;

    private ObjectInputStream westusInput;

    public ObjectInputStream getWestusInput() {
        return westusInput;
    }

    public void setWestusInput(ObjectInputStream westusInput) {
        this.westusInput = westusInput;
    }

    public ObjectInputStream getUkSouthInput() {
        return ukSouthInput;
    }

    public void setUkSouthInput(ObjectInputStream ukSouthInput) {
        this.ukSouthInput = ukSouthInput;
    }

    private ObjectInputStream ukSouthInput;



    private int threadComplete;



    public SchedulerServerThreadObject(){

    }

    public ObjectInputStream getEastusInput() {
        return eastusInput;
    }

    public void setEastusInput(ObjectInputStream eastusInput) {
        this.eastusInput = eastusInput;
    }

    public void setThreadComplete(int threadComplete) {
        this.threadComplete = threadComplete;
    }

    public int getThreadComplete() {
        return threadComplete;
    }

    public void setDayWiseConn(HttpURLConnection dayWiseConn) {
        this.dayWiseConn = dayWiseConn;
    }

    public HttpURLConnection getDayWiseConn() {
        return dayWiseConn;
    }


    public void setTaskList(List<ServerClientProtocol> taskList) {
        this.taskList = taskList;
    }

    public void setConnMap(HashMap<String, HttpURLConnection> connMap) {
        this.connMap = connMap;
    }

    public void setCasesMap(HashMap<String, Integer> casesMap) {
        this.casesMap = casesMap;
    }

    public void setEastus1(ObjectOutputStream eastus1) {
        this.eastus1 = eastus1;
    }

    public void setUksouth(ObjectOutputStream uksouth) {
        this.uksouth = uksouth;
    }

    public void setEastus2(ObjectOutputStream eastus2) {
        this.eastus2 = eastus2;
    }

    public void setEnergyListLow(List<ServerClientProtocol> energyListLow) {
        this.energyListLow = energyListLow;
    }

    public void setEnergyListHigh(List<ServerClientProtocol> energyListHigh) {
        this.energyListHigh = energyListHigh;
    }

    public HashMap<String, HttpURLConnection> getConnMap() {
        return connMap;
    }

    public HashMap<String, Integer> getCasesMap() {
        return casesMap;
    }

    public ObjectOutputStream getEastus1() {
        return eastus1;
    }

    public ObjectOutputStream getUksouth() {
        return uksouth;
    }

    public ObjectOutputStream getEastus2() {
        return eastus2;
    }

    public List<ServerClientProtocol> getEnergyListLow() {
        return energyListLow;
    }

    public List<ServerClientProtocol> getEnergyListHigh() {
        return energyListHigh;
    }


    public List<ServerClientProtocol> getTaskList() {
        return taskList;
    }
}
