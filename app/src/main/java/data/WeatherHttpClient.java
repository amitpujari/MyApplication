package data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import Util.Util;

/**
 * Created by student on 10/4/2016.
 */

public class WeatherHttpClient {

    public String getWeatherData(String place){
        HttpURLConnection connection = null;
        InputStream inputStream = null;

        try {
            //creates connection to the internet
            connection = (HttpURLConnection)(new URL(Util.BASE_URL + place +Util.APPID_URL)).openConnection();
            //using this line to get information from the site
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            //connecting to the internet
            connection.connect();

            //Reads and stores the information into the stringbuffer
            StringBuffer stringBuffer = new StringBuffer();
            inputStream = connection.getInputStream();
            //reads the input stream
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;

            while((line = bufferedReader.readLine()) != null){
                stringBuffer.append(line + "\r\n");
            }
            inputStream.close();
            connection.disconnect();

            return stringBuffer.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
