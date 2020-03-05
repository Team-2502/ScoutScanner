package com.team2502.ScoutScanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ScanQRActivity extends AppCompatActivity implements QRCodeReaderView.OnQRCodeReadListener {

    QRCodeReaderView qrCodeReader;
    public String scannedTIMD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);

        qrCodeReader = findViewById(R.id.scan_area);
        qrCodeReader.setOnQRCodeReadListener(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 300);
        }

        initCam();
    }

    //Initiates the camera
    public void initCam() throws NullPointerException {
        qrCodeReader.setQRDecodingEnabled(true);
        qrCodeReader.setTorchEnabled(true);
        qrCodeReader.setAutofocusInterval(3000L);
        qrCodeReader.setBackCamera();
    }

    public boolean checkScan(String scannedTIMD){
        return scannedTIMD.length() >= 10 && scannedTIMD.indexOf('|') != -1;
    }

    public String getTIMDName(String scannedTIMD){
        String TIMDName = "QM" + scannedTIMD.substring(1, scannedTIMD.indexOf('B')) + "-";
        TIMDName += scannedTIMD.substring(scannedTIMD.indexOf('B') + 1, scannedTIMD.indexOf('C')) + "-";
        TIMDName += scannedTIMD.substring(scannedTIMD.indexOf('D') + 1, scannedTIMD.indexOf('E'));
        return TIMDName;
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        qrCodeReader.stopCamera();
        scannedTIMD = text;
        Log.e("TIMD", scannedTIMD);

        Toast toast = Toast.makeText(getApplicationContext(), scannedTIMD, Toast.LENGTH_SHORT);
        toast.show();

        // TIMD is probably valid
        if(checkScan(scannedTIMD)){
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference();

            myRef.child("rawTIMDs").child(getTIMDName(scannedTIMD)).setValue(scannedTIMD);

            //Toast toast = Toast.makeText(getApplicationContext(), "Scanned Successfully!", Toast.LENGTH_SHORT);
            //toast.show();
        }
        else{
            //Toast toast = Toast.makeText(getApplicationContext(), "Scan Failed! Try Again.", Toast.LENGTH_SHORT);
            //toast.show();
        }

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        qrCodeReader.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        qrCodeReader.stopCamera();
    }

    public void goBack(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
