package com.example.android.inagiffy.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android.inagiffy.MainActivity;
import com.example.android.inagiffy.R;
import com.example.android.inagiffy.data.Gif;
import com.example.android.inagiffy.fragment.GifDialogFragment;

import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<Gif> mGifImages;
    private Activity mContext;

    public RecyclerViewAdapter(Activity mContext, List<Gif> mGifDetails) {
        this.mGifImages = mGifDetails;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_gif_image, viewGroup, false);
        RecyclerViewAdapter.ViewHolder viewHolder = new RecyclerViewAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder viewHolder, int position) {

        final Gif gifImages = mGifImages.get(position);

        Glide.with(mContext)
                .load(gifImages.getGifUrl())
                .into(viewHolder.gifUrlImage);

        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                launchDialogFragment();

                Toast.makeText(mContext, gifImages.getGifId(), Toast.LENGTH_SHORT).show();
                ImageView imageView = view.findViewById(R.id.dialog_image);
                Glide.with(mContext).load(gifImages).into(imageView);

            }
        });

    }

    // Method to launch DialogFragment
    private void launchDialogFragment() {
        GifDialogFragment dialogFragment = new GifDialogFragment();
        dialogFragment.show(((MainActivity)mContext).getSupportFragmentManager(), "tag");
    }

    @Override
    public int getItemCount() {
        if (mGifImages == null) {
            return 0;
        } else {
            return mGifImages.size();
        }
    }

    public void updateGifList(List<Gif> gifContent) {
        this.mGifImages = gifContent;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView gifUrlImage;
        FrameLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            gifUrlImage = itemView.findViewById(R.id.gif_image);
            parentLayout = itemView.findViewById(R.id.gif_list_layout);
        }
    }

}
