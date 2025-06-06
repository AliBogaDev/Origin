package com.example.createinn;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.createinn.data.AppDatabase;
import com.example.createinn.data.ProductFavoriteDao;
import com.example.createinn.model.FavoritProduct;
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

    private static final String TAG = "CaptureContent";
    private static final String SHARED_PREFS_NAME = "MyPrefs";
    private static final String SHARE_URL_KEY = "shareUrl";

    private EditText result;
    private TextView label_name, name, factured, country;
    private ImageView image, barcodeImage;
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private Button guardar;
    private Context context = this;
    private String jsonResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capture_content);

        initializeViews();
        setupToolbar();
        setupBottomNavigation();
        setupBackPressedCallback();

        String barcode = getIntent().getStringExtra("barcode");
        if (barcode != null && !barcode.isEmpty()) {
            result.setText(barcode);
            generateBarcodeImage(barcode);
            queryProductInfo(barcode);
        } else {
            Toast.makeText(this, "No se recibió ningún código", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initializeViews() {
        result = findViewById(R.id.result);
        label_name = findViewById(R.id.label_name);
        name = findViewById(R.id.name_product);
        factured = findViewById(R.id.factured_place);
        country = findViewById(R.id.country);
        image = findViewById(R.id.image);
        barcodeImage = findViewById(R.id.barcode_image);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        toolbar = findViewById(R.id.toolbar);
        guardar = findViewById(R.id.guardar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Origin");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_favoritos) {
                startActivity(new Intent(CaptureContent.this, Favorite.class));
                return true;
            } else if (itemId == R.id.nav_buscar) {
                startActivity(new Intent(CaptureContent.this, HandValidate.class));
                return true;
            } else if (itemId == R.id.nav_camara) {
                startActivity(new Intent(CaptureContent.this, MainActivity.class));
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.camera) {
            startActivity(new Intent(CaptureContent.this, MainActivity.class));
            return true;
        } else if (itemId == R.id.share) {
            shareProduct();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareProduct() {
        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        String shareUrl = sharedPref.getString(SHARE_URL_KEY, "https://es.openfoodfacts.org/");

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Consulta este producto");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareUrl);

        Intent chooser = Intent.createChooser(shareIntent, "Compartir con");
        if (shareIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(chooser);
        } else {
            Toast.makeText(this, "No hay aplicaciones para compartir.", Toast.LENGTH_SHORT).show();
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
            Log.e(TAG, "Error al generar código de barras", e);
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
                Log.e(TAG, "Error de conexión", e);
                runOnUiThread(() ->
                        Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
                );
                finish();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() ->
                            Toast.makeText(context, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show()
                    );
                    finish();
                    return;
                }

                jsonResponse = response.body().string();
                Log.d(TAG, "JSON Response: " + jsonResponse);

                runOnUiThread(() -> processProductResponse(jsonResponse));
            }
        });
    }

    private void processProductResponse(String jsonResponse) {
        try {
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(jsonResponse, JsonObject.class);

            if (json.get("status").getAsInt() == 0) {
                startActivity(new Intent(CaptureContent.this, UnknownProduct.class));
                finish();
                return;
            }

            JsonObject product = json.getAsJsonObject("product");
            displayProductInfo(product);
            setupSaveButton(product);

        } catch (Exception e) {
            Log.e(TAG, "Error al procesar los datos", e);
            Toast.makeText(context, "Error al procesar los datos", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void displayProductInfo(JsonObject product) {
        String brand = product.has("brands") ? product.get("brands").getAsString() : "Desconocido";
        String productName = product.has("product_name") ? product.get("product_name").getAsString() : "producto-desconocido";
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
                    .error(R.drawable.camara)
                    .into(image);
        }

        // Generar slug del nombre
        String slugName = productName.toLowerCase()
                .replace(" ", "-")
                .replaceAll("[^a-z0-9\\-]", "");

        // Crear y guardar enlace compartible
        String shareUrl = "https://es.openfoodfacts.org/producto/" + result.getText().toString() + "/" + slugName;
        saveShareUrl(shareUrl);
    }

    private void saveShareUrl(String shareUrl) {
        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        sharedPref.edit().putString(SHARE_URL_KEY, shareUrl).apply();
    }

    private void setupSaveButton(JsonObject product) {
        guardar.setOnClickListener(v -> {
            String imageUrl = product.has("image_thumb_url") ? product.get("image_thumb_url").getAsString() : "";


            FavoritProduct favoritProduct = new FavoritProduct(
                    0,
                    label_name.getText().toString(),
                    name.getText().toString(),
                    factured.getText().toString(),
                    imageUrl,
                    country.getText().toString()
            );

            saveFavoriteProduct(favoritProduct);
        });
    }

    private void saveFavoriteProduct(FavoritProduct favoritProduct) {
        AppDatabase db = AppDatabase.getInstance(CaptureContent.this);
        ProductFavoriteDao dao = db.productFavoritDao();

        new Thread(() -> {
            boolean exists = dao.checkIfExists(favoritProduct.getMarca(), favoritProduct.getNombre()) > 0;

            runOnUiThread(() -> {
                if (!exists) {
                    new Thread(() -> {
                        dao.insert(favoritProduct);
                        runOnUiThread(() ->
                                Toast.makeText(CaptureContent.this, "✅ Guardado", Toast.LENGTH_SHORT).show()
                        );
                    }).start();
                } else {
                    Toast.makeText(CaptureContent.this, "⚠️ Ya existe", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void setupBackPressedCallback() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateToMainActivity();
            }
        });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(CaptureContent.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
