package com.example.createinn;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class UnknownProduct extends AppCompatActivity {

    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unknown_product);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Origin");
        }


        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            switch(itemId){
                case R.id.nav_camara:
                    startActivity(new Intent(UnknownProduct.this, CaptureContent.class));
                    break;
                case R.id.nav_home:
                    startActivity(new Intent(UnknownProduct.this, CaptureContent.class));
                    break;
                case R.id.nav_buscar:
                    startActivity(new Intent(UnknownProduct.this, HandValidate.class));
                    break;
            }
            return true;

        });
    }



}
