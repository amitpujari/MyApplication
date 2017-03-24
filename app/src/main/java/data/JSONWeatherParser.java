package data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Util.Util;
import model.Place;
import model.Weather;

/**
 * Created by student on 10/4/2016.
 */

public class JSONWeatherParser {
    public static Weather getWeather(String data){

        Weather weather = new Weather();

        try {
            JSONObject jsonObject = new JSONObject(data);

            Place place = new Place();

            JSONObject coordObj = Util.getObject("coord", jsonObject);

            place.setLat(Util.getFloat("lat", coordObj));
            place.setLon(Util.getFloat("lon", coordObj));

            //get the sys obj
            JSONObject sysObj = Util.getObject("sys", jsonObject);
            place.setCountry(Util.getString("country", sysObj));
            place.setLastUpdated(Util.getInt("dt", jsonObject));
            place.setSunRise(Util.getInt("sunrise", sysObj));
            place.setSunSet(Util.getInt("sunset", sysObj));
            place.setCity(Util.getString("name", jsonObject));

            weather.place = place;

            //Get weather info
            JSONArray jsonArray = jsonObject.getJSONArray("weather");
            JSONObject jsonWeather = jsonArray.getJSONObject(0);
            weather.currentCondition.setWeatherID(Util.getInt("id", jsonWeather));
            weather.currentCondition.setDescription(Util.getString("description", jsonWeather));
            weather.currentCondition.setCondition(Util.getString("main", jsonWeather));
            weather.currentCondition.setIcon(Util.getString("icon", jsonWeather));

            JSONObject windObj = Util.getObject("wind", jsonObject);
            weather.wind.setDeg(Util.getFloat("deg", windObj));
            weather.wind.setSpeed(Util.getFloat("speed", windObj));

            JSONObject cloudObj = Util.getObject("clouds", jsonObject);
            weather.cloud.setPercipatation(Util.getInt("all", cloudObj));

            JSONObject mainObj = Util.getObject("main" , jsonObject);
            weather.currentCondition.setTemperature(Util.getDouble("temp", mainObj));
            weather.currentCondition.setPressure(Util.getInt("pressure", mainObj));
            weather.currentCondition.setHumidity(Util.getInt("humidity", mainObj));
            weather.currentCondition.setMaxTemp(Util.getFloat("temp_max", mainObj));
            weather.currentCondition.setMinTemp(Util.getFloat("temp_min",mainObj));

            return weather;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
