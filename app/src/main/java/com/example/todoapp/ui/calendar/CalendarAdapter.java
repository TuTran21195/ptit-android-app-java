package com.example.todoapp.ui.calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.R;
import com.example.todoapp.model.DayItem;

import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.DayViewHolder> {

    Context context;
    private List<DayItem> dayList;
    private int selectedPosition = 0;
    private final OnDateClickListener listener;

    public interface OnDateClickListener {
        void onDateClick(int position);
    }

    public CalendarAdapter(Context context,List<DayItem> dayList, OnDateClickListener listener) {
        this.dayList = dayList;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cs_item_day, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarAdapter.DayViewHolder holder, int position) {
        DayItem item = dayList.get(position);

        // Set day of week and day of month
        holder.textDay.setText(item.getDayOfWeek());
        holder.textDate.setText(item.getDayOfMonth());

        // Highlight selected day using the current position
        if (item.isSelected()){
            holder.itemView.setBackgroundResource(R.drawable.bg_day_selected);
        }
//        holder.itemView.setBackgroundResource(
//                holder.getAdapterPosition() == selectedPosition
//                        ? R.drawable.bg_day_selected
//                        : R.drawable.bg_day_unselected
//        );

        // Hiển thị chấm nếu có task
        holder.dot.setVisibility(item.isHasTask() ? View.VISIBLE : View.INVISIBLE);

//        holder.itemView.setOnClickListener(v -> {
//            int currentPosition = holder.getAdapterPosition();
//            if (currentPosition != RecyclerView.NO_POSITION) { // Safety check
//                int oldPos = selectedPosition;
//                selectedPosition = currentPosition;
//                notifyItemChanged(oldPos);
//                notifyItemChanged(currentPosition);
//                listener.onDateClick(currentPosition);
//            }
//        });

        // Handle click on a day
        holder.itemView.setOnClickListener(v -> {
            // Deselect all days
            for (int i = 0; i < dayList.size(); i++) {
                dayList.get(i).setSelected(i == position);
            }
            notifyDataSetChanged(); // Refresh the RecyclerView
            if (listener!= null) {
                listener.onDateClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return dayList.size();
    }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        TextView textDay, textDate;
        View dot;

        DayViewHolder(View itemView) {
            super(itemView);
            textDay = itemView.findViewById(R.id.text_day_of_week);
            textDate = itemView.findViewById(R.id.text_day_of_month);
            dot = itemView.findViewById(R.id.view_task_dot);
        }

    }

}
