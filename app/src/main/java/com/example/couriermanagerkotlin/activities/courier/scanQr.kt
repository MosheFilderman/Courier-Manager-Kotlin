package com.example.couriermanagerkotlin.activities.courier

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.couriermanagerkotlin.R
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

class scanQr : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Start the QR code scanner
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Scan a QR Code")
        integrator.setCameraId(0)  // Use the back camera
        integrator.setBeepEnabled(true) // Beep when a QR code is scanned
        integrator.initiateScan()
    }

    // Handle the result of the scan
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result: IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result.contents != null) {
            val scannedText = result.contents
            Toast.makeText(this@scanQr,scannedText,Toast.LENGTH_LONG).show()
        } else {
            // Handle the case where no QR code was scanned
        }
    }

}