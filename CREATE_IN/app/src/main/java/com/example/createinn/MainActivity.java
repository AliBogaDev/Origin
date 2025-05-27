package com.example.createinn;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class MainActivity extends AppCompatActivity {

    // ActivityResultLauncher que maneja el escaneo
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                if (result.getContents() != null) {
                    // Código escaneado exitosamente, redirigir a CaptureContent
                    Intent intent = new Intent(MainActivity.this, CaptureContent.class);
                    intent.putExtra("barcode", result.getContents());
                    startActivity(intent);
                    finish(); // cerrar MainActivity si no la necesitas después
                } else {
                    Toast.makeText(this, "Escaneo cancelado", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inmediatamente lanza el escáner al iniciar
        openScanner();
    }

    private void openScanner() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES);
        options.setPrompt("Escanea el código de barras");
        options.setCameraId(0);
        options.setOrientationLocked(false);
        options.setBeepEnabled(true);
        options.setCaptureActivity(CaptureActivityPosition.class); // si tienes una actividad personalizada
        barcodeLauncher.launch(options);
    }
}
