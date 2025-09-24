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
    private String currentCategoryApi = "";
    private String currentQuery = "";

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

        ivSearch.setOnClickListener(v -> handleSearch());
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            handleSearch();
            return true;
        });
        swipeRefreshLayout.setOnRefreshListener(this::fetchNewsData);

        setupTabLayout();
    }

    private void handleSearch() {
        String query = etSearch.getText().toString().trim();
        if (query.isEmpty()) {
            Toast.makeText(this, "Silakan masukkan kata kunci", Toast.LENGTH_SHORT).show();
            return;
        }
        currentQuery = query;
        currentCategoryApi = "";
        fetchNewsData();
    }

    private void setupTabLayout() {
        String[] displayCategories = {"Global", "Bisnis", "Hiburan", "Kesehatan", "Sains", "Olahraga", "Teknologi"};
        String[] apiCategories = {"", "business", "entertainment", "health", "science", "sports", "technology"};

        for (String category : displayCategories) {
            tabLayout.addTab(tabLayout.newTab().setText(category));
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                currentCategoryApi = apiCategories[position];
                currentQuery = "";
                etSearch.setText("");
                fetchNewsData();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                fetchNewsData();
            }
        });

        tabLayout.getTabAt(0).select();
    }

    private void fetchNewsData() {
        if (!swipeRefreshLayout.isRefreshing()) {
            progressBar.setVisibility(View.VISIBLE);
        }
        recyclerView.setVisibility(View.GONE);
        tvNotFound.setVisibility(View.GONE);

        StringBuilder urlBuilder = new StringBuilder(ApiConfig.BASE_URL);

        boolean isCategorySearch = currentQuery.isEmpty() && !currentCategoryApi.isEmpty();

        if (!currentQuery.isEmpty()) {
            urlBuilder.append(ApiConfig.EVERYTHING_ENDPOINT)
                    .append("?q=").append(currentQuery)
                    .append("&language=id");
        } else {
            urlBuilder.append(ApiConfig.TOP_HEADLINES_ENDPOINT).append("?");
            if (currentCategoryApi.isEmpty()) {
                urlBuilder.append("language=en");
            } else {
                urlBuilder.append("country=id")
                        .append("&category=").append(currentCategoryApi);
            }
        }

        urlBuilder.append("&apiKey=").append(ApiConfig.API_KEY);
        String url = urlBuilder.toString();
        Log.d(TAG, "Primary Fetch URL: " + url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        articleList.clear();
                        parseAndAddArticles(response);

                        if (articleList.isEmpty() && isCategorySearch) {
                            Log.d(TAG, "No results for Indonesia, trying global fallback for category: " + currentCategoryApi);
                            fetchFallbackData();
                            return;
                        }

                        updateUiAfterFetch();

                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing JSON: " + e.getMessage());
                        updateUiAfterFetch();
                    }
                },
                error -> {
                    Log.e(TAG, "Error Volley: " + error.toString());
                    Toast.makeText(this, "Gagal memuat berita", Toast.LENGTH_SHORT).show();
                    updateUiAfterFetch();
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

    private void fetchFallbackData() {
        String fallbackUrl = ApiConfig.BASE_URL + ApiConfig.TOP_HEADLINES_ENDPOINT +
                "?category=" + currentCategoryApi +
                "&language=en" +
                "&apiKey=" + ApiConfig.API_KEY;
        Log.d(TAG, "Fallback Fetch URL: " + fallbackUrl);

        JsonObjectRequest fallbackRequest = new JsonObjectRequest(
                Request.Method.GET, fallbackUrl, null,
                response -> {
                    try {
                        articleList.clear();
                        parseAndAddArticles(response);
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing fallback JSON: " + e.getMessage());
                    } finally {
                        updateUiAfterFetch();
                    }
                },
                error -> {
                    Log.e(TAG, "Error Volley on fallback: " + error.toString());
                    updateUiAfterFetch();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("User-Agent", "Mozilla/5.0");
                return headers;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(fallbackRequest);
    }

    private void parseAndAddArticles(JSONObject response) throws JSONException {
        if (response.getString("status").equals("ok")) {
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
        }
    }

    private void updateUiAfterFetch() {
        if (articleList.isEmpty()) {
            tvNotFound.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvNotFound.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        newsAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
    }
}