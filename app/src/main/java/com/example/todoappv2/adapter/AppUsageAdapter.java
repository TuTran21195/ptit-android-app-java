package com.example.todoappv2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoappv2.R;
import com.example.todoappv2.model.AppUsage;

import java.util.List;

public class AppUsageAdapter extends RecyclerView.Adapter<AppUsageAdapter.UsageViewHolder> {
    private List<AppUsage> appUsageList;

    public AppUsageAdapter(List<AppUsage> appUsageList) {
        this.appUsageList = appUsageList;
    }

    @NonNull
    @Override
    public UsageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app_usage, parent, false);
        return new UsageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsageViewHolder holder, int position) {
        AppUsage usage = appUsageList.get(position);
        holder.icon.setImageResource(usage.getIconResId());
        holder.name.setText(usage.getAppName());
        holder.time.setText("You spent " + usage.getUsageTime() + " on " + usage.getAppName() + " today");
    }

    @Override
    public int getItemCount() {
        return appUsageList.size();
    }

    static class UsageViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name, time;
        UsageViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.appIcon);
            name = itemView.findViewById(R.id.appName);
            time = itemView.findViewById(R.id.appTime);
        }
    }
}