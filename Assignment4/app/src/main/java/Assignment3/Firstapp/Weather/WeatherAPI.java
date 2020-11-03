package Assignment3.Firstapp.Weather;

import android.content.Context;
import android.nfc.Tag;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.text.DecimalFormat;

public class WeatherAPI {
    //377fde9efa673bc44398223f57dba385 key
    //api.openweathermap.org/data/2.5/weather?q={city name}&appid={API key}
    private static String APIKey = "377fde9efa673bc44398223f57dba385";
    private static String APIUrl = "https://api.openweathermap.org/data/2.5/weather?";
    private static final String TAG = "weatherAPI";
    private RequestQueue requestQueue;


    public WeatherAPI(Context context){
        this.requestQueue = Volley.newRequestQueue(context);
    }
    /*
    * The following method is based on an example from the android documentation found here:
    * https://developer.android.com/training/volley/simple
    * */
    public void requestWeatherData(String city, Response.Listener<String> responseCallback){
        String query = APIUrl + "q=" + city + "&appid=" + APIKey ;
        StringRequest request = new StringRequest(Request.Method.GET, query, responseCallback, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Problem with response from weather api query" + error.toString());
            }
        });

        this.requestQueue.add(request);
    }

    public String parseResponse(String response){
        String parsedResponse = "";
        try{
            JSONObject jsonResponse = new JSONObject(response);
            double temp = jsonResponse.getJSONObject("main").getDouble("temp");
            String Description = jsonResponse.getJSONArray("weather").getJSONObject(0).getString("description");

            //(K − 273.15) × 9/5 + 32 = °F.
            temp = (temp - 273.15) * 9/5 + 32; //convert to F
            parsedResponse = "Temp: " + new DecimalFormat("#.#").format(temp) + "\u00B0F" + ", " + Description;
        }catch (Exception e){
            parsedResponse = "There was a problem parsing the api response";
            Log.e(TAG, e.getMessage());
        }

        return parsedResponse;
    }
}
