package com.example.todoappv2.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "todo_category_cross_ref",
        primaryKeys = {"todoId", "categoryId"},
        foreignKeys = {
            @ForeignKey(entity = Todo.class,
                    parentColumns = "id",
                    childColumns = "todoId",
                    onDelete = ForeignKey.CASCADE),
            @ForeignKey(entity = Category.class,
                    parentColumns = "id",
                    childColumns = "categoryId",
                    onDelete = ForeignKey.CASCADE)
        })
public class TodoCategoryCrossRef {
    private int todoId;
    private int categoryId;

    public TodoCategoryCrossRef(int todoId, int categoryId) {
        this.todoId = todoId;
        this.categoryId = categoryId;
    }

    public int getTodoId() {
        return todoId;
    }

    public void setTodoId(int todoId) {
        this.todoId = todoId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
} 