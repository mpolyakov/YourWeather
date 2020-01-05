package com.kts.yourweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.kts.yourweather.interfaces.OpenWeatherByCity;
import com.kts.yourweather.interfaces.OpenWeatherByCityForecast;
import com.kts.yourweather.interfaces.OpenWeatherByGeoLocation;
import com.kts.yourweather.interfaces.OpenWeatherByGeoLocationForecast;
import com.kts.yourweather.model.WeatherRequest;
import com.kts.yourweather.model.forecast.WeatherForecastRequest;
import com.kts.yourweather.presenter.DateFormatter;
import com.kts.yourweather.presenter.ImageSetter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private TextView textTemperature;
    private String tempString = "+27";
    static final private int CHOOSE_CITY = 0;
    private ArrayList<String> mDays = new ArrayList<>();
    private ArrayList<String> mForecTemperature = new ArrayList<>();
    private ArrayList<Integer> mImagesId = new ArrayList<Integer>();
    private Snackbar mSnackbar;
    public static final String NAME_APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_IS_DARK_THEME = "theme";
    public static SharedPreferences mSettings;
    public static boolean isDarkTheme = false;
    public static final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather?q=Moscow,RU&appid=bffab533dd87ce4285f3b672cfb5cf29";
    public String tempCurrent = null;
    SharedPreferences.Editor editor = null;
    String currentCityTop = null;
    public static final String APP_PREFERENCES_CURRENT_CITY = "city";
    TextView currentCityTextView;
    OpenWeatherByCity openWeatherByCity;
    String KEYAPI = "bffab533dd87ce4285f3b672cfb5cf29";
    private String provider;
    String latFormat;
    String lonFormat;
    String accuracy;
    OpenWeatherByGeoLocation openWeatherByGeoLocation;
    OpenWeatherByCityForecast openWeatherByCityForecast;
    OpenWeatherByGeoLocationForecast openWeatherByGeoLocationForecast;
    String tempDay1, tempDay2,tempDay3, tempDay4, tempDay5, tempDay6, tempDay7;
    long day1, day2, day3, day4, day5, day6, day7;
    String todaysWeather;
    ImageView mainImage;
    String forecWeatherDay1, forecWeatherDay2, forecWeatherDay3, forecWeatherDay4, forecWeatherDay5, forecWeatherDay6, forecWeatherDay7;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        loadTheme();
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            requestLocation();
        } else {
            Toast.makeText(this, "Дайте приложению права на получение геолокации", Toast.LENGTH_LONG).show();
        }
        initGui();
        loadPreferences();
        initRetrofitByCity();

        initEvents();

    }

    private void initRetrofitByCityForecast() {
        Retrofit retrofit;
        retrofit = new Retrofit.Builder().baseUrl("https://api.openweathermap.org/").addConverterFactory(GsonConverterFactory.create()).build();
        openWeatherByCityForecast = retrofit.create(OpenWeatherByCityForecast.class);
        requestRetrofitByCityForecast(currentCityTextView.getText().toString(), KEYAPI);
    }

    private void requestRetrofitByCityForecast(String city, String keyApi) {
        openWeatherByCityForecast.loadWeather(city, keyApi).enqueue(new Callback<WeatherForecastRequest>() {
            @Override
            public void onResponse(Call<WeatherForecastRequest> call, Response<WeatherForecastRequest> response) {
                if (response.body() != null){
                    tempDay1 = String.format("%.0f " + "º" + "C", (response.body().getList()).get(1).getMain().getTemp());
                    tempDay2 = String.format("%.0f " + "º" + "C", (response.body().getList()).get(9).getMain().getTemp());
                    tempDay3 = String.format("%.0f " + "º" + "C", (response.body().getList()).get(17).getMain().getTemp());
                    tempDay4 = String.format("%.0f " + "º" + "C", (response.body().getList()).get(25).getMain().getTemp());
                    tempDay5 = String.format("%.0f " + "º" + "C", (response.body().getList()).get(33).getMain().getTemp());
                    tempDay6 = String.format("%.0f " + "º" + "C", (response.body().getList()).get(38).getMain().getTemp());
                    tempDay7 = String.format("%.0f " + "º" + "C", (response.body().getList()).get(39).getMain().getTemp());
                    day1 = response.body().getList().get(1).getDt();
                    day2 = response.body().getList().get(9).getDt();
                    day3 = response.body().getList().get(17).getDt();
                    day4 = response.body().getList().get(25).getDt();
                    day5 = response.body().getList().get(33).getDt();
                    day6 = response.body().getList().get(38).getDt();
                    day7 = response.body().getList().get(39).getDt();
                    forecWeatherDay1 = response.body().getList().get(1).getWeather().get(0).getMain();
                    forecWeatherDay2 = response.body().getList().get(9).getWeather().get(0).getMain();
                    forecWeatherDay3 = response.body().getList().get(17).getWeather().get(0).getMain();
                    forecWeatherDay4 = response.body().getList().get(25).getWeather().get(0).getMain();
                    forecWeatherDay5 = response.body().getList().get(33).getWeather().get(0).getMain();
                    forecWeatherDay6 = response.body().getList().get(38).getWeather().get(0).getMain();
                    forecWeatherDay7 = response.body().getList().get(39).getWeather().get(0).getMain();
                } else {
                    tempDay1 = ("-- " + "º" + "C");
                    tempDay2 = ("-- " + "º" + "C");
                    tempDay3 = ("-- " + "º" + "C");
                    tempDay4 = ("-- " + "º" + "C");
                    tempDay5 = ("-- " + "º" + "C");
                    tempDay6 = ("-- " + "º" + "C");
                    tempDay6 = ("-- " + "º" + "C");
                }
                initFillingRecycleArrays();
            }
            @Override
            public void onFailure(Call<WeatherForecastRequest> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Запрос не может быть выполнен", Toast.LENGTH_LONG).show();
                Log.d("WeatherForecastReqERROR", t.getMessage());
            }
        });
    }

    private void initRetrofitByGeoLocationForecast() {
        Retrofit retrofit;
        retrofit = new Retrofit.Builder().baseUrl("https://api.openweathermap.org/").addConverterFactory(GsonConverterFactory.create()).build();
        openWeatherByGeoLocationForecast = retrofit.create(OpenWeatherByGeoLocationForecast.class);
        requestRetrofitByGeoLocationForecast(latFormat, lonFormat, KEYAPI);
    }

    private void requestRetrofitByGeoLocationForecast(String lat, String lon, String keyApi) {
        openWeatherByGeoLocationForecast.loadWeather(lat, lon, keyApi).enqueue(new Callback<WeatherForecastRequest>() {
            @Override
            public void onResponse(Call<WeatherForecastRequest> call, Response<WeatherForecastRequest> response) {
                if (response.body() != null){
                    tempDay1 = String.format("%.0f " + "º" + "C", (response.body().getList()).get(1).getMain().getTemp());
                    tempDay2 = String.format("%.0f " + "º" + "C", (response.body().getList()).get(9).getMain().getTemp());
                    tempDay3 = String.format("%.0f " + "º" + "C", (response.body().getList()).get(17).getMain().getTemp());
                    tempDay4 = String.format("%.0f " + "º" + "C", (response.body().getList()).get(25).getMain().getTemp());
                    tempDay5 = String.format("%.0f " + "º" + "C", (response.body().getList()).get(33).getMain().getTemp());
                    tempDay6 = String.format("%.0f " + "º" + "C", (response.body().getList()).get(38).getMain().getTemp());
                    tempDay7 = String.format("%.0f " + "º" + "C", (response.body().getList()).get(39).getMain().getTemp());
                    day1 = response.body().getList().get(1).getDt();
                    day2 = response.body().getList().get(9).getDt();
                    day3 = response.body().getList().get(17).getDt();
                    day4 = response.body().getList().get(25).getDt();
                    day5 = response.body().getList().get(33).getDt();
                    day6 = response.body().getList().get(38).getDt();
                    day7 = response.body().getList().get(39).getDt();
                    forecWeatherDay1 = response.body().getList().get(1).getWeather().get(0).getMain();
                    forecWeatherDay2 = response.body().getList().get(9).getWeather().get(0).getMain();
                    forecWeatherDay3 = response.body().getList().get(17).getWeather().get(0).getMain();
                    forecWeatherDay4 = response.body().getList().get(25).getWeather().get(0).getMain();
                    forecWeatherDay5 = response.body().getList().get(33).getWeather().get(0).getMain();
                    forecWeatherDay6 = response.body().getList().get(38).getWeather().get(0).getMain();
                    forecWeatherDay7 = response.body().getList().get(39).getWeather().get(0).getMain();
                } else {
                    tempDay1 = ("-- " + "º" + "C");
                    tempDay2 = ("-- " + "º" + "C");
                    tempDay3 = ("-- " + "º" + "C");
                    tempDay4 = ("-- " + "º" + "C");
                    tempDay5 = ("-- " + "º" + "C");
                    tempDay6 = ("-- " + "º" + "C");
                    tempDay6 = ("-- " + "º" + "C");
                }
                initFillingRecycleArrays();
            }
            @Override
            public void onFailure(Call<WeatherForecastRequest> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Запрос не может быть выполнен", Toast.LENGTH_LONG).show();
                Log.d("WeatherForecastReqERROR", t.getMessage());
            }
        });
    }

    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Геолокация не может быть определена. Недостаточно прав.", Toast.LENGTH_LONG).show();
            return;
        }
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(criteria.ACCURACY_COARSE);
        provider = locationManager.getBestProvider(criteria, true);
        if (provider != null){
            locationManager.requestLocationUpdates(provider, 10000, 10, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    latFormat = String.format("%.7f", location.getLatitude());
                    lonFormat = String.format("%.7f", location.getLongitude());
                    accuracy = Float.toString(location.getAccuracy());
                }
                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {
                }
                @Override
                public void onProviderEnabled(String s) {
                }
                @Override
                public void onProviderDisabled(String s) {
                }
            });
        }
    }

    private void initEvents() {
        ImageView imageViewBrowser = findViewById(R.id.imageViewInternet);
        imageViewBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSnackbar = Snackbar.make(view, "Вы хотите перейти на gismeteo.ru?", Snackbar.LENGTH_LONG).setAction("Да", snackbarOnClickListener);
                mSnackbar.show();
            }
        });
    }

    private void loadTheme() {
        mSettings = getSharedPreferences(NAME_APP_PREFERENCES, Context.MODE_PRIVATE);       //Создаём файл настроек
        if (mSettings.contains(APP_PREFERENCES_IS_DARK_THEME)){
            isDarkTheme = mSettings.getBoolean(APP_PREFERENCES_IS_DARK_THEME, true);
        }
        if (isDarkTheme) {
            setTheme(R.style.AppDarkThem);                                                  //Применяем тёмную тему
        }
        else {
            setTheme(R.style.AppTheme);                                                     //Применяем светлую тему
        }
    }

    private void loadPreferences() {
        if (mSettings.contains((APP_PREFERENCES_CURRENT_CITY))){
            currentCityTop = mSettings.getString(APP_PREFERENCES_CURRENT_CITY, "?");
            currentCityTextView.setText(currentCityTop);
        }
    }

    private void initGui() {
        TextView textViewDate = findViewById(R.id.textViewDate);
        textViewDate.setText(DateFormatter.fullDate(0));
        textTemperature = findViewById(R.id.textView);
        currentCityTextView = findViewById(R.id.currentCity);
        mainImage = findViewById(R.id.imageViewMain);
    }

    private void initRetrofitByCity() {
        initRetrofitByCityForecast();
        Retrofit retrofit;
        retrofit = new Retrofit.Builder().baseUrl("https://api.openweathermap.org/").addConverterFactory(GsonConverterFactory.create()).build();
        openWeatherByCity = retrofit.create(OpenWeatherByCity.class);
        requestRetrofitByCity(currentCityTextView.getText().toString(), KEYAPI);
    }

    private void requestRetrofitByCity(String city, String keyApi) {
        openWeatherByCity.loadWeather(city, keyApi).enqueue(new Callback<WeatherRequest>() {
            @Override
            public void onResponse(Call<WeatherRequest> call, Response<WeatherRequest> response) {
                if (response.body() != null){
                    tempCurrent = String.format("%.0f " + "º" + "C", response.body().getMain().getTemp());
                    textTemperature.setText(tempCurrent);
                    todaysWeather = response.body().getWeather()[0].getMain();
                    mainImage.setImageResource(ImageSetter.adviceImage(todaysWeather));
                    return;
                }
                currentCityTextView.setText("Ошибка");
                textTemperature.setText("-- " + "º" + "C");
                mainImage.setImageResource(R.drawable.wtf_96);
            }
            @Override
            public void onFailure(Call<WeatherRequest> call, Throwable t) {
                Log.d("WeatherRequestERROR", t.getMessage());
                Toast.makeText(MainActivity.this, "Запрос не может быть выполнен", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initRetrofitByGeoLocation() {
        requestLocation();
        initRetrofitByGeoLocationForecast();
        Retrofit retrofit;
        retrofit = new Retrofit.Builder().baseUrl("https://api.openweathermap.org/").addConverterFactory(GsonConverterFactory.create()).build();
        openWeatherByGeoLocation = retrofit.create(OpenWeatherByGeoLocation.class);
        requestRetrofitByGeoLocation(latFormat, lonFormat, KEYAPI);
    }

    private void requestRetrofitByGeoLocation(String lat, String lon, String keyApi) {
        openWeatherByGeoLocation.loadWeather(lat, lon, keyApi).enqueue(new Callback<WeatherRequest>() {
            @Override
            public void onResponse(Call<WeatherRequest> call, Response<WeatherRequest> response) {
                if (response.body() != null){
                    currentCityTextView.setText(response.body().getName());
                    tempCurrent = String.format("%.0f " + "º" + "C", response.body().getMain().getTemp());
                    textTemperature.setText(tempCurrent);
                    todaysWeather = response.body().getWeather()[0].getMain();
                    mainImage.setImageResource(ImageSetter.adviceImage(todaysWeather));
                    return;
                }
                currentCityTextView.setText("Ошибка");
                textTemperature.setText("GPS off");
                mainImage.setImageResource(R.drawable.wtf_96);
            }
            @Override
            public void onFailure(Call<WeatherRequest> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Запрос не может быть выполнен", Toast.LENGTH_LONG).show();
            }
        });
    }

    View.OnClickListener snackbarOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String url = "https://gismeteo.ru";
            Uri uriGismeteo = Uri.parse(url);
            Intent browser = new Intent(Intent.ACTION_VIEW, uriGismeteo);
            startActivity(browser);
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("Temperature", tempString);
    }

    public void onClickChangeCity(View view) {
        Intent intentForResult = new Intent(MainActivity.this, ChangeCityActivity.class);
        startActivityForResult(intentForResult, CHOOSE_CITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_CITY){
            if (resultCode == RESULT_OK){
                currentCityTop = data.getExtras().getString("put_city");
                currentCityTextView.setText(currentCityTop);

                editor = mSettings.edit();                                                                  //Сохраняем настройки
                editor.putString(APP_PREFERENCES_CURRENT_CITY, currentCityTop);                              //Сохраняем настройки
                editor.apply();

                initRetrofitByCity();
            }
            if (resultCode == ChangeCityActivity.RESULT_GEO){
                initRetrofitByGeoLocation();
            }
        }
    }

    private void initFillingRecycleArrays(){
        mDays.clear();
        mForecTemperature.clear();
        mImagesId.clear();

        mDays.add(DateFormatter.dayOfTheWeek(day1));
        mDays.add(DateFormatter.dayOfTheWeek(day2));
        mDays.add(DateFormatter.dayOfTheWeek(day3));
        mDays.add(DateFormatter.dayOfTheWeek(day4));
        mDays.add(DateFormatter.dayOfTheWeek(day5));
        mDays.add(DateFormatter.dayOfTheWeek(day6));
        mDays.add(DateFormatter.dayOfTheWeek(day7));


        mForecTemperature.add(0, tempDay1);
        mForecTemperature.add(1, tempDay2);
        mForecTemperature.add(2, tempDay3);
        mForecTemperature.add(3, tempDay4);
        mForecTemperature.add(4, tempDay5);
        mForecTemperature.add(5, tempDay6);
        mForecTemperature.add(6, tempDay7);


        mImagesId.add(ImageSetter.adviceImage(forecWeatherDay1));
        mImagesId.add(ImageSetter.adviceImage(forecWeatherDay2));
        mImagesId.add(ImageSetter.adviceImage(forecWeatherDay3));
        mImagesId.add(ImageSetter.adviceImage(forecWeatherDay4));
        mImagesId.add(ImageSetter.adviceImage(forecWeatherDay5));
        mImagesId.add(ImageSetter.adviceImage(forecWeatherDay6));
        mImagesId.add(ImageSetter.adviceImage(forecWeatherDay7));

        initRecyclerView();

    }

    private void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(mDays, mForecTemperature, mImagesId, this);
        recyclerView.setAdapter(adapter);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        }
        else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onAboutMenuClick(MenuItem item) {
        Intent intent = new Intent(MainActivity.this, AboutActivity.class);
        startActivity(intent);
    }

    public void onThemeMenuClick(MenuItem item) {
        if (isDarkTheme) {
            setTheme(R.style.AppTheme);
            isDarkTheme = false;
        }
        else {
            setTheme(R.style.AppDarkThem);
            isDarkTheme = true;
        }
        SharedPreferences.Editor editor = mSettings.edit();                                         //Сохраняем настройки
        editor.putBoolean(APP_PREFERENCES_IS_DARK_THEME, isDarkTheme);                              //Сохраняем настройки
        editor.apply();                                                                             //Сохраняем настройки
        recreate();                                                                                 //пересоздаём активити
    }

}
