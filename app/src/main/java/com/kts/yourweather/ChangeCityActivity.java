package com.kts.yourweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

/*
Created by mipolyakov on 23.09.2019
 */

public class ChangeCityActivity extends Activity {


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

    public void onClick_msk(View view) {
        TextView mskTextView = findViewById(R.id.textView3);
        Intent answerIntent1 = new Intent();
        answerIntent1.putExtra("put_city", mskTextView.getText().toString());
        setResult(RESULT_OK, answerIntent1);
        finish();
    }
    public void onClick_spb(View view) {
        TextView spbTextView = findViewById(R.id.textView4);
        Intent answerIntent2 = new Intent();
        answerIntent2.putExtra("put_city", spbTextView.getText().toString());
        setResult(RESULT_OK, answerIntent2);
        finish();
    }
    public void onClick_NY(View view) {
        TextView nyTextView = findViewById(R.id.textView5);
        Intent answerIntent3 = new Intent();
        answerIntent3.putExtra("put_city", nyTextView.getText().toString());
        setResult(RESULT_OK, answerIntent3);
        finish();
    }

}
