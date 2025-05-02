package com.example.todoapp.model;

public class DayItem {
    private String dayOfWeek; // MON TUE...
    private String dayOfMonth; // 1-> 31
    private boolean isSelected;

    private boolean hasTask; // ngày đó có task ko để hiển thị @drawable/bg_dot_has_task

    public DayItem(String dayOfWeek, String dayOfMonth, boolean isSelected, boolean hasTask) {
        this.dayOfWeek = dayOfWeek;
        this.dayOfMonth = dayOfMonth;
        this.isSelected = isSelected;
        this.hasTask = hasTask;
    }


    // Getters và Setters...
    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(String dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isHasTask() {
        return hasTask;
    }

    public void setHasTask(boolean hasTask) {
        this.hasTask = hasTask;
    }
}

