package com.example.aplikasi_berita.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
    private NewsAdapter newsAdapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvNotFound;
    private EditText etSearch;
    private ImageView ivSearch;
    private TabLayout tabLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String currentCategory = "Indonesia";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        progressBar = findViewById(R.id.progress_bar);
        recyclerView = findViewById(R.id.rv_berita);
        tvNotFound = findViewById(R.id.tv_not_found);
        etSearch = findViewById(R.id.et_search);
        ivSearch = findViewById(R.id.iv_search);
        tabLayout = findViewById(R.id.tab_layout);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsAdapter = new NewsAdapter(articleList);
        recyclerView.setAdapter(newsAdapter);

        setupTabLayout();
        fetchNewsData(currentCategory);

        ivSearch.setOnClickListener(v -> handleSearch());
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            handleSearch();
            return true;
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            fetchNewsData(currentCategory);
        });
    }

    private void handleSearch() {
        String query = etSearch.getText().toString().trim();
        if (query.isEmpty()) {
            Toast.makeText(this, "Silakan masukkan kata kunci", Toast.LENGTH_SHORT).show();
            return;
        }

        String finalQuery = query;
        if (!currentCategory.equals("Indonesia")) {
            finalQuery = query + " AND " + currentCategory;
        }
        fetchNewsData(finalQuery);
    }

    private void setupTabLayout() {
        String[] categories = {"Semua", "Teknologi", "Bisnis", "Olahraga", "Kesehatan", "Hiburan", "Sains"};
        for (String category : categories) {
            tabLayout.addTab(tabLayout.newTab().setText(category));
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String selectedCategory = tab.getText().toString();
                if (selectedCategory.equals("Semua")) {
                    currentCategory = "Indonesia";
                } else {
                    currentCategory = selectedCategory;
                }
                etSearch.setText("");
                fetchNewsData(currentCategory);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void fetchNewsData(String query) {
        // Hanya tampilkan ProgressBar jika bukan dari aksi swipe
        if (!swipeRefreshLayout.isRefreshing()) {
            progressBar.setVisibility(View.VISIBLE);
        }
        recyclerView.setVisibility(View.GONE);
        tvNotFound.setVisibility(View.GONE);

        String url = ApiConfig.BASE_URL + ApiConfig.EVERYTHING_ENDPOINT +
                "?q=" + query + "&language=id&apiKey=" + ApiConfig.API_KEY;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        articleList.clear();
                        JSONArray articlesArray = response.getJSONArray("articles");
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

                        if (articleList.isEmpty()) {
                            tvNotFound.setVisibility(View.VISIBLE);
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                        newsAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing JSON: " + e.getMessage());
                        tvNotFound.setVisibility(View.VISIBLE);
                    } finally {
                        progressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },
                error -> {
                    Log.e(TAG, "Error Volley: " + error.toString());
                    Toast.makeText(this, "Gagal memuat berita", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    tvNotFound.setVisibility(View.VISIBLE);
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("User-Agent", "Mozilla/s.0");
                return headers;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
}