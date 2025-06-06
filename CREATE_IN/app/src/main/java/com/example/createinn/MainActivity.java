package com.example.createinn;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import android.media.AudioManager;
import android.media.ToneGenerator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DecoratedBarcodeView barcodeScannerView;
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private boolean isScanningPaused = false;

    private final BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null && !isScanningPaused) {
                pauseScanning();

                //sonido
                ToneGenerator toneGen = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 150);
                //lo que envio
                String scannedCode = result.getText();
                Log.d("MainActivity", "Código escaneado: " + scannedCode);
                Toast.makeText(MainActivity.this, "Escaneado: " + scannedCode, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, CaptureContent.class);
                intent.putExtra("barcode", scannedCode);
                startActivity(intent);
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
            // Puntos opcionales
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        barcodeScannerView = findViewById(R.id.barcode_scanner_view);

        // Solicitar permisos si es necesario
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
        } else {
            startBarcodeScanner();
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_camara) {
                resumeScanning();
                return true;
            } else if (itemId == R.id.nav_favoritos) {
                pauseScanning();
                startActivity(new Intent(MainActivity.this, Favorite.class));
                return true;
            } else if (itemId == R.id.nav_buscar) {
                pauseScanning();
                startActivity(new Intent(MainActivity.this, HandValidate.class));
                return true;
            }
            return false;
        });

        bottomNavigationView.setSelectedItemId(R.id.nav_camara);
    }

    private void startBarcodeScanner() {
        barcodeScannerView.decodeContinuous(callback);
        barcodeScannerView.resume();
        isScanningPaused = false;
    }

    private void resumeScanning() {
        if (barcodeScannerView != null && !barcodeScannerView.getBarcodeView().isPreviewActive()) {
            barcodeScannerView.resume();
            isScanningPaused = false;
            Log.d("MainActivity", "Escaneo Reanudado");
        }
    }

    private void pauseScanning() {
        if (barcodeScannerView != null && barcodeScannerView.getBarcodeView().isPreviewActive()) {
            barcodeScannerView.pause();
            isScanningPaused = true;
            Log.d("MainActivity", "Escaneo Pausado");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED &&
                bottomNavigationView.getSelectedItemId() == R.id.nav_camara) {
            resumeScanning();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseScanning();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startBarcodeScanner();
        } else {
            Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_LONG).show();
        }
    }
}
