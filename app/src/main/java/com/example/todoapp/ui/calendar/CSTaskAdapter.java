package com.example.todoapp.ui.calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.R;
import com.example.todoapp.model.Task;

import java.text.SimpleDateFormat;
import java.util.List;

public class CSTaskAdapter extends RecyclerView.Adapter<CSTaskAdapter.CSTaskViewHolder> {
    private List<Task> taskList;
    private Context context;

    public CSTaskAdapter(Context context, List<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public CSTaskAdapter.CSTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cs_item_task, parent, false);
        return new CSTaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CSTaskAdapter.CSTaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        holder.textTitle.setText(task.getTitle());
        holder.textTime.setText(new SimpleDateFormat("HH:mm").format(task.getDueTime()));
        // tạm thời chưa có catergory
        holder.textTag.setText(task.getTagID()); // Hiển thị tag (có thể thay bằng icon)

        holder.textPriority.setText(String.valueOf(task.getPriority())); // Hiển thị mức độ ưu tiên
        holder.textDescription.setText(task.getDescription());

    }


    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class CSTaskViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textTime, textTag, textPriority, textDescription;

        public CSTaskViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.cs_text_task_title);
            textTime = itemView.findViewById(R.id.cs_text_task_time);
            textTag = itemView.findViewById(R.id.cs_text_tag);
            textPriority = itemView.findViewById(R.id.cs_text_task_priority);
            textDescription = itemView.findViewById(R.id.cs_text_task_desc);
        }
    }
}
