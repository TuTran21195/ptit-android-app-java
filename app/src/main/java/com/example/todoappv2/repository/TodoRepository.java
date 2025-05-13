package com.example.todoappv2.repository;

import android.app.Application;


import com.example.todoappv2.dao.TodoDao;
import com.example.todoappv2.database.TodoDatabase;
import com.example.todoappv2.model.Todo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TodoRepository {
    private TodoDao todoDao;
    private ExecutorService executorService;

    public TodoRepository(Application application) {
        TodoDatabase database = TodoDatabase.getInstance(application);
        todoDao = database.todoDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(Todo todo) {
        executorService.execute(() -> {
            // First insert the todo
            long todoId = todoDao.insert(todo);
            
        });
    }

    public void update(Todo todo) {
        executorService.execute(() -> {
            todoDao.update(todo);
        });
    }

    public void delete(Todo todo) {
        executorService.execute(() -> todoDao.delete(todo));
    }

    public void deleteAll() {
        executorService.execute(() -> todoDao.deleteAll());
    }

}