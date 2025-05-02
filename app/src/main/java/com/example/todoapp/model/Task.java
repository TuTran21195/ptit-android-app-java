package com.example.todoapp.model;

import java.sql.Time;
import java.util.Date;
import java.util.List;

public class Task {

    String id;
    String userId; // Foreign Key
    String title;
    String description;
    Date dueDate;
    Time dueTime;
    int priority; // từ 1 -> 10 để đánh dấu mức độ ưu tiên theo đúng thiết kế Figma
    boolean isCompleted;
    String tagID; // Foreign Key : Catergory table

    List<String> subTaskIds; // Foreign Key
    boolean isReminderSet;

//    Constructor đủ đối


    public Task(String id, String userId, String title, String description, Date dueDate, Time dueTime, int priority, boolean isCompleted, String tagID, List<String> subTaskIds, boolean isReminderSet) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.dueTime = dueTime;
        this.priority = priority;
        this.isCompleted = isCompleted;
        this.tagID = tagID;
        this.subTaskIds = subTaskIds;
        this.isReminderSet = isReminderSet;
    }

    // Getter và Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Time getDueTime() {
        return dueTime;
    }

    public void setDueTime(Time dueTime) {
        this.dueTime = dueTime;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getTagID() {
        return tagID;
    }

    public void setTagID(String tagID) {
        this.tagID = tagID;
    }

    public boolean isReminderSet() {
        return isReminderSet;
    }


    public void setReminderSet(boolean reminderSet) {
        isReminderSet = reminderSet;
    }
}