package com.example.todoappv2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoappv2.adapter.TodoAdapter;
import com.example.todoappv2.model.Todo;
import com.example.todoappv2.model.TodoWithCategories;
import com.example.todoappv2.model.Category;
import com.example.todoappv2.util.NotificationHelper;
import com.example.todoappv2.viewmodel.TodoViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.EditText;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ArrayAdapter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TodoViewModel todoViewModel;
    private TodoAdapter adapter;
    private List<TodoWithCategories> allTodos = new ArrayList<>();
    private int currentFilter = R.id.navigation_all;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create notification channel
        NotificationHelper.createNotificationChannel(this);

        // Initialize ViewModel first
        todoViewModel = new ViewModelProvider(this).get(TodoViewModel.class);

        // Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter = new TodoAdapter();
        recyclerView.setAdapter(adapter);

        // Set up toolbar
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setTitle(R.string.app_name);

        // Set up FAB
        FloatingActionButton fab = findViewById(R.id.fabAddTodo);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddTodoActivity.class);
            startActivity(intent);
        });

        // Set up bottom navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_categories) {
                startActivity(new Intent(MainActivity.this, CategoryManagementActivity.class));
                return false; // Don't change the selected item
            }
            if (itemId == R.id.navigation_focus) {
                startActivity(new Intent(MainActivity.this, FocusActivity.class));
                return false; // Don't change the selected item
            }
            currentFilter = itemId;
            filterTodos();
            return true;
        });

        // Set up adapter click listeners
        adapter.setOnItemClickListener(todoWithCategories -> {
            Todo todo = todoWithCategories.getTodo();
            Intent intent = new Intent(MainActivity.this, EditTodoActivity.class);
            intent.putExtra(EditTodoActivity.EXTRA_ID, todo.getId());
            intent.putExtra(EditTodoActivity.EXTRA_TITLE, todo.getTitle());
            intent.putExtra(EditTodoActivity.EXTRA_DESCRIPTION, todo.getDescription());
            intent.putExtra(EditTodoActivity.EXTRA_DUE_DATE, todo.getDueDate().getTime());
            intent.putExtra(EditTodoActivity.EXTRA_PRIORITY, todo.getPriority());
            intent.putExtra(EditTodoActivity.EXTRA_COMPLETED, todo.isCompleted());
            intent.putExtra(EditTodoActivity.EXTRA_HAS_REMINDER, todo.hasReminder());
            if (todo.getReminderTime() != null) {
                intent.putExtra(EditTodoActivity.EXTRA_REMINDER_TIME, todo.getReminderTime().getTime());
            }
            startActivity(intent);
        });

        adapter.setOnCheckBoxClickListener(todoWithCategories -> {
            Todo todo = todoWithCategories.getTodo();
            todo.setCompleted(!todo.isCompleted());
            todoViewModel.update(todo, todoWithCategories.getCategories());
        });

        adapter.setOnDeleteClickListener(todoWithCategories -> {
            showDeleteConfirmationDialog(todoWithCategories);
        });

        // Observe todos after setting up all UI components
        todoViewModel.getAllTodos().observe(this, todos -> {
            allTodos = todos;
            filterTodos();
        });

        // Observe filtered todos and update adapter
        todoViewModel.getFilteredTodos().observe(this, todos -> {
            if (todos != null) {
                adapter.submitList(todos);
            }
        });
    }

    private void showDeleteConfirmationDialog(TodoWithCategories todoWithCategories) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_todo)
                .setMessage(getString(R.string.delete_todo_confirmation, todoWithCategories.getTodo().getTitle()))
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    todoViewModel.delete(todoWithCategories.getTodo());
                    Toast.makeText(this, R.string.todo_deleted, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void filterTodos() {
        List<TodoWithCategories> filteredTodos = new ArrayList<>();
        for (TodoWithCategories todoWithCategories : allTodos) {
            Todo todo = todoWithCategories.getTodo();
            if (currentFilter == R.id.navigation_all ||
                    (currentFilter == R.id.navigation_active && !todo.isCompleted()) ||
                    (currentFilter == R.id.navigation_completed && todo.isCompleted())) {
                filteredTodos.add(todoWithCategories);
            }
        }
        adapter.submitList(filteredTodos);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete_all) {
            showDeleteAllConfirmationDialog();
            return true;
        }
        if (item.getItemId() == R.id.action_filter) {
            showFilterDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteAllConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_all)
                .setMessage(R.string.delete_all_confirmation)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    todoViewModel.deleteAll();
                    Toast.makeText(this, R.string.all_todos_deleted, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showFilterDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_filter, null);
        EditText editTextTitle = dialogView.findViewById(R.id.editTextTitle);
        EditText editTextDescription = dialogView.findViewById(R.id.editTextDescription);
        AutoCompleteTextView spinnerPriority = dialogView.findViewById(R.id.spinnerPriority);
        EditText editTextDueDateFrom = dialogView.findViewById(R.id.editTextDueDateFrom);
        EditText editTextDueDateTo = dialogView.findViewById(R.id.editTextDueDateTo);
        CheckBox checkBoxHasReminder = dialogView.findViewById(R.id.checkBoxHasReminder);
        EditText editTextReminderTimeFrom = dialogView.findViewById(R.id.editTextReminderTimeFrom);
        EditText editTextReminderTimeTo = dialogView.findViewById(R.id.editTextReminderTimeTo);
        AutoCompleteTextView spinnerCategory = dialogView.findViewById(R.id.spinnerCategory);

        // Set up priority spinner
        ArrayAdapter<CharSequence> priorityAdapter = ArrayAdapter.createFromResource(this,
                R.array.priority_levels, android.R.layout.simple_spinner_item);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(priorityAdapter);

        // Set up category spinner
        todoViewModel.getAllCategories().observe(this, categories -> {
            List<String> categoryNames = new ArrayList<>();
            for (Category category : categories) {
                categoryNames.add(category.getName());
            }
            ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, categoryNames);
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategory.setAdapter(categoryAdapter);
        });

        // Set up date pickers
        editTextDueDateFrom.setOnClickListener(v -> showDatePicker(editTextDueDateFrom));
        editTextDueDateTo.setOnClickListener(v -> showDatePicker(editTextDueDateTo));
        editTextReminderTimeFrom.setOnClickListener(v -> showTimePicker(editTextReminderTimeFrom));
        editTextReminderTimeTo.setOnClickListener(v -> showTimePicker(editTextReminderTimeTo));

        new AlertDialog.Builder(this)
                .setTitle("Filter Tasks")
                .setView(dialogView)
                .setPositiveButton("Apply", (dialog, which) -> {
                    String title = editTextTitle.getText().toString().trim();
                    String description = editTextDescription.getText().toString().trim();
                    Integer priority = null;
                    if (spinnerPriority.getText().length() > 0) {
                        int pos = priorityAdapter.getPosition(spinnerPriority.getText().toString());
                        if (pos >= 0) priority = pos + 1; // Assuming 1:Low, 2:Med, 3:High
                    }
                    Long dueDateFrom = parseDate(editTextDueDateFrom.getText().toString().trim());
                    Long dueDateTo = parseDate(editTextDueDateTo.getText().toString().trim());
                    Boolean hasReminder = checkBoxHasReminder.isChecked() ? true : null;
                    Long reminderTimeFrom = parseTime(editTextReminderTimeFrom.getText().toString().trim());
                    Long reminderTimeTo = parseTime(editTextReminderTimeTo.getText().toString().trim());
                    String categoryName = spinnerCategory.getText().toString().trim();
                    if (categoryName.isEmpty()) categoryName = null;
                    if (title.isEmpty()) title = null;
                    if (description.isEmpty()) description = null;
                    todoViewModel.filterTodos(title, description, priority, dueDateFrom, dueDateTo, hasReminder, reminderTimeFrom, reminderTimeTo, categoryName);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDatePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            editText.setText(sdf.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            editText.setText(sdf.format(calendar.getTime()));
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    private Long parseDate(String dateStr) {
        if (dateStr.isEmpty()) return null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return sdf.parse(dateStr).getTime();
        } catch (Exception e) {
            return null;
        }
    }

    private Long parseTime(String timeStr) {
        if (timeStr.isEmpty()) return null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(timeStr));
            return cal.getTimeInMillis();
        } catch (Exception e) {
            return null;
        }
    }
}