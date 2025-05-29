package com.example.createinn;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Contacts extends AppCompatActivity {

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


        SharedPreferences sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        final String infoProduct = sharedPref.getString("infoProduct", "");

        sendInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_SUBJECT,"Información importante!.");
                intent.putExtra(Intent.EXTRA_TEXT ,"Mi web: <a href='https://es.openfoodfacts.org/' >Leer</a>"+infoProduct);
                intent.setPackage("com.whatsapp");
                startActivity(intent);

            }
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            switch(itemId){
                case R.id.nav_camara:
                    startActivity(new Intent(Contacts.this, MainActivity.class));
                    return true;
                case R.id.nav_home:
                    startActivity(new Intent(Contacts.this, MainActivity.class));
                    return true;
                case R.id.nav_buscar:
                    startActivity(new Intent(Contacts.this, HandValidate.class));
                    return true;
            }
            return false;

        });
    }


    }
