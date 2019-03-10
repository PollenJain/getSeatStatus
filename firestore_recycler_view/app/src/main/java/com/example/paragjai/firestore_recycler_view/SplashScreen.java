package com.example.paragjai.firestore_recycler_view;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;


public class SplashScreen extends Activity {
    private ImageView imageView;
    private TextView textView;
    private Button button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        findViewById(R.id.idImageView);
        findViewById(R.id.tvGetSeatStatusDriver);
        findViewById(R.id.login_button);
    }
}
