package com.example.android.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<WeatherModel> WeatherList;

    List<HourlyModel> HourlyList;

    RecyclerView recyclerView, recyclerViewTwo;

    RecyclerView.LayoutManager recyclerViewLayoutManager, recyclerViewLayoutManagerTwo;

    RecyclerViewAdapter recyclerViewAdapter;

    RecyclerViewAdapterTwo recyclerViewAdapterTwo;

    SwipeRefreshLayout swipeRefreshLayout;

    Toolbar toolbar;

    RequestQueue requestQueue, requestQueueTwo;

    String cityLocation;

    ImageView currentWeatherIcon;

    TextView volleyError, location, currentTemperature, currentWind, currentHumidity, currentWeatherDescription, lastUpdated;

    AutoCompleteTextView autoCompleteTextView;

    String[] UK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UK = getResources().getStringArray(R.array.cities);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/LemonMilkbold.otf");
        currentTemperature = (TextView) findViewById(R.id.current_temperature);
        currentTemperature.setTypeface(tf);
        lastUpdated = (TextView) findViewById(R.id.last_updated);
        currentHumidity = (TextView) findViewById(R.id.current_humidity);
        currentWind = (TextView) findViewById(R.id.current_wind);
        currentWeatherDescription = (TextView) findViewById(R.id.current_weather_description);
        location = (TextView) findViewById(R.id.location_city);
        volleyError = (TextView) findViewById(R.id.volley_error);
        currentWeatherIcon = (ImageView) findViewById(R.id.current_weather_icon);

        cityLocation = "London, GB";

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.ColorAccentOne,
                R.color.ColorAccentTwo,
                R.color.ColorAccentThree,
                R.color.ColorAccentFour,
                R.color.ColorAccentFive);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                Toast.makeText(MainActivity.this, "Refreshing...", Toast.LENGTH_SHORT).show();
                getNetworkStatus(recyclerView);
                WeatherList.clear();
                HourlyList.clear();
                gatherWeatherData();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerViewLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        recyclerViewAdapter = new RecyclerViewAdapter(WeatherList, this);
        recyclerView.setHasFixedSize(true);

        recyclerViewTwo = (RecyclerView) findViewById(R.id.recycler_view2);
        recyclerViewLayoutManagerTwo = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewTwo.setLayoutManager(recyclerViewLayoutManagerTwo);
        recyclerViewAdapterTwo = new RecyclerViewAdapterTwo(HourlyList, this);
        recyclerViewTwo.setHasFixedSize(true);


        HourlyList = new ArrayList<HourlyModel>();
        WeatherList = new ArrayList<WeatherModel>();

        getNetworkStatus(recyclerView);
        gatherWeatherData();
    }


    public void getNetworkStatus(RecyclerView recyclerView) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI && activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                ;
            Snackbar snackbar = Snackbar.make(recyclerView, "for more weather information...", Snackbar.LENGTH_INDEFINITE)

                    .setAction("CLICK HERE", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent weatherBrowserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.bbc.co.uk/weather/"));
                            startActivity(weatherBrowserIntent);
                        }
                    });

            snackbar.setActionTextColor(getResources().getColor(R.color.ColorAccentOne));
            snackbar.show();
        } else {
            swipeRefreshLayout.setRefreshing(true);
            Snackbar snackbar = Snackbar.make(recyclerView, "Please connect to internet...", Snackbar.LENGTH_INDEFINITE)

                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            swipeRefreshLayout.setRefreshing(true);
                            Toast.makeText(MainActivity.this, "Refreshing...", Toast.LENGTH_SHORT).show();
                            WeatherList.clear();
                            HourlyList.clear();
                            gatherWeatherData();
                        }
                    });

            snackbar.setActionTextColor(getResources().getColor(R.color.MainColorOne));
            snackbar.show();
        }
    }

    public void hideSoftPad() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }


    private void gatherWeatherData() {

        // String to URL address containing data (A JsonObject) on the weather conditions for multiple (cnt) days for a specific location (cityLocation):
        String WEATHER_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?q=" + cityLocation + "&cnt=10&appid=8575b3580197e5a308710d8181a44ca2";

        // String to URL address containing data (A JsonObject) on the different weather conditions in a day (every 3 hours) for a specific location (cityLocation):
        String HOURLY_WEATHER_URL = "http://api.openweathermap.org/data/2.5/forecast?q=" + cityLocation + "&appid=8575b3580197e5a308710d8181a44ca2";

        swipeRefreshLayout.setRefreshing(true);

        getNetworkStatus(recyclerView);

        StringRequest stringRequest = new StringRequest
                (Request.Method.GET, WEATHER_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {

                                    // volleyError.setVisibility(View.GONE);
                                    JSONObject jsonObject = new JSONObject(response);

                                    JSONObject cityObject = jsonObject.getJSONObject("city");
                                    JSONArray listArray = jsonObject.getJSONArray("list");

                                    //
                                    Log.d("JSON RESPONSE", response.toString());
                                    Log.d("LIST ARRAY", listArray.toString());
                                    Log.d("LIST ITEM ZERO", listArray.getJSONObject(0).toString());
                                    Log.d("ITEM ZERO, WEATHER:", listArray.getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("main"));
                                    Log.d("ITEM LOCATION:", cityObject.getString("name"));

                                    for (int i = 1; i < listArray.length(); i++) {
                                        JSONObject listObjects = listArray.getJSONObject(i);
                                        JSONObject tempObjects = listObjects.getJSONObject("temp");
                                        JSONArray weatherArray = listObjects.getJSONArray("weather");
                                        JSONObject weatherObject = weatherArray.getJSONObject(0);

                                        WeatherModel itemOne = new WeatherModel(

                                                cityObject.getString("name"),
                                                cityObject.getString("country"),
                                                listObjects.getDouble("speed"),
                                                tempObjects.getDouble("min"),
                                                tempObjects.getDouble("max"),
                                                weatherObject.getInt("id"),
                                                weatherObject.getString("main"),
                                                weatherObject.getString("description"),
                                                listObjects.getLong("dt")
                                        );

                                        location.setText(cityObject.getString("name") + ", " + cityObject.getString("country"));

                                        // lastUpdated.setText(DateFormat.format("MMMM dd YYYY HHMM", weatherModel.getDate() * 1000L));

                                        WeatherList.add(itemOne);

                                    }

                                    recyclerViewAdapter = new RecyclerViewAdapter(WeatherList, getApplicationContext());
                                    recyclerView.setAdapter(recyclerViewAdapter);
                                    swipeRefreshLayout.setRefreshing(false);

                                } catch (JSONException e) {
                                    Toast.makeText(MainActivity.this, "Problems parsing data", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        },

                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {


                                Toast.makeText(MainActivity.this, "Problems using Volley", Toast.LENGTH_LONG).show();
                                swipeRefreshLayout.setRefreshing(false);

                            }

                        });

        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

        StringRequest stringRequestTwo = new StringRequest
                (Request.Method.GET, HOURLY_WEATHER_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                // Log.d("JSON RESPONSE", response.toString());
                                try {

                                    JSONObject hourlyJsonObject = new JSONObject(response);

                                    JSONArray hourlyListArray = hourlyJsonObject.getJSONArray("list");


                                    for (int i = 0; i < 15; i++) {
                                        JSONObject hourlyListObjects = hourlyListArray.getJSONObject(i);
                                        JSONObject hourlyMainObjects = hourlyListObjects.getJSONObject("main");
                                        JSONArray hourlyWeatherArray = hourlyListObjects.getJSONArray("weather");
                                        JSONObject hourlyWeatherObject = hourlyWeatherArray.getJSONObject(0);

                                        currentTemperature.setText(String.valueOf(Math.round(hourlyListArray.getJSONObject(0).getJSONObject("main").getDouble("temp") - 273.15)) + "°");
                                        currentHumidity.setText("Humidity: " + String.valueOf(Math.round(hourlyListArray.getJSONObject(0).getJSONObject("main").getDouble("humidity"))) + "%");
                                        currentWind.setText("Wind: " + String.valueOf(Math.round(hourlyListArray.getJSONObject(0).getJSONObject("wind").getDouble("speed"))) + "ᵐˢ");
                                        currentWeatherDescription.setText(hourlyListArray.getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("main") + ", " + hourlyListArray.getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("description"));

                                        int weatherId = (int) ((Math.floor(hourlyListArray.getJSONObject(0).getJSONArray("weather").getJSONObject(0).getDouble("id")) / 100));

                                        if (weatherId == 8) {
                                            if (Math.floor(hourlyListArray.getJSONObject(0).getJSONObject("main").getDouble("temp") - 273.15) <= 19) {
                                                currentWeatherIcon.setImageResource(R.drawable.ic_icicle);
                                            } else if (Math.floor(hourlyListArray.getJSONObject(0).getJSONObject("main").getDouble("temp") - 273.15) > 19) {
                                                currentWeatherIcon.setImageResource(R.drawable.ic_sunone);
                                            }
                                        } else {
                                            switch (weatherId) {
                                                case 2: // ID: 2xx, then Weather is Thunderstorm.
                                                    currentWeatherIcon.setImageResource(R.drawable.ic_lightning);
                                                    break;
                                                case 5: // ID: 5xx, then Weather is Rain.
                                                    currentWeatherIcon.setImageResource(R.drawable.ic_drop);
                                                    break;
                                                case 6: // ID: 6xx, then Weather is Snow.
                                                    currentWeatherIcon.setImageResource(R.drawable.ic_snowman);
                                                    break;
                                                case 9: // ID: 9xx, then Weather is Windy.
                                                    currentWeatherIcon.setImageResource(R.drawable.ic_fog);
                                                    break;
                                                case 3: // ID: 3xx, then Weather is Drizzle.
                                                    currentWeatherIcon.setImageResource(R.drawable.ic_drizzle);
                                                    break;
                                                case 7: // ID: 7xx, then Weather is Atmosphere.
                                                    currentWeatherIcon.setImageResource(R.drawable.ic_fog);
                                                    break;
                                            }

                                        }

                                        HourlyModel itemTwo = new HourlyModel(

                                                hourlyMainObjects.getDouble("temp"),
                                                hourlyMainObjects.getDouble("temp_min"),
                                                hourlyMainObjects.getDouble("temp_max"),
                                                hourlyWeatherObject.getInt("id"),
                                                hourlyListObjects.getLong("dt")
                                        );

                                        HourlyList.add(itemTwo);

                                    }

                                    recyclerViewAdapterTwo = new RecyclerViewAdapterTwo(HourlyList, getApplicationContext());
                                    recyclerViewTwo.setAdapter(recyclerViewAdapterTwo);
                                    swipeRefreshLayout.setRefreshing(false);

                                } catch (JSONException e) {
                                    Toast.makeText(MainActivity.this, "Problems parsing data", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                                swipeRefreshLayout.setRefreshing(false);

                            }
                        },

                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {


                                Toast.makeText(MainActivity.this, "Problems using Volley", Toast.LENGTH_LONG).show();
                                swipeRefreshLayout.setRefreshing(false);

                            }

                        });

        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequestTwo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.app_bar, menu);

        final View v = (View) menu.findItem(R.id.action_add).getActionView();

        autoCompleteTextView = (AutoCompleteTextView) v.findViewById(R.id.autocomplete_cities);

        ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, UK);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setAdapter(cityAdapter);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                cityLocation = parent.getItemAtPosition(position).toString();
                /*String WEATHER_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?q="+ cityLocation +"&cnt=8&appid=8575b3580197e5a308710d8181a44ca2";*/
                hideSoftPad();
                swipeRefreshLayout.setRefreshing(true);
                Toast.makeText(MainActivity.this, "Refreshing...", Toast.LENGTH_SHORT).show();
                WeatherList.clear();
                HourlyList.clear();
                getNetworkStatus(recyclerView);
                gatherWeatherData();
                autoCompleteTextView.setImeOptions(EditorInfo.IME_ACTION_DONE);
                autoCompleteTextView.clearFocus();
                autoCompleteTextView.getText().clear();
                // autoCompleteTextView.setText("");

            }
        });

        return true;
    }

    public void searchCity(View view) {

        cityLocation = autoCompleteTextView.getText().toString();
        hideSoftPad();
        swipeRefreshLayout.setRefreshing(true);
        Toast.makeText(MainActivity.this, "Refreshing...", Toast.LENGTH_SHORT).show();
        WeatherList.clear();
        HourlyList.clear();
        getNetworkStatus(recyclerView);
        gatherWeatherData();
        autoCompleteTextView.clearFocus();
        autoCompleteTextView.getText().clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {

            case R.id.action_add:
                Toast.makeText(this, "Action Add selected", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
