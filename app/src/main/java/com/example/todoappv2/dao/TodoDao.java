package com.example.todoappv2.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Transaction;
import androidx.room.Update;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.example.todoappv2.model.Category;
import com.example.todoappv2.model.Todo;
import com.example.todoappv2.model.TodoCategoryCrossRef;
import com.example.todoappv2.model.TodoWithCategories;

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTodoCategoryCrossRef(TodoCategoryCrossRef crossRef);

    @Query("DELETE FROM todo_category_cross_ref WHERE todoId = :todoId")
    void deleteTodoCategoryCrossRefs(int todoId);

    @Transaction
    @Query("SELECT * FROM todos ORDER BY dueDate ASC")
    LiveData<List<TodoWithCategories>> getTodosWithCategories();

    @Transaction
    @Query("SELECT * FROM todos WHERE isCompleted = 0 ORDER BY dueDate ASC")
    LiveData<List<TodoWithCategories>> getActiveTodosWithCategories();

    @Transaction
    @Query("SELECT * FROM todos WHERE isCompleted = 1 ORDER BY dueDate ASC")
    LiveData<List<TodoWithCategories>> getCompletedTodosWithCategories();

    @Query("SELECT * FROM todos WHERE id = :id")
    LiveData<Todo> getTodoById(int id);

    @Query("SELECT * FROM category_table WHERE name = :name LIMIT 1")
    Category getCategoryByName(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertCategory(Category category);

    @Transaction
    @Query("SELECT * FROM todos WHERE id = :id")
    LiveData<TodoWithCategories> getTodoWithCategories(int id);

    @RawQuery(observedEntities = Todo.class)
    LiveData<List<Todo>> filterTodos(SupportSQLiteQuery query);

    @RawQuery(observedEntities = {Todo.class, TodoCategoryCrossRef.class, Category.class})
    LiveData<List<TodoWithCategories>> filterTodosWithCategories(SupportSQLiteQuery query);
} 