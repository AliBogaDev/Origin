package com.example.createinn;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
public class CamaraMain extends AppCompatActivity {
    FloatingActionButton main_button, capture;
    EditText result;
    TextView label_name, name, factured, country;
    Context context = this;
    ImageView image, barcodeImage;
    String infoProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camara_main);

        // Inicializar vistas
        main_button = findViewById(R.id.button_to_go_hand);
        capture = findViewById(R.id.capture_Image);
        result = findViewById(R.id.result);
        name = findViewById(R.id.name_product);
        factured = findViewById(R.id.factured_place);
        country = findViewById(R.id.country);
        image = findViewById(R.id.image);
        label_name = findViewById(R.id.label_name);
        barcodeImage = findViewById(R.id.barcode_image);

        // Botón para ir a MainActivity
        main_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CamaraMain.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Botón para abrir cámara y escanear
        capture.setOnClickListener(v -> {
            // 2. Launch the scanner using the launcher
            ScanOptions options = new ScanOptions();
            options.setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES);
            options.setPrompt("Ponga el código en la ventana.");
            options.setCameraId(0);
            options.setOrientationLocked(false);
            options.setBeepEnabled(true);
            options.setCaptureActivity(CaptureActivityPosition.class); // para poner la camara en vertical
            activityResultImage.launch(options);

        });
    }

    // 1. Define el ActivityResultLauncher
    private final ActivityResultLauncher<ScanOptions> activityResultImage = registerForActivityResult(new ScanContract(),
            scanResult -> {
                if (scanResult.getContents() == null) {
                    Toast.makeText(this, "Lectura cancelada", Toast.LENGTH_SHORT).show();
                } else {
                    String barcode = scanResult.getContents();
                    result.setText(barcode);
                    Toast.makeText(this, "Código escaneado: " + barcode, Toast.LENGTH_SHORT).show();

                    // Generar imagen del código de barras
                    generateBarcodeImage(barcode);

                    // Consultar API de Open Food Facts
                    queryProductInfo(barcode);
                }
            });
    private void generateBarcodeImage(String barcodeText) {
        try {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            BitMatrix bitMatrix = multiFormatWriter.encode(
                    barcodeText,
                    BarcodeFormat.CODE_128,
                    600,
                    200,
                    java.util.Collections.singletonMap(
                            com.google.zxing.EncodeHintType.MARGIN, 1
                    )
            );

            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            barcodeImage.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al generar código de barras", Toast.LENGTH_SHORT).show();
        }
    }

    private void queryProductInfo(String barcode) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://world.openfoodfacts.org/api/v0/product/" + barcode + ".json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();
                    Log.d("JSON Response", myResponse);

                    runOnUiThread(() -> {
                        try {
                            Gson gson = new Gson();
                            JsonObject json = gson.fromJson(myResponse, JsonObject.class);

                            if (json.get("status").getAsInt() == 0) {
                                Intent intent = new Intent(CamaraMain.this, UnknownProduct.class);
                                startActivity(intent);
                            } else {
                                JsonObject product = json.getAsJsonObject("product");

                                // Mostrar información del producto
                                String names = product.get("brands").getAsString();
                                label_name.setText(names);

                                String productName = product.get("product_name").getAsString();
                                name.setText(productName);

                                String manufacturingPlaces = product.get("manufacturing_places").getAsString();
                                factured.setText(manufacturingPlaces);

                                String countries = product.get("countries").getAsString();
                                country.setText(countries);

                                // Mostrar imagen del producto
                                String imageFrontUrl = product.get("image_thumb_url").getAsString();
                                Glide.with(context)
                                        .load(imageFrontUrl)
                                        .placeholder(R.drawable.camara)
                                        .into(image);

                                // Guardar información para compartir
                                infoProduct = "Etiqueta: " + names + "\n" +
                                        "Nombre: " + productName + "\n" +
                                        "Producido: " + manufacturingPlaces + "\n" +
                                        "País: " + countries;

                                SharedPreferences sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("infoProduct", infoProduct);
                                editor.apply();
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