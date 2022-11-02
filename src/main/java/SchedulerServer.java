import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.*;
import java.util.*;

public class SchedulerServer {
    public List<ServerClientProtocol> dirtyEnergyList = new ArrayList<>();
    public List<ServerClientProtocol> cleanEnergyList = new ArrayList<>();
    public SchedulerServer(){

    }
    public boolean segregateTasksByPriority(List<ServerClientProtocol> taskList) {
        //outer while loop
        //add try catch blocks
        List<ServerClientProtocol> scp = taskList;
        for(ServerClientProtocol scpObject: taskList){
            int taskPriority = scpObject.getPriority();

            if(taskPriority < 5){
                dirtyEnergyList.add(scpObject);
            }
            else{
                cleanEnergyList.add(scpObject);
            }
            System.out.println("dirty energy: " + dirtyEnergyList + "clean energy " + cleanEnergyList);
        }
        return true;
    }

    public double averageEmissions(URL APIUrl, HttpURLConnection conn, List<String> locations, boolean flag) throws IOException, ParseException {
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

    public static void main(String[] args) throws IOException, ClassNotFoundException, ParseException {
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
//        int SchedulerL3PortNumber = Integer.parseInt(args[4]);
//        System.out.println("Port Number " + SchedulerL1PortNumber);

//        Socket echoSocket2 = new Socket(hostName, SchedulerL2PortNumber);
//        Socket echoSocket3 = new Socket(hostName, SchedulerL3PortNumber);
        System.out.println("Client side");

//        OutputStream outputStream2 = echoSocket2.getOutputStream();
//        OutputStream outputStream3 = echoSocket3.getOutputStream();


//        ObjectOutputStream objectOutputStream2 = new ObjectOutputStream(outputStream2);
//        ObjectOutputStream objectOutputStream3 = new ObjectOutputStream(outputStream3);
        int x =0;
        while (true) {

            SchedulerServer dummy = new SchedulerServer();
            Queue<List<ServerClientProtocol>> scpQueue = new LinkedList<>();
            boolean flag = false;
            System.out.println("x**************** " + x);
            while(clientSocket.isConnected() && x < 3){
                taskList = (List<ServerClientProtocol>) objectInputStream.readObject();
                scpQueue.add(taskList);
                System.out.println("Task List: " + taskList);
                dummy.segregateTasksByPriority(taskList);
//            if(flag == true){
//                //dequeue
//                //send the next element in the q
//            }
                //every 30 mins we need to check this other thread
                if(x == 2){
                    flag = true;
                    System.out.println("here ***************************");
                    break;
                }
                x++;
            }
//
            System.out.println("Scheduler Server Logic");

            if(flag) {
                List<String> locations = new ArrayList<>();


                Date startTime = null;
                Date endTime = null;
//        double thirtyAvgDate = 0.0;
//        double dayWiseAvgDate = 0.0;
                String loc1 = "eastus";
                String loc2 = "westus";
                String loc3 = "uksouth";
                locations.add(loc1);
                locations.add(loc2);
                locations.add(loc3);


                URL thirtyUrl = new URL("https://carbon-aware-api.azurewebsites.net/emissions/forecasts/current?location=westus&dataStartAt=11%2F3%2F2022%205%3A05%3A00%20AM%20%2B00%3A00&dataEndAt=11%2F3%2F2022%205%3A35%3A00%20AM%20%2B00%3A00");
                URL dayWiseUrl = new URL("https://carbon-aware-api.azurewebsites.net/emissions/forecasts/current?location=eastus&location=westus&location=uksouth");
                // Open a connection(?) on the URL(??) and cast the response(???)

                try {
                    int i = 0;
                    double thirtyMinMedian = 0.0;
                    HashMap<String, Double> thirtyMinMedians = new HashMap<>();
                    while (i < locations.size()) {
                        List<String> thirtyList = new ArrayList<>();

                        thirtyList.add(locations.get(i));
                        HttpURLConnection connectionThirty = (HttpURLConnection) thirtyUrl.openConnection();
                        connectionThirty.setRequestProperty("accept", "application/json");
                        connectionThirty.setRequestMethod("GET");
                        thirtyMinMedian = new SchedulerServer().averageEmissions(thirtyUrl, connectionThirty, thirtyList, true);
                        thirtyMinMedians.put(locations.get(i),thirtyMinMedian);
                        i++;
                    }
                    HttpURLConnection dayWiseConnection = (HttpURLConnection) dayWiseUrl.openConnection();
                    dayWiseConnection.setRequestProperty("accept", "application/json");
                    dayWiseConnection.setRequestMethod("GET");
                    double dayWiseMedian = new SchedulerServer().averageEmissions(dayWiseUrl, dayWiseConnection, locations, false);
                    System.out.println("printing " + thirtyMinMedian + " " + dayWiseMedian);
                    x=3;
                    int medIndex =0;
                    HashMap<String, Integer> casesMap = new HashMap<>();
                    while(medIndex < thirtyMinMedians.size()){
                        if(thirtyMinMedians.get(locations.get(medIndex)) > dayWiseMedian){
                            casesMap.put(locations.get(medIndex), 1);
                        }
                        else{
                            casesMap.put(locations.get(medIndex), 0);
                        }
                        medIndex++;
                    }
                    System.out.println(casesMap);
                    if(casesMap.get("eastus") == 0 && casesMap.get("uksouth") == 0 && casesMap.get("westus") == 0){
                        //send dummy.cleanEnergyList to uksouth -  objectOutputStream1.writeObject(dummy.cleanEnergyList);
                        //load balancing: alternate from the dirty energy queue
                        //put tasks in separate lists and send

                    }
                    if(casesMap.get("eastus") == 0 && casesMap.get("uksouth") == 0 && casesMap.get("westus") == 1){


                    }
                    if(casesMap.get("eastus") == 0 && casesMap.get("uksouth") == 1 && casesMap.get("westus") == 1){

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
                    objectOutputStream1.writeObject(dummy.cleanEnergyList);

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            else{
                serverSocket.close();
                clientSocket.close();
            }
        }

    }
}
