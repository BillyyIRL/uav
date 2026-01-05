package com.uav;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class StudentDashboardActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private ListView lvAttendance;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        dbHelper = new DatabaseHelper(this);

        tvWelcome = findViewById(R.id.tvWelcome);
        lvAttendance = findViewById(R.id.lvAttendance);

        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        int studentId = prefs.getInt("userId", -1);
        String name = prefs.getString("name", "Student");

        tvWelcome.setText("Welcome, " + name);

        List<DatabaseHelper.Attendance> records =
                dbHelper.getStudentAttendance(studentId);

        List<String> display = new ArrayList<>();
        for (DatabaseHelper.Attendance a : records) {
            display.add(a.getDate() + " - " + a.getStatus().toUpperCase());
        }

        lvAttendance.setAdapter(new AttendanceAdapter(this, display));
    }
}
