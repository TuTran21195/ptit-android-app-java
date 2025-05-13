package com.example.todoappv2.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.example.todoappv2.model.Todo;

import java.util.List;

@Dao
public interface TodoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Todo todo);

    @Update
    void update(Todo todo);

    @Delete
    void delete(Todo todo);

    @Query("DELETE FROM todos")
    void deleteAll();

    @Query("SELECT * FROM todos ORDER BY dueDate ASC")
    LiveData<List<Todo>> getAllTodos();

    @Query("SELECT * FROM todos WHERE isCompleted = 0 ORDER BY dueDate ASC")
    LiveData<List<Todo>> getActiveTodos();

    @Query("SELECT * FROM todos WHERE isCompleted = 1 ORDER BY dueDate ASC")
    LiveData<List<Todo>> getCompletedTodos();



    @Query("SELECT * FROM todos WHERE id = :id")
    LiveData<Todo> getTodoById(int id);



    @RawQuery(observedEntities = Todo.class)
    LiveData<List<Todo>> filterTodos(SupportSQLiteQuery query);
}