package com.example.createinn;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.widget.Toolbar;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class HandValidate extends AppCompatActivity {

    Context context=this;
    Button handValidate;
    EditText code;
    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hand_validate);
        code= findViewById(R.id.rellenable);
        handValidate=findViewById(R.id.button_validate);
        bottomNavigationView=findViewById(R.id.bottom_navigation);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
     /*   if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Origin");
        }*/


        /*Flecha de retroceso*/

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        /*boton para saltar  a validar codigo*/
        handValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String takeCode = code.getText().toString();
                Intent intent = new Intent(HandValidate.this,ValidateCode.class);
                intent.putExtra("codigo",takeCode);
                startActivity(intent);

            }
        });

        // Bottom navigation
        bottomNavigationView.setSelectedItemId(R.id.nav_camara);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    startActivity(new Intent(HandValidate.this, MainActivity.class));
                    return true;
                case R.id.nav_buscar:
                    startActivity(new Intent(HandValidate.this, HandValidate.class));
                    return true;
                case R.id.nav_camara:
                    startActivity(new Intent(HandValidate.this, MainActivity.class));
                    return true;
                default:
                    return false;
            }
        });



    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

/*Los menus se visualizan a la parte izquierda para compartir y visualizar la camara*/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.camera:
                Intent camera = new Intent(HandValidate.this, CaptureContent.class);
                startActivity(camera);
                break;

            case R.id.share:
                Intent share = new Intent(HandValidate.this, Contacts.class);
                startActivity(share);
                break;

        }
        return super.onOptionsItemSelected(item);

    }



    }




