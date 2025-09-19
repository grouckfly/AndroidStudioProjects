package com.example.aplikasi_berita.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import com.example.aplikasi_berita.view.DetailActivity;
import com.example.aplikasi_berita.R;
import com.example.aplikasi_berita.model.Article;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<Article> articleList;

    // Constructor untuk menerima data dari Activity
    public NewsAdapter(List<Article> articleList) {
        this.articleList = articleList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Membuat view baru dari layout list_item_berita.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_berita, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        // Mengambil satu artikel dari list berdasarkan posisinya
        Article currentArticle = articleList.get(position);

        // Mengikat data dari artikel ke ViewHolder (kode ini sudah ada)
        holder.tvTitle.setText(currentArticle.getTitle());
        holder.tvSource.setText(currentArticle.getSourceName());

        // ## TAMBAHAN BARU: Menambahkan OnClickListener ##
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Membuat Intent untuk membuka DetailActivity
                Intent intent = new Intent(v.getContext(), DetailActivity.class);

                // Mengirimkan URL artikel yang diklik ke DetailActivity
                intent.putExtra("url", currentArticle.getUrl());

                // Memulai Activity baru
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        // Mengembalikan jumlah total item dalam list
        return articleList.size();
    }


    /**
     * ViewHolder bertugas untuk menyimpan referensi ke setiap view di dalam satu item.
     * Ini meningkatkan performa karena kita tidak perlu memanggil findViewById() berulang kali.
     */
    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle;
        public TextView tvSource;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSource = itemView.findViewById(R.id.tvSource);
        }
    }
}