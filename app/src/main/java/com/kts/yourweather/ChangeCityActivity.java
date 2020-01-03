package com.kts.yourweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

/*
Created by mipolyakov on 23.09.2019
 */

public class ChangeCityActivity extends Activity {


    public static final int RESULT_GEO = 5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (MainActivity.mSettings.contains(MainActivity.APP_PREFERENCES_IS_DARK_THEME)){
            MainActivity.isDarkTheme = MainActivity.mSettings.getBoolean(MainActivity.APP_PREFERENCES_IS_DARK_THEME, false);
        }
        if (MainActivity.isDarkTheme) {
            setTheme(R.style.AppDarkThem);
        }
        else {
            setTheme(R.style.AppTheme);
        }
            setContentView(R.layout.change_city);
    }

    public void onClick_city1(View view) {
        TextView cuty1TextView = findViewById(R.id.textView3);
        Intent answerIntent1 = new Intent();
        answerIntent1.putExtra("put_city", cuty1TextView.getText().toString());
        setResult(RESULT_OK, answerIntent1);
        finish();
    }
    public void onClick_city2(View view) {
        TextView city2TextView = findViewById(R.id.textView4);
        Intent answerIntent2 = new Intent();
        answerIntent2.putExtra("put_city", city2TextView.getText().toString());
        setResult(RESULT_OK, answerIntent2);
        finish();
    }
    public void onClick_city3(View view) {
        TextView city3TextView = findViewById(R.id.textView5);
        Intent answerIntent3 = new Intent();
        answerIntent3.putExtra("put_city", city3TextView.getText().toString());
        setResult(RESULT_OK, answerIntent3);
        finish();
    }

    public void onClick_getLocation(View view) {
        Button buttonLocation = findViewById(R.id.buttonGeoLocation);
        Intent answerIntent5 = new Intent();
        answerIntent5.putExtra("put_city", "mestopolozhenie");
        setResult(RESULT_GEO, answerIntent5);
        finish();

    }

    public void onClick_find(View view) {
        EditText editTextCity = findViewById(R.id.editTextCity);
        Intent answerIntent0 = new Intent();
        if (editTextCity.getText().toString().isEmpty()){
            answerIntent0.putExtra("put_city", "Ошибка");
        } else {
            answerIntent0.putExtra("put_city", editTextCity.getText().toString());
        }
        setResult(RESULT_OK, answerIntent0);
        finish();
    }


}
