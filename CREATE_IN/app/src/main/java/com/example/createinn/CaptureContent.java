package com.example.createinn;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CaptureContent extends AppCompatActivity {

    EditText result;
    TextView label_name, name, factured, country;
    ImageView image, barcodeImage;
    BottomNavigationView bottomNavigationView;
    Toolbar toolbar;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capture_content);

        // Inicializar vistas
        result = findViewById(R.id.result);
        label_name = findViewById(R.id.label_name);
        name = findViewById(R.id.name_product);
        factured = findViewById(R.id.factured_place);
        country = findViewById(R.id.country);
        image = findViewById(R.id.image);
        barcodeImage = findViewById(R.id.barcode_image);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        toolbar = findViewById(R.id.toolbar);

        // Toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Origin");
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        // Bottom navigation
        bottomNavigationView.setSelectedItemId(R.id.nav_camara);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    startActivity(new Intent(CaptureContent.this, MainActivity.class));
                    return true;
                case R.id.nav_buscar:
                    startActivity(new Intent(CaptureContent.this, HandValidate.class));
                    return true;
                case R.id.nav_camara:
                    startActivity(new Intent(CaptureContent.this, MainActivity.class));
                    // Ya estás en esta actividad, no hacer nada
                    return true;
                default:
                    return false;
            }
        });

        // Obtener el código de barras enviado desde MainActivity
        String barcode = getIntent().getStringExtra("barcode");

        if (barcode != null && !barcode.isEmpty()) {
            result.setText(barcode); // Mostrar código
            generateBarcodeImage(barcode); // Generar imagen
            queryProductInfo(barcode); // Consultar API
        } else {
            Toast.makeText(this, "No se recibió ningún código", Toast.LENGTH_SHORT).show();
        }
    }

    private void generateBarcodeImage(String barcodeText) {
        try {
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix matrix = writer.encode(barcodeText, BarcodeFormat.CODE_128, 600, 200);
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(matrix);
            barcodeImage.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al generar la imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private void queryProductInfo(String barcode) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://world.openfoodfacts.org/api/v0/product/" + barcode + ".json";

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String jsonResponse = response.body().string();
                    Log.d("JSON Response", jsonResponse);

                    runOnUiThread(() -> {
                        try {
                            Gson gson = new Gson();
                            JsonObject json = gson.fromJson(jsonResponse, JsonObject.class);

                            if (json.get("status").getAsInt() == 0) {
                                startActivity(new Intent(CaptureContent.this, UnknownProduct.class));
                            } else {
                                JsonObject product = json.getAsJsonObject("product");

                                String brand = product.has("brands") ? product.get("brands").getAsString() : "Desconocido";
                                String productName = product.has("product_name") ? product.get("product_name").getAsString() : "Desconocido";
                                String manufacture = product.has("manufacturing_places") ? product.get("manufacturing_places").getAsString() : "Desconocido";
                                String countries = product.has("countries") ? product.get("countries").getAsString() : "Desconocido";
                                String imageUrl = product.has("image_thumb_url") ? product.get("image_thumb_url").getAsString() : "";

                                label_name.setText(brand);
                                name.setText(productName);
                                factured.setText(manufacture);
                                country.setText(countries);

                                if (!imageUrl.isEmpty()) {
                                    Glide.with(context)
                                            .load(imageUrl)
                                            .placeholder(R.drawable.camara)
                                            .into(image);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Error al procesar los datos", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
