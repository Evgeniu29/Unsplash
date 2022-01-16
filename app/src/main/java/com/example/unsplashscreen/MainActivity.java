package com.example.unsplashscreen;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unsplashscreen.api.ApiUtilities;
import com.example.unsplashscreen.model.ImageModel;
import com.example.unsplashscreen.model.SearchModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private ArrayList<ImageModel> list;

    private GridLayoutManager manager;

    private ImageAdapter adapter;

    private ProgressDialog dialog;

    private int pageSize = 30;

    private boolean isLoading;

    private boolean isLastPage;

    private int page = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);

        list = new ArrayList<>();

        adapter = new ImageAdapter(this, list);

        manager = new GridLayoutManager(this, 4);

        recyclerView.setLayoutManager(manager);

        recyclerView.setAdapter(adapter);


        dialog = new ProgressDialog(this);

        dialog.setMessage("Loading...");

        dialog.setCancelable(false);

        dialog.show();

        getData();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItem = manager.getChildCount();
                int totalItem = manager.getItemCount();
                int firstVisibleItemPos = manager.findFirstVisibleItemPosition();

                if (!isLoading && !isLastPage){
                    if ((visibleItem+firstVisibleItemPos >= totalItem)
                            && firstVisibleItemPos >= 0 && totalItem >= pageSize){
                        page++;
                        getData();
                    }
                }
            }
        });

    }

    public  void getData() {

        isLoading = true;

        ApiUtilities.getApiInterface().getImages(page, pageSize).enqueue(new Callback<List<ImageModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<ImageModel>> call, @NonNull Response<List<ImageModel>> response) {
                if (response.body() != null) {
                    list.addAll(response.body());


                    adapter.notifyDataSetChanged();

                    dialog.dismiss();

                }

                isLoading = false;

                dialog.dismiss();

                if (list.size() > 0) {
                    isLastPage = list.size() < pageSize;
                } else isLastPage = true;


            }

            @Override
            public void onFailure(Call<List<ImageModel>> call, Throwable t) {

                dialog.dismiss();

                Toast.makeText(MainActivity.this, "Error" + t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                dialog.show();
                searchData(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    private void searchData(String query) {

        ApiUtilities.getApiInterface().searchImages(query)
                .enqueue(new Callback<SearchModel>() {
                    @Override
                    public void onResponse(Call<SearchModel> call, Response<SearchModel> response) {
                        dialog.dismiss();
                        list.clear();
                        list.addAll(response.body().getResults());
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<SearchModel> call, Throwable t) {

                    }
                });
    }
}