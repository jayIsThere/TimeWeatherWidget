package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    TextView dateView;
    TextView cityView;
    TextView weatherView;
    TextView tempView;

    static RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dateView = findViewById(R.id.dateView);
        cityView = findViewById(R.id.cityView);
        weatherView = findViewById(R.id.weatherView);
        tempView = findViewById(R.id.tempView);
        ImageButton button = findViewById(R.id.imageButton);

        cityView.setText("Touch me!!");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CurrentCall();

            }

        });

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

    }

    private void CurrentCall() {
        String url = "http://api.openweathermap.org/data/2.5/weather?q=Gelsenkirchen&appid=db4644b2bdbe0f464e4f0278bbb866b7";

        // Show loading state
        cityView.setText("Loading...");
        weatherView.setText("");
        tempView.setText("");

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(String response) {

                try {

                    long now = System.currentTimeMillis();
                    Date date = new Date(now);

                    SimpleDateFormat simpleDateFormatDay = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat("HH:mm");
                    String getDay = simpleDateFormatDay.format(date);
                    String getTime = simpleDateFormatTime.format(date);

                    String getDate = getDay + "\n" + getTime;

                    dateView.setText(getDate);

                    JSONObject jsonObject = new JSONObject(response);

                    String city = jsonObject.getString("name");
                    cityView.setText(city);

                    JSONArray weatherJson = jsonObject.getJSONArray("weather");
                    JSONObject weatherObj = weatherJson.getJSONObject(0);

                    String weather = weatherObj.getString("main");
                    weatherView.setText(weather);

                    JSONObject tempK = new JSONObject(jsonObject.getString("main"));
                    double tempDo = (Math.round((tempK.getDouble("temp") - 273.15) * 100) / 100.0);
                    tempView.setText(tempDo + "°C");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Reset UI to error state
                cityView.setText("Error");
                weatherView.setText("");
                tempView.setText("");

                // Show error message based on the type of error
                String errorMessage;
                if (error.networkResponse == null) {
                    // Network error (no internet connection)
                    errorMessage = "Network error. Please check your internet connection.";
                } else {
                    // Server error
                    errorMessage = "Failed to fetch weather data. Please try again later.";
                }

                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }
        };

        request.setShouldCache(false);
        requestQueue.add(request);
    }
}