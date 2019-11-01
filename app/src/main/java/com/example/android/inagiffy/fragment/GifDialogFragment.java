package com.example.android.inagiffy.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.example.android.inagiffy.R;
import com.example.android.inagiffy.data.Gif;
import com.example.android.inagiffy.databinding.DialogGifImageBinding;
import com.example.android.inagiffy.viewmodel.GifViewModel;

import java.util.List;


public class GifDialogFragment extends DialogFragment {

    private DialogGifImageBinding binding;
    private GifViewModel viewModel;
    private int gifIndex = 0;

    // Class Constructor
    public GifDialogFragment (int position) {
        gifIndex = position;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        // DataBinding
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_gif_image, container, false);

        setUpViewModel();

        shareGif();

        saveGif();

        return binding.getRoot();
    }

    // ViewModel
    private void setUpViewModel() {
        viewModel = ViewModelProviders.of(getActivity()).get(GifViewModel.class);
        viewModel.getGifImages().observe(this, new Observer<List<Gif>>() {
            @Override
            public void onChanged(List<Gif> gifs) {
                String gifUrl = gifs.get(gifIndex).getGifUrl();
                Glide.with(getActivity())
                        .load(gifUrl)
                        .into(binding.dialogImage);
            }
        });
    }

    // Save to Favorites
    private void saveGif() {
        final ToggleButton favorites = binding.buttonSave;
        favorites.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (favorites.isChecked()) {
                    Toast.makeText(getActivity(), "Saved to Favorites", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Removed from Favorites", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Share Button
    private void shareGif(){
        binding.buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Share Feature Coming Soon!", Toast.LENGTH_SHORT).show();

            }
        });
        /* Uri gifUri = Uri.parse("https://media.giphy.com/media/12NUbkX6p4xOO4/giphy.gif");
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/gif");
        shareIntent.putExtra(Intent.EXTRA_STREAM, gifUri);
        startActivity(shareIntent);*/
    }

}
