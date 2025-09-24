package com.example.aplikasi_berita.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.aplikasi_berita.R;
import com.example.aplikasi_berita.model.Article;
import com.example.aplikasi_berita.view.DetailActivity;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<Article> articleList;

    public NewsAdapter(List<Article> articleList) {
        this.articleList = articleList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_berita, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        Article currentArticle = articleList.get(position);

        holder.tvJudul.setText(currentArticle.getTitle());
        holder.tvSumber.setText(currentArticle.getSourceName());

        Glide.with(holder.itemView.getContext())
                .load(currentArticle.getUrlToImage())
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .into(holder.imgThumbnail);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DetailActivity.class);
            intent.putExtra("url", currentArticle.getUrl());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgThumbnail;
        public TextView tvJudul;
        public TextView tvSumber;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.img_thumbnail);
            tvJudul = itemView.findViewById(R.id.tv_judul);
            tvSumber = itemView.findViewById(R.id.tv_sumber);
        }
    }
}