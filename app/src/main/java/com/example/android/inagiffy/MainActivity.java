package com.example.android.inagiffy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import com.example.android.inagiffy.fragment.MainActivityFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        launchMainActivityFragment();

    }

    // Menu Setup
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }


    // Method for Launching MainActivityFragment
    private void launchMainActivityFragment() {
        MainActivityFragment mainFragment = new MainActivityFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_activity_content, mainFragment)
                .commit();
    }

}
