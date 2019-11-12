package com.example.android.inagiffy.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private File gifFile;
    private String gifUrl;
    private ProgressDialog progress;

    // Class Constructor
    public GifDialogFragment (String gifId) {
        this.gifId = gifId;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        // DataBinding
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_gif_image, container, false);

        //progress = new ProgressDialog(getActivity());
        //progress.setTitle("Loading...");

        setUpViewModel();

        setupShareButton();

        setupSaveFavoriteButton();

        return binding.getRoot();
    }

    // ViewModel
    private void setUpViewModel() {
        viewModel = ViewModelProviders.of(getActivity()).get(GifViewModel.class);
        viewModel.getGifImages().observe(this, new Observer<List<Gif>>() {
            @Override
            public void onChanged(List<Gif> gifList) {
                for (int indexForNetworkResults = 0; indexForNetworkResults < gifList.size(); indexForNetworkResults++) {
                    Gif gifFromNetwork = gifList.get(indexForNetworkResults);
                    if (gifFromNetwork.getGifId().equals(gifId)) {
                        gifUrl = gifFromNetwork.getGifUrl();
                        Glide.with(getActivity())
                                .load(gifUrl)
                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                .into(binding.dialogImage);
                    }
                }
            }
        });
    }

    // Save to Favorites
    private void setupSaveFavoriteButton() {
        final ToggleButton favorites = binding.buttonSave;
        favorites.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (favorites.isChecked()) {
                    viewModel.addFavorite(gifId);
                    Toast.makeText(getActivity(), "Saved to Favorites", Toast.LENGTH_SHORT).show();
                } else {
                    viewModel.removeFavorite(gifId);
                    Toast.makeText(getActivity(), "Removed from Favorites", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Share Button
    private void setupShareButton(){
        binding.buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getActivity(), "Share Feature Coming Soon!", Toast.LENGTH_SHORT).show();
                //progress.show();
                new GetGifFileOnDiskTask().execute(gifUrl);
            }
        });

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
            progress.dismiss();
            if (gifFile != null) {
                Uri shareUri = FileProvider.getUriForFile(getActivity(), "com.example.android.inagiffy.fileprovider", gifFile);
                    if (shareUri != null) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("image/gif");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, shareUri);
                    shareIntent.setDataAndType(shareUri, getActivity().getContentResolver().getType(shareUri));
                    // TODO: Add text/WhatsApp as a share destination
                    shareIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(shareIntent);
                }
            }
        }
    }

}
