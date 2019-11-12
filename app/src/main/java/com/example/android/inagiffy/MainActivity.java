package com.example.android.inagiffy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.android.inagiffy.adapter.RecyclerViewAdapter;
import com.example.android.inagiffy.data.Gif;
import com.example.android.inagiffy.databinding.ActivityMainBinding;
import com.example.android.inagiffy.viewmodel.GifViewModel;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static final String FAVORITES = "favorites";
    private GifViewModel viewModel;
    private RecyclerViewAdapter recyclerViewAdapter;
    private ActivityMainBinding binding;
    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // DataBinding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setupDataObservers();
        viewModel.getTrendingGifList(this);

        // RecyclerView
        recyclerViewAdapter = new RecyclerViewAdapter(this, new ArrayList<Gif>());
        binding.recyclerViewMain.setAdapter(recyclerViewAdapter);
        binding.recyclerViewMain.setLayoutManager(new LinearLayoutManager(this));

        // Setup SearchView
        binding.searchViewMain.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.getSearchGifList(binding.searchViewMain.getQuery().toString(), MainActivity.this);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // Load a Mobile Ad
        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adViewTest.loadAd(adRequest);

        // Swipe Refresh
        binding.swipeRefreshLayout.setOnRefreshListener(this);
        binding.swipeRefreshLayout.setColorScheme(android.R.color.holo_purple);

        // Error Message
        Resources resources = getResources();
        binding.errorMessage.setText(resources.getString(R.string.error_message));

        // Toolbar
        setSupportActionBar(binding.toolbar);

        // Screen orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Initialize Mobile Ads SDK
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        // Obtain the FirebaseAnalytics instance
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        /* For widget
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.get(FAVORITES) != null) {
                if (extras.get(FAVORITES) instanceof Boolean) {
                    Boolean isFavorites = (Boolean) extras.get(FAVORITES);
                    if (isFavorites) {
                        mainFragment.sortByFavorites();
                    }
                }
            }
        }*/
    }

    // ViewModel
    private void setupDataObservers() {
        viewModel = ViewModelProviders.of(this).get(GifViewModel.class);
        viewModel.setupSharedPref(this.getSharedPreferences("gif-app", Context.MODE_PRIVATE));
        viewModel.getGifImages().observe(this, new Observer<List<Gif>>() {
            @Override
            public void onChanged(List<Gif> gifs) {
                // Refresh spinner will remain visible while network call is being made
                if (binding.swipeRefreshLayout.isRefreshing()) {
                    binding.swipeRefreshLayout.setRefreshing(false);
                }

                if (gifs.size() > 0) {
                    recyclerViewAdapter.updateGifList(gifs);
                    binding.recyclerViewMain.setVisibility(View.VISIBLE);
                    binding.adViewTest.setVisibility(View.VISIBLE);
                    binding.errorMessage.setVisibility(View.GONE);
                } else {
                    binding.recyclerViewMain.setVisibility(View.GONE);
                    binding.adViewTest.setVisibility(View.GONE);
                    binding.errorMessage.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    // Menu Setup
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // Sort Methods for Menu
    public void sortByTrending() {
        viewModel.getTrendingGifList(this);
    }

    public void sortByFavorites() {
        viewModel.getFavorites(this);
    }

    // Menu Items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String trending = String.valueOf(R.string.menu_trending);
        String favorites = String.valueOf(R.string.menu_favorites);

        if (id == R.id.trending) {
            sortByTrending();
            Toast.makeText(this, "Trending", Toast.LENGTH_SHORT).show();
            logSelectedEvent(trending);
            return true;
        }

        if (id == R.id.favorites) {
            sortByFavorites();
            Toast.makeText(this, "Favorites", Toast.LENGTH_SHORT).show();
            logSelectedEvent(favorites);
            return true;
        }

        if (id == R.id.display_mode) {
            Toast.makeText(this, "Display Mode", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.loadGifImages(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    // Swipe Refresh
    @Override
    public void onRefresh() {
        viewModel.getTrendingGifList(this);
    }

    // Log Event for Firebase Analytics
    private void logSelectedEvent(String menuTitle) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, menuTitle);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

}
