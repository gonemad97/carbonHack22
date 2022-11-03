import java.io.*;
import java.net.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class TestUrlGeneration {
    // This class tests URL Generation
    public static void main(String[] args) throws IOException, URISyntaxException {
        // Testing API calls for Daily Median and 30 Min Median computations
        String dailyMedianURL = apiCallDaily();
        String eastus30MinMedianURL = apiCall30Min("eastus");
        String westus30MinMedianURL = apiCall30Min("westus");
        String uksouth30MinMedianURL = apiCall30Min("uksouth");
        System.out.println(dailyMedianURL);
        System.out.println(eastus30MinMedianURL);
        System.out.println(westus30MinMedianURL);
        System.out.println(uksouth30MinMedianURL);
    }

    private static String apiCall30Min(String location) throws URISyntaxException, UnsupportedEncodingException {
        URI baseURI30MinMed = new URI("https://carbon-aware-api.azurewebsites.net/emissions/forecasts/current?");
        String locParam = location;
        Instant instant = Instant.now();
        Instant instantAdd5 = instant.plusMillis(5*60*1000);
        Instant instantAdd35 = instant.plusMillis(35*60*1000);
        String dataStartAt = instantAdd5.toString();
        String dataEndAt = instantAdd35.toString();
        List<List<String>> uriParameter30Min = new ArrayList<>();

        List<String> locationList = new ArrayList<>();
        locationList.add("location");
        locationList.add(locParam);
        uriParameter30Min.add(locationList);

        List<String> dataStartAtList = new ArrayList<>();
        dataStartAtList.add("dataStartAt");
        dataStartAtList.add(dataStartAt);
        uriParameter30Min.add(dataStartAtList);

        List<String> dataEndAtList = new ArrayList<>();
        dataEndAtList.add("dataEndAt");
        dataEndAtList.add(dataEndAt);
        uriParameter30Min.add(dataEndAtList);

        return new TestUrlGeneration().generateURL(baseURI30MinMed, uriParameter30Min);
    }

    private static String apiCallDaily() throws URISyntaxException, UnsupportedEncodingException {
        String[] locations = {"eastus", "westus", "uksouth"};
        List<List<String>> uriParameter = new ArrayList<>();
        for(String location: locations){
            List<String> paramMap = new ArrayList<>();
            paramMap.add("location");
            paramMap.add(location);
            uriParameter.add(paramMap);
        }
        URI baseURIDailyMed = new URI("https://carbon-aware-api.azurewebsites.net/emissions/forecasts/current?");
        return new TestUrlGeneration().generateURL(baseURIDailyMed, uriParameter);
    }

    public String generateURL(URI uri, List<List<String>> uriParameter) throws URISyntaxException, UnsupportedEncodingException {
        String queryParametersString = "";
        for(int i=0; i < uriParameter.size(); i++ ){
            queryParametersString = queryParametersString.concat(URLEncoder.encode(uriParameter.get(i).get(0), "UTF-8"));
            queryParametersString = queryParametersString.concat("=");
            queryParametersString = queryParametersString.concat(URLEncoder.encode(uriParameter.get(i).get(1), "UTF-8"));
            if (i!= uriParameter.size()-1){
                queryParametersString = queryParametersString.concat("&");
            }
        }
        return uri+queryParametersString;
    }
}