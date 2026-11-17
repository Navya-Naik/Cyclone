package com.example.otp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etMobileNumber;
    private TextInputLayout tilMobile;
    private MaterialButton btnSendOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tilMobile), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, 0, 0, 0);
            return insets;
        });

        etMobileNumber = findViewById(R.id.etMobileNumber);
        tilMobile = findViewById(R.id.tilMobile);
        btnSendOtp = findViewById(R.id.btnSendOtp);

        btnSendOtp.setOnClickListener(v -> {
            String mobile = etMobileNumber.getText() != null ? etMobileNumber.getText().toString().trim() : "";
            if (TextUtils.isEmpty(mobile) || mobile.length() < 10) {
                tilMobile.setError("Please enter a valid 10-digit mobile number");
                return;
            }

            tilMobile.setError(null);
            Toast.makeText(LoginActivity.this, "OTP sent to +91 " + mobile, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(LoginActivity.this, OtpActivity.class);
            startActivity(intent);
            finish();
        });
    }
}