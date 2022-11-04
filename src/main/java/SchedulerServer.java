import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class SchedulerServer implements Runnable {
    Thread t1;
    public List<ServerClientProtocol> highPriorityEnergyList = new ArrayList<>();
    public List<ServerClientProtocol> lowPriorityEnergyList = new ArrayList<>();

    public SchedulerServerThreadObject SchedulerServerThreadObject = new SchedulerServerThreadObject();

    public ServerSocket serverSocket = null;

    public SchedulerServer(){

    }
    public SchedulerServer(SchedulerServerThreadObject SchedulerServerThreadObject){
        this.SchedulerServerThreadObject = SchedulerServerThreadObject;
        this.t1 = new Thread(this);
        this.t1.start();
    }
    public boolean segregateTasksByPriority(List<ServerClientProtocol> taskList) {
        for(ServerClientProtocol scpObject: taskList){
            int taskPriority = scpObject.getPriority();

            if(taskPriority < 5){
                System.out.println("High Priority Task Created " + scpObject.getPriority());
                highPriorityEnergyList.add(scpObject);
            }
            else{
                System.out.println("Low Priority Task Created " + scpObject.getPriority());
                lowPriorityEnergyList.add(scpObject);
            }
            System.out.println("High Priority Energy List: " + highPriorityEnergyList + "Low Priority Energy List: " + lowPriorityEnergyList);
        }
        return true;
    }

    public List<List<ServerClientProtocol>> loadBalancing(List<ServerClientProtocol> energyList){
        System.out.println("Load Balancing to prevent overloading on servers");
        List<ServerClientProtocol> loadBalance1 = new ArrayList<>();
        List<ServerClientProtocol> loadBalance2 = new ArrayList<>();
        for(int i =0; i < energyList.size();i++){
            if(i%2 == 0){
                loadBalance1.add(energyList.get(i));
            }
            else{
                loadBalance2.add(energyList.get(i));
            }
        }
        List<List<ServerClientProtocol>> returnLists = new ArrayList<>();
        returnLists.add(loadBalance1);
        returnLists.add(loadBalance2);
        System.out.println("Returning load balanced Task Lists");
        return returnLists;
    }

    public void energyScenarios(HashMap<String, Integer> casesMap, ObjectOutputStream eastus1, ObjectOutputStream uksouth,
                                ObjectOutputStream eastus2, List<ServerClientProtocol> energyListLow,List<ServerClientProtocol> energyListHigh) throws IOException, ClassNotFoundException {

        System.out.println("Energy Scenarios to determine which server to direct incoming tasks to " +
                "based on whether a server's emission is clean or dirty");
        if(casesMap.get("eastus") == 0 && casesMap.get("uksouth") == 1){
            List<List<ServerClientProtocol>> loadBalancingListsLow = loadBalancing(energyListLow);
            Map<String,List<ServerClientProtocol>> listMap1Low = new HashMap<>();
            Map<String,List<ServerClientProtocol>> listMap2Low = new HashMap<>();

            listMap1Low.put("Clean", loadBalancingListsLow.get(0));
            eastus1.writeObject(listMap1Low);

            listMap2Low.put("Clean", loadBalancingListsLow.get(1));
            eastus2.writeObject(listMap2Low);
        }
        else if(casesMap.get("eastus") == 0 && casesMap.get("uksouth") == 0){
            Map<String, List<ServerClientProtocol>> lowPriorityMap = new HashMap<>();
            lowPriorityMap.put("Clean", energyListLow);
            uksouth.writeObject(lowPriorityMap);

        }
        else if(casesMap.get("eastus") == 1 && casesMap.get("uksouth") == 0){
            Map<String, List<ServerClientProtocol>> lowPriorityMap = new HashMap<>();
            lowPriorityMap.put("Clean", energyListLow);
            System.out.println("LOW PRIORITY MAP :: "+lowPriorityMap);
            uksouth.writeObject(lowPriorityMap);
        }
        if(casesMap.get("eastus") == 1 && casesMap.get("uksouth") == 1){
            List<List<ServerClientProtocol>> loadBalancingLists = loadBalancing(energyListHigh);
            Map<String,List<ServerClientProtocol>> listMap1 = new HashMap<>();
            Map<String,List<ServerClientProtocol>> listMap2 = new HashMap<>();

            listMap1.put("Clean", loadBalancingLists.get(0));
            eastus1.writeObject(listMap1);

            listMap2.put("Clean", loadBalancingLists.get(1));
            eastus2.writeObject(listMap2);
        }
        List<List<ServerClientProtocol>> loadBalancingLists = loadBalancing(energyListHigh);
        Map<String,List<ServerClientProtocol>> highPriorityMap1 = new HashMap<>();
        Map<String,List<ServerClientProtocol>> highPriorityMap2 = new HashMap<>();

        highPriorityMap1.put("dirty", loadBalancingLists.get(0));
        eastus1.writeObject(highPriorityMap1);
        System.out.println("HIGH PRIORITY TASK MAP 1: "+highPriorityMap1);

        highPriorityMap2.put("dirty", loadBalancingLists.get(1));
        eastus2.writeObject(highPriorityMap2);
        System.out.println("HIGH PRIORITY TASK MAP 2: "+highPriorityMap2);
    }

    public double medianEmissions(HttpURLConnection conn) throws IOException, ParseException {
        System.out.println("MEDIAN EMISSION IS CALLED");
        Double median = 0.0;
        InputStream responseStream = conn.getInputStream();
        System.out.println("response stream " + responseStream);
        JSONParser jsonParser = new JSONParser();
        InputStreamReader inputStreamReader = null;
        try{
            inputStreamReader = new InputStreamReader(responseStream);
            JSONArray jsonArray = (JSONArray)jsonParser.parse(inputStreamReader);
            ObjectMapper objectMapper = new ObjectMapper();
            HashMap<String,Object> result =
                    objectMapper.readValue(jsonArray.get(0).toString(), HashMap.class);
            List<APIResponseClass> responseObjectList= new ArrayList<>();
            List<LinkedHashMap> jsonResponse = (List<LinkedHashMap>) result.get("forecastData");
            List<Double> medianList = new ArrayList<>();
            for(int object = 0; object < jsonResponse.size(); object++){
                APIResponseClass apiResponseClass = new APIResponseClass();
                apiResponseClass.setRating((Double) jsonResponse.get(object).get("value"));
                responseObjectList.add(apiResponseClass);
                medianList.add(apiResponseClass.getValue());
            }
            Collections.sort(medianList);
            if(medianList.size() %2 == 0){
                median = (medianList.get(medianList.size()/2) + medianList.get((medianList.size()/2)+1))/2;
            }
            else{
                median = medianList.get(medianList.size()/2);
            }
            System.out.println("median : "+median);

            } catch(Exception ex){
                ex.printStackTrace();
            }

        return median;
    }

    @Override
    public void run(){
        System.out.println("Processing for each batch of tasks begins here");
        segregateTasksByPriority(this.SchedulerServerThreadObject.getTaskList());
        HashMap<String, Integer> casesMap = new HashMap<>();
        HashMap<String, Double> medianMap = this.SchedulerServerThreadObject.getMedianMap();
        try {
            for(String str: medianMap.keySet()){
                if(medianMap.get(str) > this.SchedulerServerThreadObject.getDayWiseMedian()){
                    casesMap.put(str, 1);
                }
                else{
                    casesMap.put(str, 0);
                }
            }
            energyScenarios(casesMap, this.SchedulerServerThreadObject.getEastus1(),
            this.SchedulerServerThreadObject.getUksouth(),
            this.SchedulerServerThreadObject.getEastus2(), lowPriorityEnergyList,
            highPriorityEnergyList);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, ParseException, URISyntaxException {
        if (args.length < 2) {
            System.err.println(
                    "Usage: <host name> <client_schedulerServer_port> <schedulerServer_EastUS1_port> <schedulerServer_EastUS2_port> <schedulerServer_UKSouth_port>");
            System.exit(1);
        }
        SchedulerServer schedulerServer = new SchedulerServer();
        int clientPortNumber = Integer.parseInt(args[1]);
        System.out.println("Port Number used to connect client and scheduler server " + clientPortNumber);
        schedulerServer.serverSocket = new ServerSocket(Integer.parseInt(args[1]));
        Socket clientSocket = null;
        String hostName = args[0];
        int SchedulerL1PortNumber = Integer.parseInt(args[2]);
        System.out.println("Port Number used to connect scheduler server and first east US server" + SchedulerL1PortNumber);
        Socket echoSocket1 = new Socket(hostName, SchedulerL1PortNumber);
        OutputStream outputStream1 = echoSocket1.getOutputStream();
        ObjectOutputStream objectOutputStream1 = new ObjectOutputStream(outputStream1);


        int SchedulerL2PortNumber = Integer.parseInt(args[3]);
        System.out.println("Port Number used to connect scheduler server and second east US server" + SchedulerL2PortNumber);
        Socket echoSocket2 = new Socket(hostName, SchedulerL2PortNumber);
        OutputStream outputStream2 = echoSocket2.getOutputStream();
        ObjectOutputStream objectOutputStream2 = new ObjectOutputStream(outputStream2);

        int SchedulerL3PortNumber = Integer.parseInt(args[4]);
        System.out.println("Port Number used to connect scheduler server and UK South server" + SchedulerL3PortNumber);
        Socket echoSocket3 = new Socket(hostName, SchedulerL3PortNumber);
        OutputStream outputStream3 = echoSocket3.getOutputStream();
        ObjectOutputStream objectOutputStream3 = new ObjectOutputStream(outputStream3);

        Date currentDateTime = null;
        Date currentDate = null;
        boolean daywise = false;
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

        HttpURLConnection connectionThirtyeastus = null;
        HttpURLConnection connectionThirtyuksouth = null;
        HttpURLConnection dayWiseConnection = null;

        boolean cal30min = false;
        List<ServerClientProtocol> tasksList = null;
        HashMap<String, Double> medianMap = null;
        HashMap<String, HttpURLConnection> connMap = null;
        Double dayWiseMedian = null;

        try{
            while (true) {
                System.out.println("Waiting for client to send tasks");
                clientSocket = schedulerServer.serverSocket.accept();
                InputStream buffer = new BufferedInputStream(clientSocket.getInputStream());
                ObjectInputStream objectInputStream = new ObjectInputStream(buffer);
                SchedulerServer dummy = new SchedulerServer();
                URLGeneration urlGeneration = new URLGeneration();
                long endTime = (long)Double.POSITIVE_INFINITY;
                List<ServerClientProtocol> line;
                while ((line= (List<ServerClientProtocol>) objectInputStream.readObject()) != null) {
                    System.out.println(line);
                    tasksList = line;
                    if(daywise == false){
                        System.out.println("Getting emissions/forecasts/current API response for today");
                        currentDateTime = new Date();
                        currentDate = formatter.parse(formatter.format(currentDateTime));
                        String daywiseURL = urlGeneration.apiCallDaily();
                        URL dayWiseUrl = new URL(daywiseURL);
                        dayWiseConnection = (HttpURLConnection) dayWiseUrl.openConnection();
                        dayWiseConnection.setRequestProperty("accept", "application/json");
                        dayWiseConnection.setRequestMethod("GET");
                        System.out.println("BEFORE MEDIAN EMISSION IS CALLED FOR DAY WISE ");
                        dayWiseMedian = new SchedulerServer().medianEmissions(dayWiseConnection);
                        daywise = true;
                    }

                    int result = (formatter.parse(formatter.format(new Date()))).compareTo(currentDate);
                    if (result!=0) {
                        daywise = false;
                        continue;
                    }

                    if(cal30min == false){
                        System.out.println("Getting emissions/forecasts/current API response " +
                                "for 30 minute interval");
                        long currentTime = System.currentTimeMillis();
                        endTime = currentTime + 1800000;
                        String thirtyURLeastus = urlGeneration.apiCall30Min("eastus");
                        URL thirtyUrleastus = new URL(thirtyURLeastus);
                        connectionThirtyeastus = (HttpURLConnection) thirtyUrleastus.openConnection();
                        connectionThirtyeastus.setRequestProperty("accept", "application/json");
                        connectionThirtyeastus.setRequestMethod("GET");
                        String thirtyURLuksouth = urlGeneration.apiCall30Min("uksouth");
                        URL thirtyUrluksouth = new URL(thirtyURLuksouth);
                        connectionThirtyuksouth = (HttpURLConnection) thirtyUrluksouth.openConnection();
                        connectionThirtyuksouth.setRequestProperty("accept", "application/json");
                        connectionThirtyuksouth.setRequestMethod("GET");
                        medianMap = new HashMap<>();
                        connMap = new HashMap<>();
                        connMap.put("eastus",connectionThirtyeastus);
                        connMap.put("uksouth",connectionThirtyuksouth);
                        Double medianValEastus = new SchedulerServer().medianEmissions(connMap.get("eastus"));
                        medianMap.put("eastus", medianValEastus);
                        Double medianValUksouth = new SchedulerServer().medianEmissions(connMap.get("uksouth"));
                        medianMap.put("uksouth", medianValUksouth);
                        cal30min = true;
                    }

                    if(System.currentTimeMillis() > endTime){
                        cal30min = false;
                        continue;
                    }

                    if(cal30min == true){
                        SchedulerServerThreadObject schedulerServerThreadObject = new SchedulerServerThreadObject();
                        schedulerServerThreadObject.setTaskList(tasksList);
                        schedulerServerThreadObject.setConnMap(connMap);
                        schedulerServerThreadObject.setEastus1(objectOutputStream1);
                        schedulerServerThreadObject.setEnergyListHigh(dummy.highPriorityEnergyList);
                        schedulerServerThreadObject.setEnergyListLow(dummy.lowPriorityEnergyList);
                        schedulerServerThreadObject.setEastus2(objectOutputStream2);
                        schedulerServerThreadObject.setUksouth(objectOutputStream3);
                        schedulerServerThreadObject.setDayWiseConn(dayWiseConnection);
                        schedulerServerThreadObject.setDayWiseMedian(dayWiseMedian);
                        schedulerServerThreadObject.setMedianMap(medianMap);
                        new SchedulerServer(schedulerServerThreadObject);
                    }
                }

                System.out.println("END OF WHILE : connection30eastus : " + connectionThirtyeastus + "connection30uksouth : "+ connectionThirtyuksouth + "daywise : "+dayWiseConnection);
            }

        } catch(Exception ex){
            ex.printStackTrace();
        } finally {
            schedulerServer.serverSocket.close();
            clientSocket.close();
        }
        }
}
