package com.example.andriod_task2_todoapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "todo_database.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "email TEXT UNIQUE, " +
                "password TEXT)");

        db.execSQL("CREATE TABLE tasks (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "task TEXT, " +
                "is_completed INTEGER DEFAULT 0)");
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS tasks");
        db.execSQL("DROP TABLE IF EXISTS users");

        onCreate(db);
    }

    // Register new user
    public boolean registerUser(
            String name,
            String email,
            String hashedPassword) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("email", email);
        values.put("password", hashedPassword);

        long result =
                db.insert(
                        "users",
                        null,
                        values
                );

        return result != -1;
    }

    // Login user
    public Cursor loginUser(
            String email,
            String hashedPassword) {

        SQLiteDatabase db =
                this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM users WHERE email = ? AND password = ?",
                new String[]{
                        email,
                        hashedPassword
                }
        );
    }

    // Add task
    public boolean addTask(
            int userId,
            String task) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put("user_id", userId);
        values.put("task", task);
        values.put("is_completed", 0);

        long result =
                db.insert(
                        "tasks",
                        null,
                        values
                );

        return result != -1;
    }

    // Get tasks for a specific user
    public Cursor getTasks(int userId) {

        SQLiteDatabase db =
                this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM tasks " +
                        "WHERE user_id = ? " +
                        "ORDER BY id DESC",
                new String[]{
                        String.valueOf(userId)
                }
        );
    }

    // Update task completion status
    public boolean updateTaskStatus(
            int taskId,
            int userId,
            boolean completed) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                "is_completed",
                completed ? 1 : 0
        );

        int result =
                db.update(
                        "tasks",
                        values,
                        "id = ? AND user_id = ?",
                        new String[]{
                                String.valueOf(taskId),
                                String.valueOf(userId)
                        }
                );

        return result > 0;
    }

    // Delete task
    public boolean deleteTask(
            int taskId,
            int userId) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        int result =
                db.delete(
                        "tasks",
                        "id = ? AND user_id = ?",
                        new String[]{
                                String.valueOf(taskId),
                                String.valueOf(userId)
                        }
                );

        return result > 0;
    }
}