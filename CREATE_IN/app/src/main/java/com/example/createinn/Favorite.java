package com.example.createinn;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Favorite extends AppCompatActivity {

    ImageButton sendInformation;
    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;

    Context context=this;
 /*Actividad  para compartir los datos obtenidos de la api*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        sendInformation=(ImageButton)findViewById(R.id.send_information);
        bottomNavigationView= findViewById(R.id.bottom_navigation);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Origin");
        }




        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            switch(itemId){
                case R.id.nav_camara:
                    startActivity(new Intent(Favorite.this, MainActivity.class));
                    return true;
                case R.id.nav_home:
                    startActivity(new Intent(Favorite.this, MainActivity.class));
                    return true;
                case R.id.nav_buscar:
                    startActivity(new Intent(Favorite.this, HandValidate.class));
                    return true;
            }
            return false;

        });
    }


    }
