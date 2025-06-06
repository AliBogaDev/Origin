package com.example.createinn;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HandValidate extends AppCompatActivity {

    Context context = this;
    Button handValidate;
    EditText code;
    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hand_validate);
        code = findViewById(R.id.rellenable);
        handValidate = findViewById(R.id.button_validate);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        //control para el retroceso
        setupBackPressedCallback();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Flecha de retroceso
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        // Botón para validar código
        handValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String takeCode = code.getText().toString().trim();

                if (!takeCode.isEmpty()) {
                    checkProductExists(takeCode); // Verificar en API
                } else {
                    goToUnknownProduct();
                }
            }
        });

        // Bottom navigation
        bottomNavigationView.setSelectedItemId(R.id.nav_camara);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_favoritos:
                    startActivity(new Intent(HandValidate.this, Favorite.class));
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
//llamada a la api para comprobar existencia de producto
    private void checkProductExists(String barcode) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://world.openfoodfacts.org/api/v0/product/" + barcode + ".json";

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show();
                    goToUnknownProduct();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    JsonObject json = new Gson().fromJson(jsonResponse, JsonObject.class);

                    runOnUiThread(() -> {
                        if (json.get("status").getAsInt() == 0) {
                            goToUnknownProduct();
                        } else {
                            Intent intent = new Intent(HandValidate.this, CaptureContent.class);
                            intent.putExtra("barcode", barcode);
                            startActivity(intent);
                        }
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(context, "Error en la respuesta", Toast.LENGTH_SHORT).show();
                        goToUnknownProduct();
                    });
                }
            }
        });
    }

    private void goToUnknownProduct() {
        Toast.makeText(this, "Producto no encontrado", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, UnknownProduct.class));
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
        Intent intent = new Intent(HandValidate.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}