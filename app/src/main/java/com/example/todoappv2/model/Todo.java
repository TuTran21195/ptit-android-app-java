package com.example.todoappv2.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "todos")
public class Todo {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String description;
    private boolean isCompleted;
    private Date createdAt;
    private Date dueDate;
    private int priority; // 1: Low, 2: Medium, 3: High
    private boolean hasReminder;
    private Date reminderTime;

    public Todo(String title, String description, Date dueDate, int priority) {
        this.title = title;
        this.description = description;
        this.isCompleted = false;
        this.createdAt = new Date();
        this.dueDate = dueDate;
        this.priority = priority;
        this.hasReminder = false;
        this.reminderTime = null;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
    
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    
    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }
    
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    
    public boolean hasReminder() { return hasReminder; }
    public void setHasReminder(boolean hasReminder) { this.hasReminder = hasReminder; }
    
    public Date getReminderTime() { return reminderTime; }
    public void setReminderTime(Date reminderTime) { this.reminderTime = reminderTime; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Todo todo = (Todo) o;
        return id == todo.id &&
                isCompleted == todo.isCompleted &&
                priority == todo.priority &&
                hasReminder == todo.hasReminder &&
                (title != null ? title.equals(todo.title) : todo.title == null) &&
                (description != null ? description.equals(todo.description) : todo.description == null) &&
                (createdAt != null ? createdAt.equals(todo.createdAt) : todo.createdAt == null) &&
                (dueDate != null ? dueDate.equals(todo.dueDate) : todo.dueDate == null) &&
                (reminderTime != null ? reminderTime.equals(todo.reminderTime) : todo.reminderTime == null);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (isCompleted ? 1 : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (dueDate != null ? dueDate.hashCode() : 0);
        result = 31 * result + priority;
        result = 31 * result + (hasReminder ? 1 : 0);
        result = 31 * result + (reminderTime != null ? reminderTime.hashCode() : 0);
        return result;
    }
} 