package com.kts.yourweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.kts.yourweather.interfaces.OpenWeather;
import com.kts.yourweather.model.WeatherRequest;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "myLogs";
    private TextView temperature;
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
    OpenWeather openWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadTheme();
        setContentView(R.layout.activity_main);

        initGui();
        loadPreferences();
        initRetrofit();
        initFillingRecycleArrays();
        initEvents();
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
        temperature = findViewById(R.id.textView);
        currentCityTextView = findViewById(R.id.currentCity);
    }

    private void initRetrofit() {
        Retrofit retrofit;
        retrofit = new Retrofit.Builder().baseUrl("https://api.openweathermap.org/").addConverterFactory(GsonConverterFactory.create()).build();
        openWeather = retrofit.create(OpenWeather.class);
        requestRetrofit(currentCityTextView.getText().toString(), "bffab533dd87ce4285f3b672cfb5cf29");
    }

    private void requestRetrofit(String city, String keyApi) {
        openWeather.loadWeather(city, keyApi).enqueue(new Callback<WeatherRequest>() {
            @Override
            public void onResponse(Call<WeatherRequest> call, Response<WeatherRequest> response) {
                if (response.body() != null){
                    tempCurrent = String.format("%.0f " + "º" + "C", response.body().getMain().getTemp());
                    temperature.setText(tempCurrent);
                }
            }
            @Override
            public void onFailure(Call<WeatherRequest> call, Throwable t) {
                temperature.setText("Ошибка");
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

                initRetrofit();
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
