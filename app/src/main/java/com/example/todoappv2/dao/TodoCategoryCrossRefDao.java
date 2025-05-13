package com.example.todoappv2.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface TodoCategoryCrossRefDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(long todoId, int categoryId);

    @Query("DELETE FROM todo_category_cross_ref WHERE todoId = :todoId")
    void deleteTodoCategories(long todoId);

    @Query("DELETE FROM todo_category_cross_ref")
    void deleteAllTodoCategories();
} 