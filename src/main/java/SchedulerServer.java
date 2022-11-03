import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.*;
import java.util.*;

public class SchedulerServer implements Runnable {
    Thread t1;
    public List<ServerClientProtocol> highPriorityEnergyList = new ArrayList<>();
    public List<ServerClientProtocol> lowPriorityEnergyList = new ArrayList<>();

    public SchedulerServerThreadObject SchedulerServerThreadObject = new SchedulerServerThreadObject();

    public SchedulerServer(){

    }
    public SchedulerServer(SchedulerServerThreadObject SchedulerServerThreadObject){
        this.SchedulerServerThreadObject = SchedulerServerThreadObject;
        this.t1 = new Thread(this);
        this.t1.start();
    }
    public boolean segregateTasksByPriority(List<ServerClientProtocol> taskList) {
        //outer while loop
        //add try catch blocks
        List<ServerClientProtocol> scp = taskList;
        for(ServerClientProtocol scpObject: taskList){
            int taskPriority = scpObject.getPriority();

            if(taskPriority < 5){
                highPriorityEnergyList.add(scpObject);
            }
            else{
                lowPriorityEnergyList.add(scpObject);
            }
            System.out.println("dirty energy: " + highPriorityEnergyList + "clean energy " + lowPriorityEnergyList);
        }
        return true;
    }

    public List<List<ServerClientProtocol>> loadBalancing(List<ServerClientProtocol> energyList){
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
        return returnLists;
    }

    public void energyScenarios(HashMap<String, Integer> casesMap, ObjectOutputStream eastus, ObjectOutputStream uksouth,
                                ObjectOutputStream westus, List<ServerClientProtocol> energyListLow,List<ServerClientProtocol> energyListHigh) throws IOException {
        if(casesMap.get("eastus") == 0 && casesMap.get("uksouth") == 0 && casesMap.get("westus") == 0){
            uksouth.writeObject(energyListLow);
            //load balancing: alternate from the dirty energy queue
            List<List<ServerClientProtocol>> loadBalancingLists = loadBalancing(energyListHigh);
            Map<String,List<ServerClientProtocol>> listMap1 = new HashMap<>();
            Map<String,List<ServerClientProtocol>> listMap2 = new HashMap<>();

            listMap1.put("Clean", loadBalancingLists.get(0));
            eastus.writeObject(listMap1);

            listMap2.put("Clean", loadBalancingLists.get(1));
            westus.writeObject(listMap2);

        }
        else if(casesMap.get("eastus") == 0 && casesMap.get("uksouth") == 0 && casesMap.get("westus") == 1){
            uksouth.writeObject(energyListLow);
            //load balancing: alternate from the dirty energy queue
            List<List<ServerClientProtocol>> loadBalancingLists = loadBalancing(energyListHigh);
            Map<String,List<ServerClientProtocol>> listMap1 = new HashMap<>();
            Map<String,List<ServerClientProtocol>> listMap2 = new HashMap<>();

            listMap1.put("Clean", loadBalancingLists.get(0));
            eastus.writeObject(listMap1);

            listMap2.put("Clean", loadBalancingLists.get(1));
            westus.writeObject(listMap2);

        }
        else if(casesMap.get("eastus") == 0 && casesMap.get("uksouth") == 1 && casesMap.get("westus") == 0){
            //low priority - load balancing
            List<List<ServerClientProtocol>> loadBalancingLists = loadBalancing(energyListLow);
            eastus.writeObject(loadBalancingLists.get(0));
            westus.writeObject(loadBalancingLists.get(1));
            //high priority - send to freer server
            //free - relatively lesser no of tasks to finish
            if(loadBalancingLists.get(0).size() < loadBalancingLists.get(1).size()){
                //what if the hp tasks are supposed to be sent while the concurrent exec is still going on -
                //communicate with the scheduler server to check the status of the tasks
            }

        }
        if(casesMap.get("eastus") == 0 && casesMap.get("uksouth") == 1 && casesMap.get("westus") == 1){


        }
        if(casesMap.get("eastus") == 1 && casesMap.get("uksouth") == 0 && casesMap.get("westus") == 0){


        }
        if(casesMap.get("eastus") == 1 && casesMap.get("uksouth") == 0 && casesMap.get("westus") == 1){


        }
        if(casesMap.get("eastus") == 1 && casesMap.get("uksouth") == 1 && casesMap.get("westus") == 0){


        }
        if(casesMap.get("eastus") == 1 && casesMap.get("uksouth") == 1 && casesMap.get("westus") == 1){

        }
    }

    public double medianEmissions(HttpURLConnection conn) throws IOException, ParseException {
        //set the location, time parameters in the url
//        if(flag){
        Double median = 0.0;
        InputStream responseStream = conn.getInputStream();
        JSONParser jsonParser = new JSONParser();
        try{
            JSONArray jsonArray = (JSONArray)jsonParser.parse(
                    new InputStreamReader(responseStream, "UTF-8"));
            System.out.println(jsonArray.get(0));
            HashMap<String,Object> result =
                    new ObjectMapper().readValue(jsonArray.get(0).toString(), HashMap.class);
            List<APIResponseClass> responseObjectList= new ArrayList<>();
            List<LinkedHashMap> jsonResponse = (List<LinkedHashMap>) result.get("forecastData");
            System.out.println("json Response to string " + jsonResponse.get(0).toString());
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
            System.out.println(median);

            } catch(Exception ex){
                ex.printStackTrace();
            } finally {
                responseStream.close();
            }



        return median;
    }

    @Override
    public void run(){
        segregateTasksByPriority(this.SchedulerServerThreadObject.getTaskList());
        HashMap<String, HttpURLConnection> connectionMap = this.SchedulerServerThreadObject.getConnMap();
        HashMap<String, Double> medianMap = new HashMap<>();
        HashMap<String, Integer> casesMap = new HashMap<>();
        try {
            Double medianValEastus = medianEmissions(connectionMap.get("eastus"));
            medianMap.put("eastus", medianValEastus);
            Double medianValWestus = medianEmissions(connectionMap.get("westus"));
            medianMap.put("westus", medianValWestus);
            Double medianValUksouth = medianEmissions(connectionMap.get("uksouth"));
            medianMap.put("uksouth", medianValUksouth);
            Double dayWiseMedian = medianEmissions(this.SchedulerServerThreadObject.getDayWiseConn());
            for(String str: medianMap.keySet()){
                if(medianMap.get(str) > dayWiseMedian){
                    casesMap.put(str, 1);
                }
                else{
                    casesMap.put(str, 0);
                }
            }
            Map<String, List<ServerClientProtocol>> map = new HashMap<>();
            map.put("Clean", lowPriorityEnergyList);
            System.out.println("sending once *********");
            this.SchedulerServerThreadObject.getEastus().writeObject(map);
            System.out.println("sending twice *********");
            this.SchedulerServerThreadObject.getEastus().writeObject(map);
            Thread.sleep(50000);
//            energyScenarios(casesMap, this.SchedulerServerThreadObject.getEastus(),
//                    this.SchedulerServerThreadObject.getUksouth(),
//                    this.SchedulerServerThreadObject.getWestus(), this.SchedulerServerThreadObject.getEnergyListLow(),
//                    this.SchedulerServerThreadObject.getEnergyListHigh());

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }



    public static void main(String[] args) throws IOException, ClassNotFoundException, ParseException, URISyntaxException {
        if (args.length < 2) {
            System.err.println(
                    "Usage: java EchoClient <host name> <port number>");
            System.exit(1);
        }
        //1 - client port number, 2- Scheduler server port number, 3- LocationBasedServer port number
        //listening

        int clientPortNumber = Integer.parseInt(args[1]);
        System.out.println("Portnumber " + clientPortNumber);
        ServerSocket serverSocket =
                new ServerSocket(Integer.parseInt(args[1]));
        Socket clientSocket = serverSocket.accept();
        InputStream inputStream = clientSocket.getInputStream();
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

        List<ServerClientProtocol> taskList = null;
        String hostName = args[0];
        int SchedulerL1PortNumber = Integer.parseInt(args[2]);
        Socket echoSocket1 = new Socket(hostName, SchedulerL1PortNumber);
        OutputStream outputStream1 = echoSocket1.getOutputStream();
        ObjectOutputStream objectOutputStream1 = new ObjectOutputStream(outputStream1);

//        int SchedulerL2PortNumber = Integer.parseInt(args[3]);
//        Socket echoSocket2 = new Socket(hostName, SchedulerL2PortNumber);
//        OutputStream outputStream2 = echoSocket2.getOutputStream();
//        ObjectOutputStream objectOutputStream2 = new ObjectOutputStream(outputStream2);
//
//        int SchedulerL3PortNumber = Integer.parseInt(args[4]);
//        Socket echoSocket3 = new Socket(hostName, SchedulerL3PortNumber);
//        OutputStream outputStream3 = echoSocket3.getOutputStream();
//        ObjectOutputStream objectOutputStream3 = new ObjectOutputStream(outputStream3);

        int x = 0;
        int time = 0;
        boolean flag = false;
        try{
            while (true) {
                SchedulerServer dummy = new SchedulerServer();
                URLGeneration urlGeneration = new URLGeneration();
                //listening thread - the url check
                //if(in the 30 min period) - new SchedulerServer()
                //else - change url, continue
                //if(24 hours)
                boolean cal30min = false;
                boolean daywise = false;
                Date date = null;
                long endTime = (long)Double.POSITIVE_INFINITY;
                HttpURLConnection connectionThirtyeastus = null;
                HttpURLConnection connectionThirtyWestus = null;
                HttpURLConnection connectionThirtyuksouth = null;
                HttpURLConnection dayWiseConnection = null;
                while (clientSocket.isConnected()) {
                    List<ServerClientProtocol> tasksList = (List<ServerClientProtocol>) objectInputStream.readObject();
                    if(daywise == false){
                        date = new Date();
                        String daywiseURL = urlGeneration.apiCallDaily();
                        URL dayWiseUrl = new URL(daywiseURL);
                        dayWiseConnection = (HttpURLConnection) dayWiseUrl.openConnection();
                        dayWiseConnection.setRequestProperty("accept", "application/json");
                        dayWiseConnection.setRequestMethod("GET");
                        daywise = true;
                    }

                    if(new Date() != date){
                        daywise = false;
                    }

                    if(cal30min == false){
                        long currentTime = System.currentTimeMillis();
                        endTime = currentTime + 1800000;
                        String thirtyURLeastus = urlGeneration.apiCall30Min("eastus");
                        URL thirtyUrleastus = new URL(thirtyURLeastus);
                        connectionThirtyeastus = (HttpURLConnection) thirtyUrleastus.openConnection();
                        connectionThirtyeastus.setRequestProperty("accept", "application/json");
                        connectionThirtyeastus.setRequestMethod("GET");
                        String thirtyURLwestus = urlGeneration.apiCall30Min("westus");
                        URL thirtyUrlwestus = new URL(thirtyURLwestus);
                        connectionThirtyWestus = (HttpURLConnection) thirtyUrlwestus.openConnection();
                        connectionThirtyWestus.setRequestProperty("accept", "application/json");
                        connectionThirtyWestus.setRequestMethod("GET");
                        String thirtyURLuksouth = urlGeneration.apiCall30Min("uksouth");
                        URL thirtyUrluksouth = new URL(thirtyURLuksouth);
                        connectionThirtyuksouth = (HttpURLConnection) thirtyUrluksouth.openConnection();
                        connectionThirtyuksouth.setRequestProperty("accept", "application/json");
                        connectionThirtyuksouth.setRequestMethod("GET");
                        cal30min = true;
                    }
                    if(System.currentTimeMillis() > endTime){
                        cal30min = false;
                        continue;
                    }
                    if(cal30min == true){
                        SchedulerServerThreadObject schedulerServerThreadObject = new SchedulerServerThreadObject();
                        schedulerServerThreadObject.setTaskList(tasksList);
                        HashMap<String, HttpURLConnection> connMap = new HashMap<>();
                        connMap.put("eastus",connectionThirtyeastus);
                        connMap.put("westus",connectionThirtyWestus);
                        connMap.put("uksouth",connectionThirtyuksouth);
                        schedulerServerThreadObject.setConnMap(connMap);
                        schedulerServerThreadObject.setEastus(objectOutputStream1);
                        schedulerServerThreadObject.setEnergyListHigh(dummy.highPriorityEnergyList);
                        schedulerServerThreadObject.setEnergyListLow(dummy.lowPriorityEnergyList);
                        schedulerServerThreadObject.setDayWiseConn(dayWiseConnection);
                        new SchedulerServer(schedulerServerThreadObject);
                    }



                }
//            if(flag){
//
//
//
//                //URL thirtyUrl = new URL("https://carbon-aware-api.azurewebsites.net/emissions/forecasts/current?location=westus&dataStartAt=11%2F3%2F2022%205%3A05%3A00%20AM%20%2B00%3A00&dataEndAt=11%2F3%2F2022%205%3A35%3A00%20AM%20%2B00%3A00");
//                //URL dayWiseUrl = new URL("https://carbon-aware-api.azurewebsites.net/emissions/forecasts/current?location=eastus&location=westus&location=uksouth");
//                // Open a connection(?) on the URL(??) and cast the response(???)
//
//                try {
//                    int i = 0;
//                    double thirtyMinMedian = 0.0;
//                    HashMap<String, Double> thirtyMinMedians = new HashMap<>();
//                    while (i < locations.size()) {
//                        List<String> thirtyList = new ArrayList<>();
//                        String thirtyURL = urlGeneration.apiCall30Min(locations.get(i));
//                        URL thirtyUrl = new URL(thirtyURL);
//                        thirtyList.add(locations.get(i));
//                        HttpURLConnection connectionThirty = (HttpURLConnection) thirtyUrl.openConnection();
//                        connectionThirty.setRequestProperty("accept", "application/json");
//                        connectionThirty.setRequestMethod("GET");
//                        thirtyMinMedian = new SchedulerServer().medianEmissions(thirtyUrl, connectionThirty, thirtyList, true);
//                        thirtyMinMedians.put(locations.get(i),thirtyMinMedian);
//                        i++;
//                    }
//                    String daywiseURL = urlGeneration.apiCallDaily();
//                    URL dayWiseUrl = new URL(daywiseURL);
//                    HttpURLConnection dayWiseConnection = (HttpURLConnection) dayWiseUrl.openConnection();
//                    dayWiseConnection.setRequestProperty("accept", "application/json");
//                    dayWiseConnection.setRequestMethod("GET");
//                    double dayWiseMedian = new SchedulerServer().medianEmissions(dayWiseUrl, dayWiseConnection, locations, false);
//                    System.out.println("printing " + thirtyMinMedian + " " + dayWiseMedian);
//                    x=3;
//                    int medIndex =0;
//                    HashMap<String, Integer> casesMap = new HashMap<>();
//                    while(medIndex < thirtyMinMedians.size()){
//                        if(thirtyMinMedians.get(locations.get(medIndex)) > dayWiseMedian){
//                            casesMap.put(locations.get(medIndex), 1);
//                        }
//                        else{
//                            casesMap.put(locations.get(medIndex), 0);
//                        }
//                        medIndex++;
//                    }
//                    System.out.println(casesMap);
//                    //new SchedulerServer().energyScenarios(casesMap, objectOutputStream1, objectOutputStream2, objectOutputStream3, dummy.lowPriorityEnergyList, dummy.highPriorityEnergyList);

//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                } catch (URISyntaxException e) {
//                    throw new RuntimeException(e);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                } catch (ParseException e) {
//                    throw new RuntimeException(e);
//                }
//            }
            }

        } catch(Exception ex){
            ex.printStackTrace();
        } finally {
            serverSocket.close();
            clientSocket.close();
        }



        }
}
