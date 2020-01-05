package com.kts.yourweather.presenter;

import com.kts.yourweather.R;

public class ImageSetter {

    public static int adviceImage(String description){
        switch (description){
            case ("Clear"): return R.drawable.sun240;
            case ("Clouds"): return R.drawable.cloudly200_f;
            case ("Rain"): return R.drawable.rain100_f;
            case ("Snow"): return R.drawable.snow192;
            case ("Fog"): return R.drawable.icons8_fog_100;
            default: return R.drawable.wtf_96;
        }
    }
}
