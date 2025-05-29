package com.example.createinn;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.ResultPoint;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DecoratedBarcodeView barcodeScannerView;
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private boolean isScanningPaused = false; // Para controlar si el escaneo está pausado

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null && !isScanningPaused) {
                // Pausar el escaneo para evitar múltiples detecciones y antes de navegar
                pauseScanning();

                String scannedCode = result.getText();
                Log.d("MainActivity", "Código escaneado: " + scannedCode);
                Toast.makeText(MainActivity.this, "Escaneado: " + scannedCode, Toast.LENGTH_SHORT).show();

                // Enviar a CaptureContentActivity
                Intent intent = new Intent(MainActivity.this, CaptureContent.class); // Envio el codigo leido a CaptureContent.class
                intent.putExtra("barcode", scannedCode);
                startActivity(intent);
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
            // Opcional: para dibujar puntos sobre posibles códigos
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // El título se puede establecer en el XML o aquí:
         getSupportActionBar().setTitle("Origin");

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        barcodeScannerView=findViewById(R.id.barcode_scanner_view);
        // Configurar el escáner
        barcodeScannerView.decodeContinuous(callback); // Para escanear continuamente

        // Configurar BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_camara) {
                // Si el usuario está en esta pantalla (MainActivity es la de escaneo)
                // y el escaneo estaba pausado, lo reanudamos.
                resumeScanning();
                return true;
            } else if (itemId == R.id.nav_home) {
                pauseScanning(); // Pausar el escaneo antes de ir a otra actividad
                startActivity(new Intent(MainActivity.this, MainActivity.class)); // Reemplaza con tu actividad
                return true;
            } else if (itemId == R.id.nav_buscar) {
                pauseScanning(); // Pausar el escaneo
                startActivity(new Intent(MainActivity.this, HandValidate.class)); // Reemplaza con tu actividad
                return true;
            }
            return false;
        });

        // Seleccionar el ítem de cámara por defecto al iniciar
        // Esto también asegura que si se vuelve de otra actividad, la cámara se active si es necesario
        bottomNavigationView.setSelectedItemId(R.id.nav_camara);
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
        // Solo reanudar si el ítem de cámara está seleccionado o si es la primera vez
        // y no está ya pausado por una navegación previa.
        if (bottomNavigationView.getSelectedItemId() == R.id.nav_camara) {
            resumeScanning();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseScanning();
    }

    // manejar los resultados de la solicitud de permisos

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Delegar al barcodeScannerView (o a un CaptureManager si lo usaras)
        // Esto es manejado internamente por la librería al llamar a resume() si los permisos no están.


    }}