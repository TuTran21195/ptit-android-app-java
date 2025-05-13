package com.example.todoappv2.model;

public class AppUsage {
    private String appName;
    private String usageTime;
    private int iconResId;

    public AppUsage(String appName, String usageTime, int iconResId) {
        this.appName = appName;
        this.usageTime = usageTime;
        this.iconResId = iconResId;
    }

    public String getAppName() {
        return appName;
    }

    public String getUsageTime() {
        return usageTime;
    }

    public int getIconResId() {
        return iconResId;
    }
} 