package com.example.aplikasi_berita.view;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.aplikasi_berita.R; // Pastikan import R sudah benar
import com.example.aplikasi_berita.adapter.NewsAdapter;
import com.example.aplikasi_berita.model.Article;
import com.example.aplikasi_berita.network.ApiConfig;
import com.example.aplikasi_berita.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private List<Article> articleList = new ArrayList<>();

    // ## PERUBAHAN 1: Deklarasi variabel untuk RecyclerView dan Adapter
    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ## PERUBAHAN 2: Menghubungkan layout XML ke Activity
        setContentView(R.layout.activity_main);

        // ## PERUBAHAN 3: Inisialisasi RecyclerView dan Adapter
        recyclerView = findViewById(R.id.recyclerViewNews);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inisialisasi adapter dengan list kosong terlebih dahulu
        newsAdapter = new NewsAdapter(articleList);
        recyclerView.setAdapter(newsAdapter);

        // Memulai proses pengambilan data
        fetchNewsData();
    }

    private void fetchNewsData() {
        String url = ApiConfig.BASE_URL + ApiConfig.EVERYTHING_ENDPOINT +
                "?q=teknologi" +
                "&language=id" +
                "&apiKey=" + ApiConfig.API_KEY;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    Log.d(TAG, "Respon diterima: " + response.toString());
                    try {
                        if (response.getString("status").equals("ok")) {
                            JSONArray articlesArray = response.getJSONArray("articles");

                            // Kosongkan list sebelum diisi data baru
                            articleList.clear();

                            for (int i = 0; i < articlesArray.length(); i++) {
                                JSONObject articleObject = articlesArray.getJSONObject(i);
                                String title = articleObject.getString("title");
                                String description = articleObject.getString("description");
                                String articleUrl = articleObject.getString("url");
                                String imageUrl = articleObject.getString("urlToImage");
                                String publishedAt = articleObject.getString("publishedAt");
                                JSONObject sourceObject = articleObject.getJSONObject("source");
                                String sourceName = sourceObject.getString("name");
                                Article article = new Article(title, description, articleUrl, imageUrl, publishedAt, sourceName);
                                articleList.add(article);
                            }

                            // ## PERUBAHAN 4: Memberi tahu adapter bahwa data telah berubah
                            newsAdapter.notifyDataSetChanged();

                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing JSON: " + e.getMessage());
                    }
                },
                error -> Log.e(TAG, "Error Volley: " + error.toString())
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("User-Agent", "Mozilla/5.0");
                return headers;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
}