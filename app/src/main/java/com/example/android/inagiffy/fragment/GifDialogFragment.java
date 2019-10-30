package com.example.android.inagiffy.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.example.android.inagiffy.R;
import com.example.android.inagiffy.databinding.DialogGifImageBinding;


public class GifDialogFragment extends DialogFragment {

    private DialogGifImageBinding binding;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        // DataBinding
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_gif_image, container, false);

        binding.dialogImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        return binding.getRoot();
    }

}
