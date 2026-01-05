package com.uav;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "AttendanceSystem.db";
    private static final int DATABASE_VERSION = 1;

    // Users Table
    private static final String TABLE_USERS = "users";
    private static final String COL_USER_ID = "id";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";
    private static final String COL_NAME = "name";
    private static final String COL_ROLE = "role";

    // Students Table
    private static final String TABLE_STUDENTS = "students";
    private static final String COL_STUDENT_ID = "student_id";
    private static final String COL_MATRIC = "matric_number";
    private static final String COL_DEPARTMENT = "department";
    private static final String COL_LEVEL = "level";

    // Lecturers Table
    private static final String TABLE_LECTURERS = "lecturers";
    private static final String COL_LECTURER_ID = "lecturer_id";
    private static final String COL_FACULTY = "faculty";

    // Attendance Table
    private static final String TABLE_ATTENDANCE = "attendance";
    private static final String COL_ATTENDANCE_ID = "attendance_id";
    private static final String COL_ATT_STUDENT_ID = "student_id";
    private static final String COL_ATT_LECTURER_ID = "lecturer_id";
    private static final String COL_ATT_DEPARTMENT = "department";
    private static final String COL_ATT_LEVEL = "level";
    private static final String COL_DATE = "date";
    private static final String COL_TIME = "time";
    private static final String COL_STATUS = "status";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users Table
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + "("
                + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_USERNAME + " TEXT UNIQUE,"
                + COL_PASSWORD + " TEXT,"
                + COL_NAME + " TEXT,"
                + COL_ROLE + " TEXT)";
        db.execSQL(createUsersTable);

        // Create Students Table
        String createStudentsTable = "CREATE TABLE " + TABLE_STUDENTS + "("
                + COL_STUDENT_ID + " INTEGER PRIMARY KEY,"
                + COL_MATRIC + " TEXT UNIQUE,"
                + COL_DEPARTMENT + " TEXT,"
                + COL_LEVEL + " TEXT,"
                + "FOREIGN KEY(" + COL_STUDENT_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "))";
        db.execSQL(createStudentsTable);

        // Create Lecturers Table
        String createLecturersTable = "CREATE TABLE " + TABLE_LECTURERS + "("
                + COL_LECTURER_ID + " INTEGER PRIMARY KEY,"
                + COL_FACULTY + " TEXT,"
                + "FOREIGN KEY(" + COL_LECTURER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "))";
        db.execSQL(createLecturersTable);

        // Create Attendance Table
        String createAttendanceTable = "CREATE TABLE " + TABLE_ATTENDANCE + "("
                + COL_ATTENDANCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_ATT_STUDENT_ID + " INTEGER,"
                + COL_ATT_LECTURER_ID + " INTEGER,"
                + COL_ATT_DEPARTMENT + " TEXT,"
                + COL_ATT_LEVEL + " TEXT,"
                + COL_DATE + " TEXT,"
                + COL_TIME + " TEXT,"
                + COL_STATUS + " TEXT,"
                + "FOREIGN KEY(" + COL_ATT_STUDENT_ID + ") REFERENCES " + TABLE_STUDENTS + "(" + COL_STUDENT_ID + "),"
                + "FOREIGN KEY(" + COL_ATT_LECTURER_ID + ") REFERENCES " + TABLE_LECTURERS + "(" + COL_LECTURER_ID + "))";
        db.execSQL(createAttendanceTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTENDANCE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LECTURERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Check if username exists
    public boolean checkUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COL_USER_ID},
                COL_USERNAME + "=?", new String[]{username}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Validate login
    public User validateLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null,
                COL_USERNAME + "=? AND " + COL_PASSWORD + "=?",
                new String[]{username, password}, null, null, null);

        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(0));
            user.setUsername(cursor.getString(1));
            user.setPassword(cursor.getString(2));
            user.setName(cursor.getString(3));
            user.setRole(cursor.getString(4));
        }
        cursor.close();
        return user;
    }

    // Register Student
    public long registerStudent(String username, String password, String name,
                                String matricNumber, String department, String level) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Insert into Users table
        ContentValues userValues = new ContentValues();
        userValues.put(COL_USERNAME, username);
        userValues.put(COL_PASSWORD, password);
        userValues.put(COL_NAME, name);
        userValues.put(COL_ROLE, "student");
        long userId = db.insert(TABLE_USERS, null, userValues);

        // Insert into Students table
        if (userId != -1) {
            ContentValues studentValues = new ContentValues();
            studentValues.put(COL_STUDENT_ID, userId);
            studentValues.put(COL_MATRIC, matricNumber);
            studentValues.put(COL_DEPARTMENT, department);
            studentValues.put(COL_LEVEL, level);
            db.insert(TABLE_STUDENTS, null, studentValues);
        }

        return userId;
    }

    // Register Lecturer
    public long registerLecturer(String username, String password, String name, String faculty) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Insert into Users table
        ContentValues userValues = new ContentValues();
        userValues.put(COL_USERNAME, username);
        userValues.put(COL_PASSWORD, password);
        userValues.put(COL_NAME, name);
        userValues.put(COL_ROLE, "lecturer");
        long userId = db.insert(TABLE_USERS, null, userValues);

        // Insert into Lecturers table
        if (userId != -1) {
            ContentValues lecturerValues = new ContentValues();
            lecturerValues.put(COL_LECTURER_ID, userId);
            lecturerValues.put(COL_FACULTY, faculty);
            db.insert(TABLE_LECTURERS, null, lecturerValues);
        }

        return userId;
    }

    // Get students by department and level
    public List<Student> getStudentsByDeptAndLevel(String department, String level) {
        List<Student> students = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT u." + COL_USER_ID + ", u." + COL_NAME + ", s." + COL_MATRIC +
                ", s." + COL_DEPARTMENT + ", s." + COL_LEVEL +
                " FROM " + TABLE_USERS + " u INNER JOIN " + TABLE_STUDENTS + " s ON u." +
                COL_USER_ID + " = s." + COL_STUDENT_ID +
                " WHERE s." + COL_DEPARTMENT + "=? AND s." + COL_LEVEL + "=?";

        Cursor cursor = db.rawQuery(query, new String[]{department, level});

        if (cursor.moveToFirst()) {
            do {
                Student student = new Student();
                student.setId(cursor.getInt(0));
                student.setName(cursor.getString(1));
                student.setMatricNumber(cursor.getString(2));
                student.setDepartment(cursor.getString(3));
                student.setLevel(cursor.getString(4));
                students.add(student);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return students;
    }

    // Mark attendance
    public long markAttendance(int studentId, int lecturerId, String department,
                               String level, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        String currentTime = timeFormat.format(new Date());

        values.put(COL_ATT_STUDENT_ID, studentId);
        values.put(COL_ATT_LECTURER_ID, lecturerId);
        values.put(COL_ATT_DEPARTMENT, department);
        values.put(COL_ATT_LEVEL, level);
        values.put(COL_DATE, currentDate);
        values.put(COL_TIME, currentTime);
        values.put(COL_STATUS, status);

        return db.insert(TABLE_ATTENDANCE, null, values);
    }

    // Get attendance for a student
    public List<Attendance> getStudentAttendance(int studentId) {
        List<Attendance> attendanceList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ATTENDANCE, null,
                COL_ATT_STUDENT_ID + "=?", new String[]{String.valueOf(studentId)},
                null, null, COL_DATE + " DESC");

        if (cursor.moveToFirst()) {
            do {
                Attendance attendance = new Attendance();
                attendance.setId(cursor.getInt(0));
                attendance.setStudentId(cursor.getInt(1));
                attendance.setLecturerId(cursor.getInt(2));
                attendance.setDepartment(cursor.getString(3));
                attendance.setLevel(cursor.getString(4));
                attendance.setDate(cursor.getString(5));
                attendance.setTime(cursor.getString(6));
                attendance.setStatus(cursor.getString(7));
                attendanceList.add(attendance);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return attendanceList;
    }

    // Get all departments (distinct)
    public List<String> getAllDepartments() {
        List<String> departments = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT " + COL_DEPARTMENT + " FROM " + TABLE_STUDENTS, null);

        if (cursor.moveToFirst()) {
            do {
                departments.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return departments;
    }

    // Get all levels (distinct)
    public List<String> getAllLevels() {
        List<String> levels = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT " + COL_LEVEL + " FROM " + TABLE_STUDENTS, null);

        if (cursor.moveToFirst()) {
            do {
                levels.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return levels;
    }

    // Inner classes for data models
    public static class User {
        private int id;
        private String username;
        private String password;
        private String name;
        private String role;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    public static class Student {
        private int id;
        private String name;
        private String matricNumber;
        private String department;
        private String level;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getMatricNumber() { return matricNumber; }
        public void setMatricNumber(String matricNumber) { this.matricNumber = matricNumber; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        public String getLevel() { return level; }
        public void setLevel(String level) { this.level = level; }
    }

    public static class Attendance {
        private int id;
        private int studentId;
        private int lecturerId;
        private String department;
        private String level;
        private String date;
        private String time;
        private String status;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public int getStudentId() { return studentId; }
        public void setStudentId(int studentId) { this.studentId = studentId; }
        public int getLecturerId() { return lecturerId; }
        public void setLecturerId(int lecturerId) { this.lecturerId = lecturerId; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        public String getLevel() { return level; }
        public void setLevel(String level) { this.level = level; }
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public String getTime() { return time; }
        public void setTime(String time) { this.time = time; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
