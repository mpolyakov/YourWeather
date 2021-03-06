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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.kts.yourweather.interfaces.OpenWeatherByCity;
import com.kts.yourweather.interfaces.OpenWeatherByGeoLocation;
import com.kts.yourweather.model.WeatherRequest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        initFillingRecycleArrays();
        initEvents();

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
                    String latitude = Double.toString(location.getLatitude());
                    latFormat = String.format("%.7f", location.getLatitude());
                    String longitude = Double.toString(location.getLongitude());
                    lonFormat = String.format("%.7f", location.getLongitude());
                    accuracy = Float.toString(location.getAccuracy());
//                    Toast.makeText(MainActivity.this, latFormat + "         " + lonFormat, Toast.LENGTH_SHORT).show();
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
        String pattern = "EE, dd MMM yyyy, HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, new Locale("ru", "RU"));
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+3"));
        String date = simpleDateFormat.format(new Date());
        TextView textViewDate = findViewById(R.id.textViewDate);
        textViewDate.setText(date);

        textTemperature = findViewById(R.id.textView);
        currentCityTextView = findViewById(R.id.currentCity);
    }

    private void initRetrofitByCity() {
        textTemperature.setText("-- " + "º" + "C");
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
                }
            }
            @Override
            public void onFailure(Call<WeatherRequest> call, Throwable t) {
                currentCityTextView.setText("Ошибка");
                textTemperature.setText("Ошибка");
            }
        });
    }

    private void initRetrofitByGeoLocation() {
        textTemperature.setText("-- " + "º" + "C");
        Retrofit retrofit;
        retrofit = new Retrofit.Builder().baseUrl("https://api.openweathermap.org/").addConverterFactory(GsonConverterFactory.create()).build();
        openWeatherByGeoLocation = retrofit.create(OpenWeatherByGeoLocation.class);
        requestLocation();
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
                }
            }
            @Override
            public void onFailure(Call<WeatherRequest> call, Throwable t) {
                currentCityTextView.setText("Ошибка");
                textTemperature.setText("Ошибка");
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
        mDays.add("ПН");
        mDays.add("ВТ");
        mDays.add("СР");
        mDays.add("ЧТ");
        mDays.add("ПТ");
        mDays.add("СБ");
        mDays.add("ВС");

        mForecTemperature.add(tempCurrent);
        mForecTemperature.add(tempCurrent);
        mForecTemperature.add(tempCurrent);
        mForecTemperature.add(tempCurrent);
        mForecTemperature.add(tempCurrent);
        mForecTemperature.add(tempCurrent);
        mForecTemperature.add(tempCurrent);

        mImagesId.add(R.drawable.sun240);
        mImagesId.add(R.drawable.snow192);
        mImagesId.add(R.drawable.sun240);
        mImagesId.add(R.drawable.storm100_f);
        mImagesId.add(R.drawable.rain100_f);
        mImagesId.add(R.drawable.cloudly200_f);
        mImagesId.add(R.drawable.sun240);

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
