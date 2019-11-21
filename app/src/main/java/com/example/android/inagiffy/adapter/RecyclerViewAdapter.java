package com.example.android.inagiffy.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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
    public void onBindViewHolder(final RecyclerViewAdapter.ViewHolder viewHolder, final int position) {

        final Gif gifImages = mGifImages.get(position);

        // Glide Setup
        Glide.with(mContext)
                .load(gifImages.getGifUrl())
                .placeholder(R.drawable.ic_gif_progress_placeholder)
                // Remove ProgressBar once gif has loaded (or if gif fails to load)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        viewHolder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        viewHolder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(viewHolder.gifUrlImage);

        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchDialogFragment(gifImages.getGifId());
            }
        });

    }

    // Method to launch DialogFragment
    private void launchDialogFragment(String id) {
        GifDialogFragment dialogFragment = new GifDialogFragment(id);
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
        ProgressBar progressBar;
        CardView parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gifUrlImage = itemView.findViewById(R.id.gif_image);
            progressBar = itemView.findViewById(R.id.gif_progress_bar);
            parentLayout = itemView.findViewById(R.id.gif_list_layout);
        }
    }

}
