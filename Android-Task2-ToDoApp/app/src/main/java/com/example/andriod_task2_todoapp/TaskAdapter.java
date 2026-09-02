package com.example.andriod_task2_todoapp;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private OnTaskActionListener listener;

    public interface OnTaskActionListener {

        void onTaskStatusChanged(
                Task task,
                boolean completed
        );

        void onTaskDeleted(Task task);
    }

    public TaskAdapter(
            List<Task> taskList,
            OnTaskActionListener listener) {

        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_task,
                        parent,
                        false
                );

        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull TaskViewHolder holder,
            int position) {

        Task task = taskList.get(position);

        holder.checkTask.setText(task.getTask());

        // Prevent listener from firing while setting the checkbox
        holder.checkTask.setOnCheckedChangeListener(null);

        holder.checkTask.setChecked(
                task.isCompleted()
        );

        updateTaskAppearance(
                holder,
                task.isCompleted()
        );

        // Complete / uncomplete task
        holder.checkTask.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    task.setCompleted(isChecked);

                    updateTaskAppearance(
                            holder,
                            isChecked
                    );

                    listener.onTaskStatusChanged(
                            task,
                            isChecked
                    );
                }
        );

        // Delete task
        holder.btnDelete.setOnClickListener(
                v -> listener.onTaskDeleted(task)
        );
    }

    private void updateTaskAppearance(
            TaskViewHolder holder,
            boolean completed) {

        if (completed) {

            holder.checkTask.setPaintFlags(
                    holder.checkTask.getPaintFlags()
                            | Paint.STRIKE_THRU_TEXT_FLAG
            );

        } else {

            holder.checkTask.setPaintFlags(
                    holder.checkTask.getPaintFlags()
                            & (~Paint.STRIKE_THRU_TEXT_FLAG)
            );
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder
            extends RecyclerView.ViewHolder {

        CheckBox checkTask;
        Button btnDelete;

        public TaskViewHolder(
                @NonNull View itemView) {

            super(itemView);

            checkTask =
                    itemView.findViewById(
                            R.id.checkTask
                    );

            btnDelete =
                    itemView.findViewById(
                            R.id.btnDelete
                    );
        }
    }
}