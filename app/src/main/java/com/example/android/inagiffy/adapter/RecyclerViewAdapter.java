package com.example.android.inagiffy.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder viewHolder, final int position) {

        final Gif gifImages = mGifImages.get(position);

        Glide.with(mContext)
                .load(gifImages.getGifUrl())
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
        CardView parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            gifUrlImage = itemView.findViewById(R.id.gif_image);
            parentLayout = itemView.findViewById(R.id.gif_list_layout);
        }
    }

}
