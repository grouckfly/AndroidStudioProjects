package com.example.aplikasi_berita.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.tabs.TabLayout;
import com.example.aplikasi_berita.R;
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

    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private EditText etSearch;
    private ImageView ivSearch;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        progressBar = findViewById(R.id.progress_bar);
        recyclerView = findViewById(R.id.rv_berita);
        etSearch = findViewById(R.id.et_search);
        ivSearch = findViewById(R.id.iv_search);
        tabLayout = findViewById(R.id.tab_layout);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsAdapter = new NewsAdapter(articleList);
        recyclerView.setAdapter(newsAdapter);

        setupTabLayout();

        // Memuat berita default dari tab pertama saat aplikasi dibuka
        fetchNewsData("Teknologi");

        ivSearch.setOnClickListener(v -> handleSearch());

        // Menambahkan listener agar bisa search dengan menekan tombol enter/search di keyboard
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            handleSearch();
            return true;
        });
    }

    private void handleSearch() {
        String query = etSearch.getText().toString().trim();
        if (!query.isEmpty()) {
            fetchNewsData(query);
        } else {
            Toast.makeText(MainActivity.this, "Silakan masukkan kata kunci", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupTabLayout() {
        String[] categories = {"Teknologi", "Bisnis", "Olahraga", "Kesehatan", "Hiburan", "Sains"};
        for (String category : categories) {
            tabLayout.addTab(tabLayout.newTab().setText(category));
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String selectedCategory = tab.getText().toString();
                fetchNewsData(selectedCategory);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void fetchNewsData(String query) {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE); // Sembunyikan daftar saat memuat

        String url = ApiConfig.BASE_URL + ApiConfig.EVERYTHING_ENDPOINT +
                "?q=" + query +
                "&language=id" +
                "&apiKey=" + ApiConfig.API_KEY;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.getString("status").equals("ok")) {
                            JSONArray articlesArray = response.getJSONArray("articles");
                            articleList.clear();
                            for (int i = 0; i < articlesArray.length(); i++) {
                                JSONObject articleObject = articlesArray.getJSONObject(i);
                                String title = articleObject.getString("title");
                                String description = articleObject.optString("description", "");
                                String articleUrl = articleObject.getString("url");
                                String imageUrl = articleObject.optString("urlToImage", "");
                                String publishedAt = articleObject.getString("publishedAt");
                                JSONObject sourceObject = articleObject.getJSONObject("source");
                                String sourceName = sourceObject.getString("name");
                                Article article = new Article(title, description, articleUrl, imageUrl, publishedAt, sourceName);
                                articleList.add(article);
                            }
                            newsAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing JSON: " + e.getMessage());
                    } finally {
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                },
                error -> {
                    Log.e(TAG, "Error Volley: " + error.toString());
                    Toast.makeText(MainActivity.this, "Gagal memuat berita", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
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