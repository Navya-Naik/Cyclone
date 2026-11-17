package com.example.otp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Direct launch into OTP Verification Page for CycloneSafe
        Intent intent = new Intent(this, OtpActivity.class);
        startActivity(intent);
        finish();
    }
}