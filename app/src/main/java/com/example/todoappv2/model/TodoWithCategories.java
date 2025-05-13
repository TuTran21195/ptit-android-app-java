package com.example.todoappv2.model;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class TodoWithCategories {
    @Embedded
    private Todo todo;

    @Relation(
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(
                    value = TodoCategoryCrossRef.class,
                    parentColumn = "todoId",
                    entityColumn = "categoryId"
            )
    )
    private List<Category> categories;

    public TodoWithCategories(Todo todo, List<Category> categories) {
        this.todo = todo;
        this.categories = categories;
    }

    public Todo getTodo() {
        return todo;
    }

    public void setTodo(Todo todo) {
        this.todo = todo;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
} 