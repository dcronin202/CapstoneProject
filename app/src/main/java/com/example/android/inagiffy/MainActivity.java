package com.example.android.inagiffy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
        Resources resources = getResources();

        // DataBinding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setupDataObservers();
        viewModel.getTrendingGifList(this);

        // RecyclerView
        recyclerViewAdapter = new RecyclerViewAdapter(this, new ArrayList<Gif>());
        binding.recyclerViewMain.setAdapter(recyclerViewAdapter);

        // Set image cards to display in a staggered grid layout
        int columnCount = resources.getInteger(R.integer.list_column_count);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        binding.recyclerViewMain.setLayoutManager(layoutManager);
        binding.recyclerViewMain.setItemAnimator(null);

        // Setup SearchView
        binding.searchViewMain.setQueryHint(resources.getString(R.string.search_hint));
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
        binding.errorMessage.setText(resources.getString(R.string.error_message));

        // Toolbar
        setSupportActionBar(binding.toolbar);
        Drawable overflowIcon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_menu_light_mode);
        binding.toolbar.setOverflowIcon(overflowIcon);

        // Screen orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Initialize Mobile Ads SDK
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        // Obtain the Firebase Analytics instance
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
        final Resources resources = getResources();

        switch (item.getItemId()) {
            case R.id.trending:
                Toast.makeText(this, resources.getString(R.string.menu_trending), Toast.LENGTH_SHORT).show();
                sortByTrending();
                return true;

            case R.id.favorites:
                Toast.makeText(this, resources.getString(R.string.menu_favorites), Toast.LENGTH_SHORT).show();
                sortByFavorites();
                return true;

            case R.id.display_mode:
                return true;

            case R.id.display_theme:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    setDarkMode();
                    Toast.makeText(this, resources.getString(R.string.menu_dark_theme), Toast.LENGTH_SHORT).show();
                } else {
                    item.setChecked(false);
                    setLightMode();
                    Toast.makeText(this, resources.getString(R.string.menu_light_theme), Toast.LENGTH_SHORT).show();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Set Display Mode
    private void setDarkMode() {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorBackgroundDarkMode)));
        Drawable overflowIcon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_menu_dark_mode);
        binding.toolbar.setOverflowIcon(overflowIcon);
        binding.mainActivityLayout.setBackgroundColor(getResources().getColor(R.color.colorBackgroundDarkMode));
    }

    private void setLightMode() {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorBackground)));
        Drawable overflowIcon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_menu_light_mode);
        binding.toolbar.setOverflowIcon(overflowIcon);
        binding.mainActivityLayout.setBackgroundColor(getResources().getColor(R.color.colorBackground));
    }

    // Swipe Refresh
    @Override
    public void onRefresh() {
        viewModel.loadGifImages(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        viewModel.loadGifImages(this);
        mFirebaseAnalytics.setCurrentScreen(this, getResources().getString(R.string.screen_name), null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
