package com.hle.calculator.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;

import com.hle.calculator.Model.ResultService;
import com.hle.calculator.R;
import com.hle.calculator.ViewModel.MainViewModel;

public class MainActivity extends AppCompatActivity {

    private static final String FRAGMENT_KEY = "FRAGMENT_KEY";
    private MainFragment mainFragment;
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        /*
        if (savedInstanceState != null){
            mainFragment = (MainFragment) getSupportFragmentManager().getFragment(savedInstanceState, FRAGMENT_KEY);
            if (mainFragment != null){
                mainFragment.onCreate(savedInstanceState);
            } */
        if (savedInstanceState == null) {
            mainViewModel = new ViewModelProvider(MainActivity.this).get(MainViewModel.class);
            mainViewModel.setResultService(new ResultService());
            mainFragment = new MainFragment();
            Log.d("TAG", "main oncreate, lager nytt frag");
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, mainFragment)
                    .commitNow();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        /*
        if (mainFragment != null) {
            this.getSupportFragmentManager().putFragment(outState, FRAGMENT_KEY,mainFragment);
        }*/

    }
}