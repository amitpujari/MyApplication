package weatherbroadcast.weatherapp;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;

import Util.Util;
import data.CityPreference;
import data.JSONWeatherParser;
import data.WeatherHttpClient;
import model.Weather;

public class MainActivity extends AppCompatActivity {

    private TextView cityName;
    private TextView temp;
    //private TextView temp1;
    private ImageView iconView;
    private TextView description;
    private TextView humidity;
    private TextView pressure;
    private TextView wind;
    private TextView sunRise;
    private TextView sunSet;
    private TextView updated;

    Weather weather = new Weather();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = (TextView)findViewById(R.id.cityText);
        iconView = (ImageView)findViewById(R.id.thumbnailIcon);
        temp = (TextView)findViewById(R.id.tempText1);
        //temp1 = (TextView)findViewById(R.id.tempText1);
        wind = (TextView)findViewById(R.id.windText);
        description = (TextView)findViewById(R.id.cloudText);
        humidity = (TextView)findViewById(R.id.humidityText);
        pressure = (TextView)findViewById(R.id.pressureText);
        sunRise = (TextView)findViewById(R.id.sunriseText);
        sunSet = (TextView)findViewById(R.id.sunsetText);
        updated = (TextView)findViewById(R.id.updateText);

        CityPreference cityPreference = new CityPreference(MainActivity.this);

        renderWeatherData(cityPreference.getCity());

    }

    public void renderWeatherData(String city){
        WeatherTask weatherTask = new WeatherTask();
        weatherTask.execute(new String[]{city + "&units=metric"});
        DownloadImageTask imageTask = new DownloadImageTask();
        imageTask.execute();
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap>{


        @Override
        protected Bitmap doInBackground(String... code) {
            Bitmap bitmap = null;
            try {
                URL url = new URL(Util.ICON_URL + weather.currentCondition.getIcon() + ".png");
                bitmap = BitmapFactory.decodeStream((InputStream)url.getContent());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            iconView.setImageBitmap(bitmap);
            super.onPostExecute(bitmap);
        }
    }

    private class WeatherTask extends AsyncTask<String, Void, Weather>{


        @Override
        protected Weather doInBackground(String... params) {
            String data = ((new WeatherHttpClient()).getWeatherData(params[0]));
            weather.iconData=weather.currentCondition.getIcon();
            weather = JSONWeatherParser.getWeather(data);


            Log.v("data:" , weather.place.getCity());
            new DownloadImageTask().execute(weather.iconData);
            //log.v("data",weather.currentcondition.getdescription();
            return weather;
        }

        @Override
        protected void onPostExecute(Weather weather) {

            //Convert times into readable formats
            //get unix times and multiply by 1000 to get proper length since it converts down to milliseconds
            long unixSunrise = weather.place.getSunRise() * 1000;
            long unixSunset  = weather.place.getSunSet() * 1000;
            long unixUpdated = weather.place.getLastUpdated() * 1000;
            //do the conversion
            java.util.Date sunriseDate = new java.util.Date(unixSunrise);
            java.util.Date sunsetDate  = new java.util.Date(unixSunset);
            java.util.Date updatedDate = new java.util.Date(unixUpdated);
            //Strip away everything but the time in 24hr time (use hh in place of kk or 12hour clock)
            //need to change sunset and rise times to time zone for where the location is not my local timezone
            String sunriseTime = String.valueOf(android.text.format.DateFormat.format("kk:mm:ss zzz", sunriseDate));
            String sunsetTime  = String.valueOf(android.text.format.DateFormat.format("kk:mm:ss zzz", sunsetDate));
            String updatedTime = String.valueOf(android.text.format.DateFormat.format("kk:mm:ss zzz", updatedDate));

           //DecimalFormat decimalFormat=new DecimalFormat("#,#");
            //String tempFormat=decimalFormat.format(weather.currentCondition.getTemperature());


            //Celcius to Fahrenheit Multiply by 9, then divide by 5, then add 32

            cityName.setText("" + weather.place.getCity() + (", " + weather.place.getCountry()));
            temp.setText(weather.currentCondition.getTemperature() + " °C");
            //temp1.setText((int) (((weather.currentCondition.getTemperature() * 9) / 5) + 32)+" °F");
            description.setText("Condition: " + weather.currentCondition.getCondition() + " (" + weather.currentCondition.getDescription() + ")");
            humidity.setText("Humidity: " + weather.currentCondition.getHumidity() + "%");
            pressure.setText("Pressure: " + weather.currentCondition.getPressure() + "hpa");
            wind.setText("Wind: " +  weather.wind.getSpeed() + "mps");
            sunRise.setText("Sunrise: " + sunriseTime);
            sunSet.setText("Sunset: " + sunsetTime);
            updated.setText("Updated: " + updatedTime);
            super.onPostExecute(weather);
        }
    }

    public void showInputDialog(){
        final AlertDialog.Builder cityDialog = new AlertDialog.Builder(this);
        cityDialog.setTitle("Change City");

        final EditText cityText1 = new EditText(this);
        cityText1.setInputType(InputType.TYPE_CLASS_TEXT);
        cityText1.setHint("Name of City");
        cityDialog.setView(cityText1);
        cityDialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                CityPreference cityPreference = new CityPreference(MainActivity.this);
                cityPreference.setCity(cityText1.getText().toString());

                String newCity = cityPreference.getCity();
                renderWeatherData(newCity);

            }
        });
        cityDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch(id){
            case R.id.change_cityId:
                showInputDialog();
        }
        return super.onOptionsItemSelected(item);
    }
}

