package com.example.android.inagiffy.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.android.inagiffy.R;
import com.example.android.inagiffy.adapter.RecyclerViewAdapter;
import com.example.android.inagiffy.data.Gif;
import com.example.android.inagiffy.databinding.FragmentMainBinding;
import com.example.android.inagiffy.viewmodel.GifViewModel;

import java.util.ArrayList;
import java.util.List;


public class MainActivityFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private GifViewModel viewModel;
    private RecyclerViewAdapter recyclerViewAdapter;
    private FragmentMainBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // DataBinding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);

        // Setup RecyclerView
        recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), new ArrayList<Gif>());
        binding.recyclerViewMain.setAdapter(recyclerViewAdapter);
        //binding.recyclerViewMain.setLayoutManager(new LinearLayoutManager(getActivity()));
        // TODO: Fix shuffle glitch and set the horizontal span to 3
        binding.recyclerViewMain.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        binding.recyclerViewMain.setItemAnimator(null);

        // Setup SearchView
        binding.searchViewMain.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            // Insert search results as a String in the getSearchGifList method
            public boolean onQueryTextSubmit(String query) {
                viewModel.getSearchGifList(binding.searchViewMain.getQuery().toString());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // Swipe Refresh
        binding.swipeRefreshLayout.setOnRefreshListener(this);

        setupDataObservers();
        viewModel.getTrendingGifList(this);

        return binding.getRoot();
    }

    private void setupDataObservers() {
        viewModel = ViewModelProviders.of(getActivity()).get(GifViewModel.class);
        //viewModel.setupSharedPref(getActivity().getSharedPreferences("gif_app", Context.MODE_PRIVATE));
        viewModel.getGifImages().observe(this, new Observer<List<Gif>>() {
            @Override
            public void onChanged(List<Gif> gifs) {
                // Refresh spinner will remain visible while network call is being made
                if(binding.swipeRefreshLayout.isRefreshing()) {
                    binding.swipeRefreshLayout.setRefreshing(false);
                }

                if (gifs.size() > 0) {
                    recyclerViewAdapter.updateGifList(gifs);
                    binding.recyclerViewMain.setVisibility(View.VISIBLE);
                    binding.searchViewMain.setVisibility(View.VISIBLE);
                    binding.errorMessage.setVisibility(View.GONE);

                } else {
                    binding.errorMessage.setVisibility(View.VISIBLE);
                    binding.recyclerViewMain.setVisibility(View.GONE);
                    binding.searchViewMain.setVisibility(View.GONE);
                }
            }
        });
    }

    public MainActivityFragment(){
    }

    @Override
    public void onResume() {
        super.onResume();
        //viewModel.loadGifImages(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    // Sort Methods for Menu
    public void sortByTrending() {
        viewModel.getTrendingGifList(this);
    }

    public void sortByFavorites() {
        viewModel.getFavorites(this);
    }

    // Swipe Refresh
    @Override
    public void onRefresh() {
        viewModel.getTrendingGifList(this);
    }
}
