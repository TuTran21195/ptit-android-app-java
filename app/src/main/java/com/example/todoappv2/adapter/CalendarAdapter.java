package com.example.todoappv2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoappv2.R;
import com.example.todoappv2.model.CalendarDay;

import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
    private List<CalendarDay> days;
    private OnDayClickListener listener;
    private int itemWidth;

    public interface OnDayClickListener {
        void onDayClick(CalendarDay day, int position);
    }

    public CalendarAdapter(List<CalendarDay> days, OnDayClickListener listener, int itemWidth) {
        this.days = days;
        this.listener = listener;
        this.itemWidth = itemWidth;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calendar_day, parent, false);
        
        // Set the width of the item
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        lp.width = itemWidth;
        view.setLayoutParams(lp);
        
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        CalendarDay day = days.get(position);
        holder.bind(day);
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public void updateData(List<CalendarDay> newDays) {
        this.days = newDays;
        notifyDataSetChanged();
    }

    class CalendarViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDayName;
        private TextView tvDayNumber;
        private View viewDot;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayName = itemView.findViewById(R.id.tvDayName);
            tvDayNumber = itemView.findViewById(R.id.tvDayNumber);
            viewDot = itemView.findViewById(R.id.viewDot);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onDayClick(days.get(position), position);
                }
            });
        }

        public void bind(CalendarDay day) {
            tvDayName.setText(day.getDayName());
            tvDayNumber.setText(day.getDayNumber());
            viewDot.setVisibility(day.isHasUncompletedTasks() ? View.VISIBLE : View.GONE);
            itemView.setSelected(day.isSelected());
        }
    }
} 