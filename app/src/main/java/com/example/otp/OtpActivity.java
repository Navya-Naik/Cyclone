package com.example.otp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class OtpActivity extends AppCompatActivity {

    private EditText[] otpDigits;
    private TextView tvErrorText;
    private TextView tvStatusBanner;
    private TextView tvResendTimer;
    private TextView btnResendOtpCard;
    private TextView btnResendOtpBottom;
    private LinearLayout btnBackToLogin;
    private ImageView btnBackHeader;
    private MaterialButton btnVerifyOtp;
    private ProgressBar progressBarVerify;

    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;
    private static final long TIMER_DURATION_MS = 30000; // 30 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otp);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupOtpInputHandling();
        setupListeners();
        startResendTimer(TIMER_DURATION_MS);
    }

    private void initViews() {
        otpDigits = new EditText[]{
                findViewById(R.id.otpDigit1),
                findViewById(R.id.otpDigit2),
                findViewById(R.id.otpDigit3),
                findViewById(R.id.otpDigit4),
                findViewById(R.id.otpDigit5),
                findViewById(R.id.otpDigit6)
        };

        tvErrorText = findViewById(R.id.tvErrorText);
        tvStatusBanner = findViewById(R.id.tvStatusBanner);
        tvResendTimer = findViewById(R.id.tvResendTimer);
        btnResendOtpCard = findViewById(R.id.btnResendOtpCard);
        btnResendOtpBottom = findViewById(R.id.btnResendOtpBottom);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);
        btnBackHeader = findViewById(R.id.btnBackHeader);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        progressBarVerify = findViewById(R.id.progressBarVerify);
    }

    private void setupOtpInputHandling() {
        for (int i = 0; i < otpDigits.length; i++) {
            final int index = i;

            otpDigits[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    clearErrorState();
                    if (s.length() == 1) {
                        otpDigits[index].setBackgroundResource(R.drawable.bg_otp_box_filled);
                        if (index < otpDigits.length - 1) {
                            otpDigits[index + 1].requestFocus();
                        } else {
                            hideKeyboard(otpDigits[index]);
                        }
                    } else if (s.length() == 0) {
                        otpDigits[index].setBackgroundResource(R.drawable.bg_otp_box_selector);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            otpDigits[i].setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                        if (otpDigits[index].getText().toString().isEmpty() && index > 0) {
                            otpDigits[index - 1].requestFocus();
                            otpDigits[index - 1].setText("");
                            return true;
                        }
                    }
                    return false;
                }
            });
        }
    }

    private void setupListeners() {
        btnVerifyOtp.setOnClickListener(v -> verifyOtp());

        View.OnClickListener resendClickListener = v -> handleResendOtp();
        btnResendOtpCard.setOnClickListener(resendClickListener);
        btnResendOtpBottom.setOnClickListener(resendClickListener);

        View.OnClickListener backClickListener = v -> navigateToLogin();
        btnBackToLogin.setOnClickListener(backClickListener);
        btnBackHeader.setOnClickListener(backClickListener);
    }

    private void verifyOtp() {
        String enteredOtp = getEnteredOtp();

        if (enteredOtp.length() < 6) {
            showError(getString(R.string.error_incomplete_otp));
            highlightEmptyBoxes();
            return;
        }

        // Show loading state
        setLoadingState(true);

        // Simulate network API call
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            setLoadingState(false);
            Toast.makeText(OtpActivity.this, R.string.verification_success, Toast.LENGTH_SHORT).show();
            navigateToHome();
        }, 1500);
    }

    private String getEnteredOtp() {
        StringBuilder sb = new StringBuilder();
        for (EditText digitBox : otpDigits) {
            sb.append(digitBox.getText().toString().trim());
        }
        return sb.toString();
    }

    private void highlightEmptyBoxes() {
        for (EditText box : otpDigits) {
            if (box.getText().toString().trim().isEmpty()) {
                box.setBackgroundResource(R.drawable.bg_otp_box_error);
            }
        }
    }

    private void showError(String message) {
        tvErrorText.setText(message);
        tvErrorText.setVisibility(View.VISIBLE);
    }

    private void clearErrorState() {
        tvErrorText.setVisibility(View.GONE);
        for (EditText box : otpDigits) {
            if (box.getText().toString().length() == 1) {
                box.setBackgroundResource(R.drawable.bg_otp_box_filled);
            } else {
                box.setBackgroundResource(R.drawable.bg_otp_box_selector);
            }
        }
    }

    private void handleResendOtp() {
        if (isTimerRunning) return;

        // Clear existing digits
        for (EditText box : otpDigits) {
            box.setText("");
            box.setBackgroundResource(R.drawable.bg_otp_box_selector);
        }
        otpDigits[0].requestFocus();
        clearErrorState();

        // Show status banner
        tvStatusBanner.setText(R.string.otp_resent_success);
        tvStatusBanner.setVisibility(View.VISIBLE);
        Toast.makeText(this, R.string.otp_resent_success, Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(() ->
                tvStatusBanner.setVisibility(View.GONE), 4000);

        // Restart timer
        startResendTimer(TIMER_DURATION_MS);
    }

    private void startResendTimer(long durationMs) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        isTimerRunning = true;
        btnResendOtpCard.setVisibility(View.GONE);
        btnResendOtpBottom.setEnabled(false);
        btnResendOtpBottom.setAlpha(0.5f);
        tvResendTimer.setVisibility(View.VISIBLE);

        countDownTimer = new CountDownTimer(durationMs, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                long minutes = seconds / 60;
                long remSeconds = seconds % 60;
                String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, remSeconds);
                tvResendTimer.setText(getString(R.string.resend_timer, timeFormatted));
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                tvResendTimer.setVisibility(View.GONE);
                btnResendOtpCard.setVisibility(View.VISIBLE);
                btnResendOtpBottom.setEnabled(true);
                btnResendOtpBottom.setAlpha(1.0f);
            }
        }.start();
    }

    private void setLoadingState(boolean isLoading) {
        if (isLoading) {
            btnVerifyOtp.setEnabled(false);
            btnVerifyOtp.setText(R.string.verifying);
            progressBarVerify.setVisibility(View.VISIBLE);
        } else {
            btnVerifyOtp.setEnabled(true);
            btnVerifyOtp.setText(R.string.verify_otp);
            progressBarVerify.setVisibility(View.GONE);
        }
    }

    private void navigateToHome() {
        Intent intent = new Intent(OtpActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(OtpActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}