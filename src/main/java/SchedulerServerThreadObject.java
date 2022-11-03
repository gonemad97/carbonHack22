import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;

public class SchedulerServerThreadObject {
    private List<ServerClientProtocol> taskList;
    private HashMap<String, HttpURLConnection> connMap;
    private HttpURLConnection dayWiseConn;
    private HashMap<String, Integer> casesMap;
    private java.io.ObjectOutputStream eastus;
    private ObjectOutputStream uksouth;
    private ObjectOutputStream westus;
    private List<ServerClientProtocol> energyListLow;
    private List<ServerClientProtocol> energyListHigh;

    public SchedulerServerThreadObject(){

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

    public void setEastus(ObjectOutputStream eastus) {
        this.eastus = eastus;
    }

    public void setUksouth(ObjectOutputStream uksouth) {
        this.uksouth = uksouth;
    }

    public void setWestus(ObjectOutputStream westus) {
        this.westus = westus;
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

    public ObjectOutputStream getEastus() {
        return eastus;
    }

    public ObjectOutputStream getUksouth() {
        return uksouth;
    }

    public ObjectOutputStream getWestus() {
        return westus;
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
