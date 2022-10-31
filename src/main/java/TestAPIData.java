import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.corba.se.impl.orbutil.ObjectWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.fasterxml.jackson.core.*;

public class TestAPIData {
    public TestAPIData(){

    }

    public int random() throws IOException {
        URL url = new URL("https://carbon-aware-api.azurewebsites.net/emissions/bylocations?location=westus&location=eastus&time=2022-03-01T15%3A30%3A00Z&toTime=2022-03-01T18%3A30%3A00Z");

        // Open a connection(?) on the URL(??) and cast the response(???)
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("accept", "application/json");
        connection.setRequestMethod("GET");
//        InputStream responseStream = connection.getInputStream();
//        System.out.println(responseStream);

        // Manually converting the response body InputStream to APOD using Jackson
        ObjectMapper mapper = new ObjectMapper();
//        APOD apod = mapper.readValue(responseStream, APOD.class);
//
//// Finally we have the response
//        System.out.println(apod.title);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        System.out.println(content);
        return 0;
    }
    public static void main(String[] args) throws IOException {
        int inputStream = new TestAPIData().random();
        System.out.println(inputStream);
    }

}
