package com.example.todoappv2.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import com.example.todoappv2.model.Todo;
import com.example.todoappv2.repository.TodoRepository;


public class TodoViewModel extends AndroidViewModel {
    private TodoRepository todoRepository;

    public TodoViewModel(Application application) {
        super(application);
        todoRepository = new TodoRepository(application);

    }

    public void insert(Todo todo) {
        todoRepository.insert(todo);
    }

    public void update(Todo todo) {
        todoRepository.update(todo);
    }

    public void delete(Todo todo) {
        todoRepository.delete(todo);
    }

    public void deleteAll() {
        todoRepository.deleteAll();
    }

}