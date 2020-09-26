package com.hle.calculator.View;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.hle.calculator.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        //this resets fragment viewmodel also, should be done differently
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new MainFragment())
                .commit();
    }
}