package com.example.createinn;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity {

    private ZXingScannerView scannerView;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                if (result.getContents() != null) {
                    Intent intent = new Intent(MainActivity.this, CaptureContent.class);
                    intent.putExtra("barcode", result.getContents());
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Escaneo cancelado", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configurar Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Origin");
        }

        // Configurar Scanner
        scannerView = findViewById(R.id.scanner_view);
        scannerView.setAutoFocus(true);

        // Configurar Bottom Navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(MainActivity.this, HandValidate.class)); // esto falta por arreglar
            } else if (itemId == R.id.nav_buscar) {
                startActivity(new Intent(MainActivity.this, ValidateCode.class));
            } else if (itemId == R.id.nav_camara) {
                startActivity(new Intent(MainActivity.this, CaptureContent.class));
            }
            return true;
        });
    }
    /*implementar lectura de codigo de barras  */

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }
}