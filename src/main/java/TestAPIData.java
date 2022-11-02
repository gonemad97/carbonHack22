import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class TestAPIData {
//    public HashMap<String, Double> averageEmissionsTest(URL APIUrl, HttpURLConnection conn, List<String> locations, boolean flag) throws IOException, ParseException {
//        //set the location, time parameters in the url
////        if(flag){
//        HashMap<String, Double> locationBasedAvg = new HashMap<>();
//        int i = 0;
//        double avgSum =0;
//        while(i < locations.size()){
//            InputStream responseStream = conn.getInputStream();
//            System.out.println(responseStream);
//            JSONParser jsonParser = new JSONParser();
//            JSONArray jsonArray = (JSONArray)jsonParser.parse(
//                    new InputStreamReader(responseStream, "UTF-8"));
//            ArrayList<HashMap<String, Object>> jsonList = new ArrayList<>();
//            System.out.println(jsonArray.get(0));
//            ObjectMapper mapper = new ObjectMapper();
//            HashMap<String,Object> result =
//                    new ObjectMapper().readValue(jsonArray.get(0).toString(), HashMap.class);
//            List<APIResponseClass> responseObjectList= new ArrayList<>();
//            List<Object> jsonResponse = (List<Object>) result.get("forecastData");
//            System.out.println("json Response to string " + jsonResponse.get(0).toString());
//            int object = 0;
//            int emissionsSum = 0;
//            while(object < jsonResponse.size()){
//                APIResponseClass apiResponseClass = mapper.readValue(jsonResponse.get(object), APIResponseClass.class);
    //We're checking api response class
//                responseObjectList.add(apiResponseClass);
//                emissionsSum += apiResponseClass.getRating();
//                object++;
//            }
//            double emissionsAvg = emissionsSum/ jsonArray.size();
//            if(flag){
//                locationBasedAvg.put(locations.get(i), emissionsAvg);
//            }
//            else{
//                avgSum += emissionsAvg;
//            }
//
//            double dailyAvg = avgSum/3;
//            locationBasedAvg.put("dailyAvg", dailyAvg);
//
//            i++;
//        }
//        return locationBasedAvg;
//    }

    public TestAPIData(){

    }

    public int random() throws IOException, ParseException {
//        URL url = new URL("https://carbon-aware-api.azurewebsites.net/emissions/bylocations?location=westus&location=eastus&time=2022-03-01T15%3A30%3A00Z&toTime=2022-03-01T18%3A30%3A00Z");
//
//        // Open a connection(?) on the URL(??) and cast the response(???)
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//        connection.setRequestProperty("accept", "application/json");
//        connection.setRequestMethod("GET");
//        InputStream responseStream = connection.getInputStream();
//        JSONParser jsonParser = new JSONParser();
//        JSONArray jsonArray = (JSONArray)jsonParser.parse(
//                new InputStreamReader(responseStream, "UTF-8"));
//        ObjectMapper mapper = new ObjectMapper();
//        List<APIResponseClass> responseObjectList= new ArrayList<>();
//        int object = 0;
//        while(object < jsonArray.size()){
//            APIResponseClass apiResponseClass = mapper.readValue(jsonArray.get(object).toString(), APIResponseClass.class);
//
//        }
//        APIResponseClass apiResponseClass = mapper.readValue(jsonArray.get(0).toString(), APIResponseClass.class);

        List<String> locations = new ArrayList<>();


        Date startTime = null;
        Date endTime = null;
        double thirtyAvgDate = 0.0;
        double dayWiseAvgDate = 0.0;
        String loc1 = "eastus";
        String loc2 = "westus";
        String loc3 = "ukSouth";
        locations.add(loc1);
//        locations.add(loc2);
//        locations.add(loc3);
        boolean flag = true;

        URL thirtyUrl = new URL("https://carbon-aware-api.azurewebsites.net/emissions/forecasts/current?location=eastus&dataStartAt=11%2F2%2F2022%2003%3A00%3A00%20AM%20%2B00%3A00&dataEndAt=11%2F2%2F2022%2003%3A30%20AM%20%2B00%3A00");
        String dayWiseAPIUrl = "https://carbon-aware-api.azurewebsites.net/emissions/forecasts/current?location=eastus&location=westus&location=uksouth";
        URL dayWiseUrl = new URL(dayWiseAPIUrl);
        // Open a connection(?) on the URL(??) and cast the response(???)
        HttpURLConnection connectionThirty = (HttpURLConnection) thirtyUrl.openConnection();
        connectionThirty.setRequestProperty("accept", "application/json");
        connectionThirty.setRequestMethod("GET");
//        HashMap<String, Double> thirtyAvg = new TestAPIData().averageEmissionsTest(thirtyUrl, connectionThirty,locations, true);
//
//        HttpURLConnection dayWiseConnection = (HttpURLConnection) thirtyUrl.openConnection();
//        dayWiseConnection.setRequestProperty("accept", "application/json");
//        dayWiseConnection.setRequestMethod("GET");
//        HashMap<String, Double> dayWiseAvg = new TestAPIData().averageEmissionsTest(dayWiseUrl, dayWiseConnection, locations, false);

//        System.out.println(dayWiseAvg + " " + thirtyAvg);


        return 0;
    }
    public static void main(String[] args) throws IOException, ParseException {
        int inputStream = new TestAPIData().random();
        System.out.println(inputStream);
    }

}
