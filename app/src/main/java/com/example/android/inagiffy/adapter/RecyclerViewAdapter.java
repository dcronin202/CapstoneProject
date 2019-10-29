package com.example.android.inagiffy.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.inagiffy.R;
import com.example.android.inagiffy.data.Gif;

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

        viewHolder.gifUrl.setText(gifImages.getTrendingGifUrl());

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

        TextView gifUrl;
        LinearLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            gifUrl = itemView.findViewById(R.id.gif_url_textview);
            parentLayout = itemView.findViewById(R.id.gif_list_layout);
        }
    }

}
