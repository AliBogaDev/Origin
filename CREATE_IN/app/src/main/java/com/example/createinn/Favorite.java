package com.example.createinn;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.createinn.adapter.FavoriteAdapter;
import com.example.createinn.data.AppDatabase;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Favorite extends AppCompatActivity {

    ImageButton sendInformation;
    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;

    private FavoriteAdapter adapter;
    private AppDatabase db;

    Context context=this;
 /*Actividad  para compartir los datos obtenidos de la api*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

    //    sendInformation=(ImageButton)findViewById(R.id.send_information);
        bottomNavigationView= findViewById(R.id.bottom_navigation);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Origin");
        }

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = AppDatabase.getInstance(this);


        db.productFavoritDao().getAllFavorites().observe(this, favorites -> {
            adapter = new FavoriteAdapter(favorites, product -> {
                // Eliminar producto
                new Thread(() -> {
                    db.productFavoritDao().delete(product);
                }).start();
            });
            recyclerView.setAdapter(adapter);
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            switch(itemId){
                case R.id.nav_camara:
                    startActivity(new Intent(Favorite.this, MainActivity.class));
                    return true;
                case R.id.nav_favoritos:
                    startActivity(new Intent(Favorite.this, Favorite.class));
                    return true;
                case R.id.nav_buscar:
                    startActivity(new Intent(Favorite.this, HandValidate.class));
                    return true;
            }
            return false;

        });
    }

    // --- Método  para manejar el retroceso ---
    private void setupBackPressedCallback() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Lógica personalizada aquí
                navigateToMainActivity();
            }
        });
    }

    // --- Método para redirigir a MainActivity, reescribiendo el calback ---
    private void navigateToMainActivity() {
        Intent intent = new Intent(Favorite.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}



