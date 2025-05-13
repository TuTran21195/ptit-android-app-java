package com.example.todoappv2.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import com.example.todoappv2.model.Category;
import com.example.todoappv2.model.Todo;
import com.example.todoappv2.model.TodoWithCategories;
import com.example.todoappv2.repository.CategoryRepository;
import com.example.todoappv2.repository.TodoRepository;
import java.util.List;

public class TodoViewModel extends AndroidViewModel {
    private TodoRepository todoRepository;
    private CategoryRepository categoryRepository;
    private LiveData<List<TodoWithCategories>> allTodos;
    private LiveData<List<TodoWithCategories>> activeTodos;
    private LiveData<List<TodoWithCategories>> completedTodos;
    private LiveData<List<Category>> allCategories;
    private MutableLiveData<LiveData<List<TodoWithCategories>>> filteredTodos = new MutableLiveData<>();

    public TodoViewModel(Application application) {
        super(application);
        todoRepository = new TodoRepository(application);
        categoryRepository = new CategoryRepository(application);
        allTodos = todoRepository.getAllTodos();
        activeTodos = todoRepository.getActiveTodos();
        completedTodos = todoRepository.getCompletedTodos();
        allCategories = categoryRepository.getAllCategories();
    }

    public void insert(Todo todo, List<Category> categories) {
        todoRepository.insert(todo, categories);
    }

    public void update(Todo todo, List<Category> categories) {
        todoRepository.update(todo, categories);
    }

    public void delete(Todo todo) {
        todoRepository.delete(todo);
    }

    public void deleteAll() {
        todoRepository.deleteAll();
    }

    public LiveData<List<TodoWithCategories>> getAllTodos() {
        return allTodos;
    }

    public LiveData<List<TodoWithCategories>> getActiveTodos() {
        return activeTodos;
    }

    public LiveData<List<TodoWithCategories>> getCompletedTodos() {
        return completedTodos;
    }

    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }

    public void insertCategory(Category category) {
        categoryRepository.insert(category);
    }

    public void updateCategory(Category category) {
        categoryRepository.update(category);
    }

    public void deleteCategory(Category category) {
        categoryRepository.delete(category);
    }

    public LiveData<Todo> getTodoById(int id) {
        return todoRepository.getTodoById(id);
    }

    public LiveData<TodoWithCategories> getTodoWithCategories(int id) {
        return todoRepository.getTodoWithCategories(id);
    }

    public LiveData<List<TodoWithCategories>> getFilteredTodos() {
        return Transformations.switchMap(filteredTodos, data -> data);
    }

    public void filterTodos(
            String title,
            String description,
            Integer priority,
            Long dueDateFrom,
            Long dueDateTo,
            Boolean hasReminder,
            Long reminderTimeFrom,
            Long reminderTimeTo,
            String categoryName
    ) {
        filteredTodos.setValue(todoRepository.filterTodosWithCategories(
                title, description, priority, dueDateFrom, dueDateTo, hasReminder, reminderTimeFrom, reminderTimeTo, categoryName
        ));
    }
}