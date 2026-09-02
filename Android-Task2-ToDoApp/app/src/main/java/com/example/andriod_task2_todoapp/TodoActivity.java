package com.example.andriod_task2_todoapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TodoActivity extends AppCompatActivity {

    TextView tvWelcome, tvEmpty;
    Button btnLogout, btnAddTask;
    EditText etTask;
    RecyclerView recyclerTasks;

    DatabaseHelper databaseHelper;

    List<Task> taskList;
    TaskAdapter taskAdapter;

    int userId;
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        // Get user information
        userId = getIntent().getIntExtra("USER_ID", -1);
        userName = getIntent().getStringExtra("USER_NAME");

        // Connect UI elements
        tvWelcome = findViewById(R.id.tvWelcome);
        tvEmpty = findViewById(R.id.tvEmpty);

        btnLogout = findViewById(R.id.btnLogout);
        btnAddTask = findViewById(R.id.btnAddTask);

        etTask = findViewById(R.id.etTask);
        recyclerTasks = findViewById(R.id.recyclerTasks);

        databaseHelper = new DatabaseHelper(this);

        // Welcome message
        if (userName != null) {
            tvWelcome.setText("Welcome, " + userName);
        }

        // Create task list
        taskList = new ArrayList<>();

        // Create adapter
        taskAdapter = new TaskAdapter(
                taskList,
                new TaskAdapter.OnTaskActionListener() {

                    @Override
                    public void onTaskStatusChanged(
                            Task task,
                            boolean completed) {

                        databaseHelper.updateTaskStatus(
                                task.getId(),
                                userId,
                                completed
                        );
                    }

                    @Override
                    public void onTaskDeleted(Task task) {

                        boolean deleted =
                                databaseHelper.deleteTask(
                                        task.getId(),
                                        userId
                                );

                        if (deleted) {

                            loadTasks();

                            Toast.makeText(
                                    TodoActivity.this,
                                    "Task deleted",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
                }
        );

        // RecyclerView setup
        recyclerTasks.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerTasks.setAdapter(taskAdapter);

        // Load existing tasks
        loadTasks();

        // Add task
        btnAddTask.setOnClickListener(
                v -> addTask()
        );

        // Logout
        btnLogout.setOnClickListener(
                v -> logout()
        );
    }

    // Add a new task
    private void addTask() {

        String taskText =
                etTask.getText().toString().trim();

        if (taskText.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please enter a task",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (userId == -1) {

            Toast.makeText(
                    this,
                    "User session not found",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        boolean added =
                databaseHelper.addTask(
                        userId,
                        taskText
                );

        if (added) {

            etTask.setText("");

            loadTasks();

            Toast.makeText(
                    this,
                    "Task added",
                    Toast.LENGTH_SHORT
            ).show();

        } else {

            Toast.makeText(
                    this,
                    "Failed to add task",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // Load tasks for current user
    private void loadTasks() {

        taskList.clear();

        Cursor cursor =
                databaseHelper.getTasks(userId);

        while (cursor.moveToNext()) {

            int id =
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow("id")
                    );

            String taskText =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("task")
                    );

            boolean completed =
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                    "is_completed"
                            )
                    ) == 1;

            taskList.add(
                    new Task(
                            id,
                            taskText,
                            completed
                    )
            );
        }

        cursor.close();

        taskAdapter.notifyDataSetChanged();

        // Show empty message when there are no tasks
        if (taskList.isEmpty()) {

            tvEmpty.setVisibility(View.VISIBLE);
            recyclerTasks.setVisibility(View.GONE);

        } else {

            tvEmpty.setVisibility(View.GONE);
            recyclerTasks.setVisibility(View.VISIBLE);
        }
    }

    // Logout user
    private void logout() {

        getSharedPreferences(
                "TodoSession",
                MODE_PRIVATE
        ).edit()
                .clear()
                .apply();

        Intent intent =
                new Intent(
                        TodoActivity.this,
                        MainActivity.class
                );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);

        Toast.makeText(
                this,
                "Logged out successfully",
                Toast.LENGTH_SHORT
        ).show();

        finish();
    }
}