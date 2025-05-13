package com.example.todoappv2.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.todoappv2.dao.CategoryDao;
import com.example.todoappv2.database.TodoDatabase;
import com.example.todoappv2.model.Category;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CategoryRepository {
    private CategoryDao categoryDao;
    private LiveData<List<Category>> allCategories;
    private ExecutorService executorService;

    public CategoryRepository(Application application) {
        TodoDatabase database = TodoDatabase.getInstance(application);
        categoryDao = database.categoryDao();
        allCategories = categoryDao.getAllCategories();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(Category category) {
        executorService.execute(() -> categoryDao.insert(category));
    }

    public void update(Category category) {
        executorService.execute(() -> categoryDao.update(category));
    }

    public void delete(Category category) {
        executorService.execute(() -> categoryDao.delete(category));
    }

    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }

    public LiveData<Category> getCategory(int categoryId) {
        return categoryDao.getCategory(categoryId);
    }
} 