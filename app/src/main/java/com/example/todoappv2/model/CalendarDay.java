package com.example.todoappv2.model;

import java.util.Calendar;
import java.util.Date;

public class CalendarDay {
    private Date date;
    private boolean hasUncompletedTasks;
    private boolean isSelected;

    public CalendarDay(Date date) {
        this.date = date;
        this.hasUncompletedTasks = false;
        this.isSelected = false;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isHasUncompletedTasks() {
        return hasUncompletedTasks;
    }

    public void setHasUncompletedTasks(boolean hasUncompletedTasks) {
        this.hasUncompletedTasks = hasUncompletedTasks;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getDayName() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String[] days = new String[] {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
        return days[calendar.get(Calendar.DAY_OF_WEEK) - 1];
    }

    public String getDayNumber() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }
} 