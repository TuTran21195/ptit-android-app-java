package com.example.todoappv2.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.todoappv2.dao.TodoDao;
import com.example.todoappv2.database.TodoDatabase;
import com.example.todoappv2.model.Category;
import com.example.todoappv2.model.Todo;
import com.example.todoappv2.model.TodoCategoryCrossRef;
import com.example.todoappv2.model.TodoWithCategories;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

public class TodoRepository {
    private TodoDao todoDao;
    private LiveData<List<TodoWithCategories>> allTodos;
    private LiveData<List<TodoWithCategories>> activeTodos;
    private LiveData<List<TodoWithCategories>> completedTodos;
    private ExecutorService executorService;

    public TodoRepository(Application application) {
        TodoDatabase database = TodoDatabase.getInstance(application);
        todoDao = database.todoDao();
        allTodos = todoDao.getTodosWithCategories();
        activeTodos = todoDao.getActiveTodosWithCategories();
        completedTodos = todoDao.getCompletedTodosWithCategories();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(Todo todo, List<Category> categories) {
        executorService.execute(() -> {
            // First insert the todo
            long todoId = todoDao.insert(todo);

            // Then handle each category
            for (Category category : categories) {
                // Check if category exists
                Category existingCategory = todoDao.getCategoryByName(category.getName());
                long categoryId;

                if (existingCategory == null) {
                    // If category doesn't exist, insert it
                    categoryId = todoDao.insertCategory(category);
                } else {
                    // If category exists, use its ID
                    categoryId = existingCategory.getId();
                }

                // Create the relationship
                todoDao.insertTodoCategoryCrossRef(new TodoCategoryCrossRef((int) todoId, (int) categoryId));
            }
        });
    }

    public void update(Todo todo, List<Category> categories) {
        executorService.execute(() -> {
            todoDao.update(todo);
            // Delete existing category relationships
            todoDao.deleteTodoCategoryCrossRefs(todo.getId());
            // Add new category relationships
            for (Category category : categories) {
                // Check if category exists
                Category existingCategory = todoDao.getCategoryByName(category.getName());
                long categoryId;

                if (existingCategory == null) {
                    // If category doesn't exist, insert it
                    categoryId = todoDao.insertCategory(category);
                } else {
                    // If category exists, use its ID
                    categoryId = existingCategory.getId();
                }

                // Create the relationship
                todoDao.insertTodoCategoryCrossRef(new TodoCategoryCrossRef(todo.getId(), (int) categoryId));
            }
        });
    }

    public void delete(Todo todo) {
        executorService.execute(() -> todoDao.delete(todo));
    }

    public void deleteAll() {
        executorService.execute(() -> todoDao.deleteAll());
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

    public LiveData<Todo> getTodoById(int id) {
        return todoDao.getTodoById(id);
    }

    public LiveData<TodoWithCategories> getTodoWithCategories(int id) {
        return todoDao.getTodoWithCategories(id);
    }

    public LiveData<List<Todo>> filterTodos(
            String title,
            String description,
            Integer priority,
            Long dueDateFrom,
            Long dueDateTo,
            Boolean hasReminder,
            Long reminderTimeFrom,
            Long reminderTimeTo,
            String categoryName // can be null
    ) {
        StringBuilder query = new StringBuilder();
        List<Object> args = new java.util.ArrayList<>();
        query.append("SELECT t.* FROM todos t");

        if (categoryName != null && !categoryName.isEmpty()) {
            query.append(" INNER JOIN todo_category_cross_ref c ON t.id = c.todoId ");
            query.append(" INNER JOIN category_table cat ON c.categoryId = cat.id ");
        }

        query.append(" WHERE 1=1 ");

        if (title != null && !title.isEmpty()) {
            query.append(" AND t.title LIKE ? ");
            args.add("%" + title + "%");
        }
        if (description != null && !description.isEmpty()) {
            query.append(" AND t.description LIKE ? ");
            args.add("%" + description + "%");
        }
        if (priority != null) {
            query.append(" AND t.priority = ? ");
            args.add(priority);
        }
        if (dueDateFrom != null) {
            query.append(" AND t.dueDate >= ? ");
            args.add(dueDateFrom);
        }
        if (dueDateTo != null) {
            query.append(" AND t.dueDate <= ? ");
            args.add(dueDateTo);
        }
        if (hasReminder != null) {
            query.append(" AND t.hasReminder = ? ");
            args.add(hasReminder ? 1 : 0);
        }
        if (reminderTimeFrom != null) {
            query.append(" AND t.reminderTime >= ? ");
            args.add(reminderTimeFrom);
        }
        if (reminderTimeTo != null) {
            query.append(" AND t.reminderTime <= ? ");
            args.add(reminderTimeTo);
        }
        if (categoryName != null && !categoryName.isEmpty()) {
            query.append(" AND cat.name = ? ");
            args.add(categoryName);
        }

        query.append(" ORDER BY t.dueDate ASC");

        SupportSQLiteQuery sqLiteQuery = new SimpleSQLiteQuery(query.toString(), args.toArray());
        return todoDao.filterTodos(sqLiteQuery);
    }

    public LiveData<List<TodoWithCategories>> filterTodosWithCategories(
            String title,
            String description,
            Integer priority,
            Long dueDateFrom,
            Long dueDateTo,
            Boolean hasReminder,
            Long reminderTimeFrom,
            Long reminderTimeTo,
            String categoryName // can be null
    ) {
        StringBuilder query = new StringBuilder();
        List<Object> args = new java.util.ArrayList<>();
        query.append("SELECT t.* FROM todos t");
        if (categoryName != null && !categoryName.isEmpty()) {
            query.append(" INNER JOIN todo_category_cross_ref c ON t.id = c.todoId ");
            query.append(" INNER JOIN category_table cat ON c.categoryId = cat.id ");
        }
        query.append(" WHERE 1=1 ");
        if (title != null && !title.isEmpty()) {
            query.append(" AND t.title LIKE ? ");
            args.add("%" + title + "%");
        }
        if (description != null && !description.isEmpty()) {
            query.append(" AND t.description LIKE ? ");
            args.add("%" + description + "%");
        }
        if (priority != null) {
            query.append(" AND t.priority = ? ");
            args.add(priority);
        }
        if (dueDateFrom != null) {
            query.append(" AND t.dueDate >= ? ");
            args.add(dueDateFrom);
        }
        if (dueDateTo != null) {
            query.append(" AND t.dueDate <= ? ");
            args.add(dueDateTo);
        }
        if (hasReminder != null) {
            query.append(" AND t.hasReminder = ? ");
            args.add(hasReminder ? 1 : 0);
        }
        if (reminderTimeFrom != null) {
            query.append(" AND t.reminderTime >= ? ");
            args.add(reminderTimeFrom);
        }
        if (reminderTimeTo != null) {
            query.append(" AND t.reminderTime <= ? ");
            args.add(reminderTimeTo);
        }
        if (categoryName != null && !categoryName.isEmpty()) {
            query.append(" AND cat.name = ? ");
            args.add(categoryName);
        }
        query.append(" ORDER BY t.dueDate ASC");
        SupportSQLiteQuery sqLiteQuery = new SimpleSQLiteQuery(query.toString(), args.toArray());
        return todoDao.filterTodosWithCategories(sqLiteQuery);
    }
}