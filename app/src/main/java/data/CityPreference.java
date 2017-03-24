package data;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by student on 10/8/2016.
 */

public class CityPreference {
    SharedPreferences prefs;

    public CityPreference(Activity activity){
        prefs = activity.getPreferences(activity.MODE_PRIVATE);
    }

    public String getCity(){
        return prefs.getString("city" , "Mumbai,india");
    }
    public void setCity(String city){
        prefs.edit().putString("city", city).commit();

    }
}
