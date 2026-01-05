package com.uav;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterLecturerActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etName, etFaculty;
    private Button btnRegister;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_lecturer);

        dbHelper = new DatabaseHelper(this);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etName = findViewById(R.id.etName);
        etFaculty = findViewById(R.id.etFaculty);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> registerLecturer());
    }

    private void registerLecturer() {
        long id = dbHelper.registerLecturer(
                etUsername.getText().toString(),
                etPassword.getText().toString(),
                etName.getText().toString(),
                etFaculty.getText().toString()
        );

        if (id != -1) {
            Toast.makeText(this, "Lecturer registered successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
        }
    }
}
