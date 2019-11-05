package com.example.android.inagiffy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.inagiffy.fragment.MainActivityFragment;
import com.example.android.inagiffy.viewmodel.GifViewModel;


public class MainActivity extends AppCompatActivity {

    private MainActivityFragment mainFragment;
    GifViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = ViewModelProviders.of(this).get(GifViewModel.class);
        viewModel.setupSharedPref(getSharedPreferences("gif-app", Context.MODE_PRIVATE));

        launchMainActivityFragment();
    }

    // Menu Setup
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.trending) {
            mainFragment.sortByTrending();
            Toast.makeText(this, "Trending", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.favorites) {
            mainFragment.sortByFavorites();
            Toast.makeText(this, "Favorites", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.display_mode) {
            Toast.makeText(this, "Display Mode", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Method for Launching MainActivityFragment
    private void launchMainActivityFragment() {
        mainFragment = new MainActivityFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_activity_content, mainFragment)
                .commit();
    }

}
