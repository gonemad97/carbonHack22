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

    //public static final JsonParser.Feature ALLOW_UNQUOTED_FIELD_NAMES;
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

    public void energyScenarios(HashMap<String, Integer> casesMap, ObjectOutputStream eastus1, ObjectOutputStream uksouth,
                                ObjectOutputStream eastus2, List<ServerClientProtocol> energyListLow,List<ServerClientProtocol> energyListHigh) throws IOException, ClassNotFoundException {

        System.out.println("Energy Scenarios*************");
        System.out.println(casesMap);
        if(casesMap.get("eastus") == 0 && casesMap.get("uksouth") == 1){
            List<List<ServerClientProtocol>> loadBalancingListsLow = loadBalancing(energyListLow);
            Map<String,List<ServerClientProtocol>> listMap1Low = new HashMap<>();
            Map<String,List<ServerClientProtocol>> listMap2Low = new HashMap<>();

            listMap1Low.put("Clean", loadBalancingListsLow.get(0));
            eastus1.writeObject(listMap1Low);

            listMap2Low.put("Clean", loadBalancingListsLow.get(1));
            eastus2.writeObject(listMap2Low);
            //load balancing: alternate from the dirty energy queue

        }
        else if(casesMap.get("eastus") == 0 && casesMap.get("uksouth") == 0){
            //low priority - to away
            Map<String, List<ServerClientProtocol>> lowPriorityMap = new HashMap<>();
            lowPriorityMap.put("Clean", energyListLow);
            uksouth.writeObject(lowPriorityMap);

        }
        else if(casesMap.get("eastus") == 1 && casesMap.get("uksouth") == 0){
            //low priority - send to away
            Map<String, List<ServerClientProtocol>> lowPriorityMap = new HashMap<>();
            lowPriorityMap.put("Clean", energyListLow);
            System.out.println("LOW PRIORITY MAP :: "+lowPriorityMap);
            uksouth.writeObject(lowPriorityMap);
        }
        if(casesMap.get("eastus") == 1 && casesMap.get("uksouth") == 1){
            //lp
            List<List<ServerClientProtocol>> loadBalancingLists = loadBalancing(energyListHigh);
            Map<String,List<ServerClientProtocol>> listMap1 = new HashMap<>();
            Map<String,List<ServerClientProtocol>> listMap2 = new HashMap<>();

            listMap1.put("Clean", loadBalancingLists.get(0));
            eastus1.writeObject(listMap1);

            listMap2.put("Clean", loadBalancingLists.get(1));
            eastus2.writeObject(listMap2);
            //hp
        }
        List<List<ServerClientProtocol>> loadBalancingLists = loadBalancing(energyListHigh);
        Map<String,List<ServerClientProtocol>> highPriorityMap1 = new HashMap<>();
        Map<String,List<ServerClientProtocol>> highPriorityMap2 = new HashMap<>();

        highPriorityMap1.put("dirty", loadBalancingLists.get(0));
        eastus1.writeObject(highPriorityMap1);
        System.out.println("HIGH PRIORITY MAP 1: "+highPriorityMap1);

        highPriorityMap2.put("dirty", loadBalancingLists.get(1));
        eastus2.writeObject(highPriorityMap2);
        System.out.println("HIGH PRIORITY MAP 2: "+highPriorityMap2);



    }

    public double medianEmissions(HttpURLConnection conn) throws IOException, ParseException {
        //set the location, time parameters in the url
//        if(flag){
        Double median = 0.0;
        InputStream responseStream = conn.getInputStream();
        System.out.println("response stream " + responseStream);
        JSONParser jsonParser = new JSONParser();
        try{
            InputStreamReader inputStreamReader = new InputStreamReader(responseStream, "UTF-8");
//            JSONArray jsonArray = (JSONArray)jsonParser.parse(
//                    new InputStreamReader(responseStream, "UTF-8"));
            JSONArray jsonArray = (JSONArray)jsonParser.parse(inputStreamReader);
            System.out.println(jsonArray.get(0));

            inputStreamReader.close();

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);


            HashMap<String,Object> result =
                    objectMapper.readValue(jsonArray.get(0).toString(), HashMap.class);
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
        System.out.println("Thread running.....*********");
        segregateTasksByPriority(this.SchedulerServerThreadObject.getTaskList());
        HashMap<String, HttpURLConnection> connectionMap = this.SchedulerServerThreadObject.getConnMap();
//        HashMap<String, Double> medianMap = new HashMap<>();
        HashMap<String, Integer> casesMap = new HashMap<>();
        HashMap<String, Double> medianMap = this.SchedulerServerThreadObject.getMedianMap();

        try {
//            Double medianValEastus = medianEmissions(connectionMap.get("eastus"));
//            medianMap.put("eastus", medianValEastus);
//            Double medianValUksouth = medianEmissions(connectionMap.get("uksouth"));
//            medianMap.put("uksouth", medianValUksouth);
//            Double dayWiseMedian = medianEmissions(this.SchedulerServerThreadObject.getDayWiseConn());
            for(String str: medianMap.keySet()){
                if(medianMap.get(str) > this.SchedulerServerThreadObject.getDayWiseMedian()){
                    casesMap.put(str, 1);
                }
                else{
                    casesMap.put(str, 0);
                }
            }
//            System.out.println("After Meidian Calc******************");
            System.out.println("low list "+lowPriorityEnergyList.size());
            System.out.println("high list"+highPriorityEnergyList.size());
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
                    "Usage: java EchoClient <host name> <port number>");
            System.exit(1);
        }
        //1 - client port number, 2- Scheduler server port number, 3- LocationBasedServer port number
        //listening
        SchedulerServer schedulerServer = new SchedulerServer();
        int clientPortNumber = Integer.parseInt(args[1]);
        System.out.println("Portnumber " + clientPortNumber);
//        ServerSocket serverSocket =
        schedulerServer.serverSocket = new ServerSocket(Integer.parseInt(args[1]));
        Socket clientSocket = null;
                //= serverSocket.accept();
//        InputStream inputStream = clientSocket.getInputStream();
//        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

        List<ServerClientProtocol> taskList = null;
        String hostName = args[0];
        int SchedulerL1PortNumber = Integer.parseInt(args[2]);
        Socket echoSocket1 = new Socket(hostName, SchedulerL1PortNumber);
        OutputStream outputStream1 = echoSocket1.getOutputStream();
        ObjectOutputStream objectOutputStream1 = new ObjectOutputStream(outputStream1);


        int SchedulerL2PortNumber = Integer.parseInt(args[3]);
        Socket echoSocket2 = new Socket(hostName, SchedulerL2PortNumber);
        OutputStream outputStream2 = echoSocket2.getOutputStream();
        ObjectOutputStream objectOutputStream2 = new ObjectOutputStream(outputStream2);

        int SchedulerL3PortNumber = Integer.parseInt(args[4]);
        Socket echoSocket3 = new Socket(hostName, SchedulerL3PortNumber);
        OutputStream outputStream3 = echoSocket3.getOutputStream();
        ObjectOutputStream objectOutputStream3 = new ObjectOutputStream(outputStream3);

        Queue<List<ServerClientProtocol>> taskQueue = new LinkedList<>();

        Date currentDateTime = null;
        Date currentDate = null;
        boolean daywise = false;
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

        HttpURLConnection connectionThirtyeastus = null;
        HttpURLConnection connectionThirtyuksouth = null;
        HttpURLConnection dayWiseConnection = null;

        boolean cal30min = false;
        SchedulerServer schedulerServerMedian= null;


        //List<List<ServerClientProtocol>> tempList = new ArrayList<>();
        try{
            while (true) {
                System.out.println("ENTER WHILE");
                clientSocket = schedulerServer.serverSocket.accept();
                InputStream inputStream = clientSocket.getInputStream();


                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                System.out.println("Object Input Stream : "+objectInputStream);
                SchedulerServer dummy = new SchedulerServer();
                URLGeneration urlGeneration = new URLGeneration();

                long endTime = (long)Double.POSITIVE_INFINITY;

                List<ServerClientProtocol> tasksList = (List<ServerClientProtocol>) objectInputStream.readObject();


                if(daywise == false){
                    //date = new Date();
                    currentDateTime = new Date();
                    currentDate = formatter.parse(formatter.format(currentDateTime));

                    String daywiseURL = urlGeneration.apiCallDaily();
                    URL dayWiseUrl = new URL(daywiseURL);
                    dayWiseConnection = (HttpURLConnection) dayWiseUrl.openConnection();
                    dayWiseConnection.setRequestProperty("accept", "application/json");
                    dayWiseConnection.setRequestMethod("GET");
                    daywise = true;
                }

                int result = (formatter.parse(formatter.format(new Date()))).compareTo(currentDate);
                if (result!=0) {
                    daywise = false;
                    continue;
                }

                if(cal30min == false){
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

                    schedulerServerMedian = new SchedulerServer();
                    cal30min = true;
                }

                if(System.currentTimeMillis() > endTime){
                    cal30min = false;
                    continue;
                }

                if(cal30min == true){
                    SchedulerServerThreadObject schedulerServerThreadObject = new SchedulerServerThreadObject();
                    schedulerServerThreadObject.setTaskList(tasksList);
                    System.out.println("tasksList " + tasksList);
                    HashMap<String, HttpURLConnection> connMap = new HashMap<>();
                    connMap.put("eastus",connectionThirtyeastus);
                    connMap.put("uksouth",connectionThirtyuksouth);
                    System.out.println("connMap " + connMap);

                    HashMap<String, Double> medianMap = new HashMap<>();
                    Double medianValEastus = schedulerServerMedian.medianEmissions(connMap.get("eastus"));
                    medianMap.put("eastus", medianValEastus);
                    Double medianValUksouth = schedulerServerMedian.medianEmissions(connMap.get("uksouth"));
                    medianMap.put("uksouth", medianValUksouth);
                    Double dayWiseMedian = schedulerServerMedian.medianEmissions(dayWiseConnection);


                    schedulerServerThreadObject.setConnMap(connMap);
                    schedulerServerThreadObject.setEastus1(objectOutputStream1);
                    schedulerServerThreadObject.setEnergyListHigh(dummy.highPriorityEnergyList);
                    schedulerServerThreadObject.setEnergyListLow(dummy.lowPriorityEnergyList);
                    schedulerServerThreadObject.setEastus2(objectOutputStream2);
                    schedulerServerThreadObject.setUksouth(objectOutputStream3);

                    schedulerServerThreadObject.setDayWiseConn(dayWiseConnection);

                    schedulerServerThreadObject.setDayWiseMedian(dayWiseMedian);
                    schedulerServerThreadObject.setMedianMap(medianMap);
//                    objectInputStream.close();
                    new SchedulerServer(schedulerServerThreadObject);
                }

                System.out.println("END OF WHILE : connection30eastus : " + connectionThirtyeastus + "connection30uksouth : "+ connectionThirtyuksouth + "daywise : "+dayWiseConnection);
                //System.out.println(dayWiseConnection);

            }

        } catch(Exception ex){
            ex.printStackTrace();
        } finally {
            schedulerServer.serverSocket.close();
//            clientSocket.close();

        }



        }
}
