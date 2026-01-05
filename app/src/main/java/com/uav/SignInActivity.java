package com.uav;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignInActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private DatabaseHelper dbHelper;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        role = getIntent().getStringExtra("role");
        dbHelper = new DatabaseHelper(this);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        btnLogin.setOnClickListener(v -> login());

        tvRegister.setOnClickListener(v -> {
            Intent intent;
            if ("student".equals(role)) {
                intent = new Intent(this, RegisterStudentActivity.class);
            } else {
                intent = new Intent(this, RegisterLecturerActivity.class);
            }
            startActivity(intent);
        });
    }

    private void login() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        DatabaseHelper.User user = dbHelper.validateLogin(username, password);

        if (user == null || !user.getRole().equals(role)) {
            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        prefs.edit()
                .putInt("userId", user.getId())
                .putString("name", user.getName())
                .putString("role", user.getRole())
                .apply();

        Intent intent = role.equals("student")
                ? new Intent(this, StudentDashboardActivity.class)
                : new Intent(this, LecturerDashboardActivity.class);

        startActivity(intent);
        finish();
    }
}

