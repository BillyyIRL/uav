package com.uav;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LecturerDashboardActivity extends AppCompatActivity {
    private TextView tvWelcome;
    private Spinner spinnerDepartment, spinnerLevel;
    private Button btnLoadStudents, btnMarkAttendance, btnLogout;
    private ListView lvStudents;
    private DatabaseHelper dbHelper;
    private int userId;
    private String userName;
    private List<DatabaseHelper.Student> currentStudents;
    private Map<Integer, Boolean> attendanceStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer_dashboard);

        dbHelper = new DatabaseHelper(this);
        attendanceStatus = new HashMap<>();

        // Get user session
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);
        userName = prefs.getString("name", "Lecturer");

        tvWelcome = findViewById(R.id.tvWelcome);
        spinnerDepartment = findViewById(R.id.spinnerDepartment);
        spinnerLevel = findViewById(R.id.spinnerLevel);
        btnLoadStudents = findViewById(R.id.btnLoadStudents);
        btnMarkAttendance = findViewById(R.id.btnMarkAttendance);
        btnLogout = findViewById(R.id.btnLogout);
        lvStudents = findViewById(R.id.lvStudents);

        tvWelcome.setText("Welcome, " + userName);

        setupSpinners();

        btnLoadStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadStudents();
            }
        });

        btnMarkAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markAttendance();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
                prefs.edit().clear().apply();

                Intent intent = new Intent(LecturerDashboardActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setupSpinners() {
        // Setup department spinner
        String[] departments = {"Computer Science", "Information Technology",
                "Software Engineering", "Cyber Security", "Data Science"};
        ArrayAdapter<String> deptAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, departments);
        deptAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepartment.setAdapter(deptAdapter);

        // Setup level spinner
        String[] levels = {"100", "200", "300", "400", "500"};
        ArrayAdapter<String> levelAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, levels);
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLevel.setAdapter(levelAdapter);
    }

    private void loadStudents() {
        String department = spinnerDepartment.getSelectedItem().toString();
        String level = spinnerLevel.getSelectedItem().toString();

        currentStudents = dbHelper.getStudentsByDeptAndLevel(department, level);

        if (currentStudents.isEmpty()) {
            Toast.makeText(this, "No students found for selected criteria", Toast.LENGTH_SHORT).show();
            return;
        }

        // Initialize all students as absent by default
        attendanceStatus.clear();
        for (DatabaseHelper.Student student : currentStudents) {
            attendanceStatus.put(student.getId(), false);
        }

        List<String> displayList = new ArrayList<>();
        for (DatabaseHelper.Student student : currentStudents) {
            String display = student.getName() + "\n" +
                    "Matric: " + student.getMatricNumber();
            displayList.add(display);
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_multiple_choice, displayList);
        lvStudents.setAdapter(adapter);
        lvStudents.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        // Handle item clicks to toggle attendance
        lvStudents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DatabaseHelper.Student student = currentStudents.get(position);
                boolean isChecked = lvStudents.isItemChecked(position);
                attendanceStatus.put(student.getId(), isChecked);
            }
        });

        Toast.makeText(this, "Loaded " + currentStudents.size() + " students", Toast.LENGTH_SHORT).show();
    }

    private void markAttendance() {
        if (currentStudents == null || currentStudents.isEmpty()) {
            Toast.makeText(this, "Please load students first", Toast.LENGTH_SHORT).show();
            return;
        }

        String department = spinnerDepartment.getSelectedItem().toString();
        String level = spinnerLevel.getSelectedItem().toString();

        int markedCount = 0;
        for (DatabaseHelper.Student student : currentStudents) {
            boolean isPresent = attendanceStatus.get(student.getId());
            String status = isPresent ? "present" : "absent";

            long result = dbHelper.markAttendance(student.getId(), userId, department, level, status);
            if (result != -1) {
                markedCount++;
            }
        }

        if (markedCount > 0) {
            Toast.makeText(this, "Attendance marked for " + markedCount + " students", Toast.LENGTH_LONG).show();
            // Clear the list after marking
            lvStudents.setAdapter(null);
            currentStudents = null;
            attendanceStatus.clear();
        } else {
            Toast.makeText(this, "Failed to mark attendance", Toast.LENGTH_SHORT).show();
        }
    }
}