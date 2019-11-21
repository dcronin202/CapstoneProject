package com.example.android.inagiffy.fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.android.inagiffy.R;
import com.example.android.inagiffy.data.Gif;
import com.example.android.inagiffy.databinding.DialogGifImageBinding;
import com.example.android.inagiffy.viewmodel.GifViewModel;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class GifDialogFragment extends DialogFragment {

    private DialogGifImageBinding binding;
    private GifViewModel viewModel;
    private String gifId;
    private String gifUrl;
    private Boolean gifIsFavorite;
    private File gifFile;


    // Class Constructor
    public GifDialogFragment (String gifId) {
        this.gifId = gifId;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        // DataBinding
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_gif_image, container, false);

        setUpViewModel();

        setupShareButton();

        return binding.getRoot();
    }

    // ViewModel
    private void setUpViewModel() {
        final ToggleButton favorites = binding.buttonSave;
        final Resources resources = getResources();
        viewModel = ViewModelProviders.of(getActivity()).get(GifViewModel.class);
        viewModel.getGifImages().observe(this, new Observer<List<Gif>>() {
            @Override
            public void onChanged(List<Gif> gifList) {
                for (int indexForNetworkResults = 0; indexForNetworkResults < gifList.size(); indexForNetworkResults++) {
                    Gif gifFromNetwork = gifList.get(indexForNetworkResults);
                    if (gifFromNetwork.getGifId().equals(gifId)) {
                        // Get the selected gif image
                        gifUrl = gifFromNetwork.getGifUrl();
                        Glide.with(getActivity())
                                .load(gifUrl)
                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                .apply(RequestOptions.centerCropTransform())
                                .into(binding.dialogImage);

                        // Set the favorites icon to the correct state
                        gifIsFavorite = gifFromNetwork.getIsFavorite();
                        favorites.setChecked(gifIsFavorite);
                    }
                }

                // Save to favorites
                favorites.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (favorites.isChecked()) {
                            viewModel.addFavorite(gifId);
                            Toast.makeText(getActivity(), resources.getString(R.string.toast_favorites_saved), Toast.LENGTH_SHORT).show();
                        } else {
                            viewModel.removeFavorite(gifId);
                            Toast.makeText(getActivity(), resources.getString(R.string.toast_favorites_removed), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    // Share Button
    private void setupShareButton(){
        final Resources resources = getResources();
        binding.buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), resources.getString(R.string.toast_share_gif), Toast.LENGTH_SHORT).show();
                new GetGifFileOnDiskTask().execute(gifUrl);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        // Set the size of the DialogFragment to be a square 90% of the width of the screen
        Window window = getDialog().getWindow();
        Point size = new Point();

        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);

        int width = size.x;
        double adjustedWidth = width * 0.90;

        window.setLayout((int)(adjustedWidth), (int)(adjustedWidth));
        window.setGravity(Gravity.CENTER);
    }


    private class GetGifFileOnDiskTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            try {
                gifFile = Glide.with(getActivity()).asFile().load(strings[0]).submit().get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (gifFile != null) {
                Uri shareUri = FileProvider.getUriForFile(getActivity(), "com.example.android.inagiffy.fileprovider", gifFile);
                    if (shareUri != null) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, shareUri);
                    shareIntent.setType("image/gif");
                    shareIntent.setDataAndType(shareUri, getActivity().getContentResolver().getType(shareUri));
                    shareIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(shareIntent);
                }
            }
        }
    }

}
