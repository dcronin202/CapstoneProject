package com.example.android.inagiffy.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.inagiffy.R;
import com.example.android.inagiffy.adapter.RecyclerViewAdapter;
import com.example.android.inagiffy.data.Gif;
import com.example.android.inagiffy.viewmodel.GifViewModel;

import java.util.ArrayList;
import java.util.List;


public class MainActivityFragment extends Fragment {

    private GifViewModel viewModel;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private TextView errorMessage;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), new ArrayList<Gif>());
        recyclerView = view.findViewById(R.id.recycler_view_main);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //errorMessage = view.findViewById(R.id.error_message);
        setupViewModel();

        return view;

    }

    private void setupViewModel() {
        viewModel = ViewModelProviders.of(getActivity()).get(GifViewModel.class);
        viewModel.getGifImages().observe(this, new Observer<List<Gif>>() {
            @Override
            public void onChanged(List<Gif> gifs) {
                if (gifs.size() > 0) {
                    recyclerViewAdapter.updateGifList(gifs);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });

        viewModel.getGifList();
    }

    public MainActivityFragment(){
    }

}
