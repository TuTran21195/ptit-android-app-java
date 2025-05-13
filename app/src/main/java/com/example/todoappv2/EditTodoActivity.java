package com.example.todoappv2;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.todoappv2.model.Category;
import com.example.todoappv2.model.Todo;
import com.example.todoappv2.model.TodoWithCategories;
import com.example.todoappv2.util.ReminderManager;
import com.example.todoappv2.viewmodel.TodoViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class EditTodoActivity extends AppCompatActivity {
    public static final String EXTRA_ID = "com.example.todoappv2.EXTRA_ID";
    public static final String EXTRA_TITLE = "com.example.todoappv2.EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION = "com.example.todoappv2.EXTRA_DESCRIPTION";
    public static final String EXTRA_DUE_DATE = "com.example.todoappv2.EXTRA_DUE_DATE";
    public static final String EXTRA_PRIORITY = "com.example.todoappv2.EXTRA_PRIORITY";
    public static final String EXTRA_COMPLETED = "com.example.todoappv2.EXTRA_COMPLETED";
    public static final String EXTRA_HAS_REMINDER = "com.example.todoappv2.EXTRA_HAS_REMINDER";
    public static final String EXTRA_REMINDER_TIME = "com.example.todoappv2.EXTRA_REMINDER_TIME";

    private TextInputEditText editTextTitle;
    private TextInputEditText editTextDescription;
    private TextInputEditText editTextDueDate;
    private TextInputEditText editTextReminderTime;
    private AutoCompleteTextView spinnerPriority;
    private AutoCompleteTextView spinnerCategories;
    private ChipGroup chipGroupCategories;
    private TodoViewModel todoViewModel;
    private Calendar calendar;
    private Calendar reminderCalendar;
    private SimpleDateFormat dateFormat;
    private int todoId;
    private List<Category> selectedCategories = new ArrayList<>();
    private List<String> categoryNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_todo);

        // Set up toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.edit_todo);

        // Initialize views
        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        editTextDueDate = findViewById(R.id.edit_text_due_date);
        editTextReminderTime = findViewById(R.id.edit_text_reminder_time);
        spinnerPriority = findViewById(R.id.spinner_priority);
        spinnerCategories = findViewById(R.id.spinner_categories);
        chipGroupCategories = findViewById(R.id.chip_group_categories);

        // Initialize calendars
        calendar = Calendar.getInstance();
        reminderCalendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        // Get intent data
        Intent intent = getIntent();
        todoId = intent.getIntExtra(EXTRA_ID, -1);
        if (todoId == -1) {
            Toast.makeText(this, "Error: Todo not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set up priority spinner
        ArrayAdapter<CharSequence> priorityAdapter = ArrayAdapter.createFromResource(this,
                R.array.priority_levels, android.R.layout.simple_spinner_item);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(priorityAdapter);

        // Set up due date picker
        editTextDueDate.setOnClickListener(v -> showDatePicker());

        // Set up reminder time picker
        editTextReminderTime.setOnClickListener(v -> showReminderTimePicker());

        // Initialize ViewModel
        todoViewModel = new ViewModelProvider(this).get(TodoViewModel.class);

        // Load todo data
        todoViewModel.getTodoWithCategories(todoId).observe(this, todoWithCategories -> {
            if (todoWithCategories != null) {
                Todo todo = todoWithCategories.getTodo();
                editTextTitle.setText(todo.getTitle());
                editTextDescription.setText(todo.getDescription());

                // Set due date
                calendar.setTime(todo.getDueDate());
                editTextDueDate.setText(dateFormat.format(todo.getDueDate()));

                // Set priority
                String priority;
                switch (todo.getPriority()) {
                    case 3:
                        priority = "High";
                        break;
                    case 2:
                        priority = "Medium";
                        break;
                    default:
                        priority = "Low";
                }
                spinnerPriority.setText(priority, false);

                // Set reminder if exists
                if (todo.hasReminder() && todo.getReminderTime() != null) {
                    reminderCalendar.setTime(todo.getReminderTime());
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    editTextReminderTime.setText(timeFormat.format(todo.getReminderTime()));
                }

                // Set categories
                selectedCategories.clear();
                selectedCategories.addAll(todoWithCategories.getCategories());
                chipGroupCategories.removeAllViews();
                for (Category category : selectedCategories) {
                    addCategoryChip(category.getName(), false);
                }
            }
        });

        // Observe categories
        todoViewModel.getAllCategories().observe(this, categories -> {
            categoryNames.clear();
            for (Category category : categories) {
                categoryNames.add(category.getName());
            }
            ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, categoryNames);
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategories.setAdapter(categoryAdapter);
        });

        // Set up category selection
        spinnerCategories.setOnItemClickListener((parent, view, position, id) -> {
            String categoryName = parent.getItemAtPosition(position).toString();
            addCategoryChip(categoryName, true);
            spinnerCategories.setText(""); // Clear the text after selection
        });

        // Set up save button
        findViewById(R.id.button_save).setOnClickListener(v -> saveTodo());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    editTextDueDate.setText(dateFormat.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showReminderTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    reminderCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    reminderCalendar.set(Calendar.MINUTE, minute);
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    editTextReminderTime.setText(timeFormat.format(reminderCalendar.getTime()));
                },
                reminderCalendar.get(Calendar.HOUR_OF_DAY),
                reminderCalendar.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private void addCategoryChip(String categoryName, boolean addToList) {
        // Check if category is already added
        for (int i = 0; i < chipGroupCategories.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupCategories.getChildAt(i);
            if (chip.getText().toString().equals(categoryName)) {
                return; // Category already exists
            }
        }

        // Create and add new chip
        Chip chip = new Chip(this);
        chip.setText(categoryName);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> {
            chipGroupCategories.removeView(chip);
            Iterator<Category> iterator = selectedCategories.iterator();
            while (iterator.hasNext()) {
                Category c = iterator.next();
                if (c.getName().equals(categoryName)) {
                    iterator.remove();
                }
            }
        });

        // Add category to selected categories if needed
        if (addToList) {
            Category category = new Category(categoryName);
            selectedCategories.add(category);
        }

        chipGroupCategories.addView(chip);
    }

    private void saveTodo() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String priorityStr = spinnerPriority.getText().toString();
        String reminderTimeStr = editTextReminderTime.getText().toString().trim();

        if (title.isEmpty()) {
            editTextTitle.setError("Title is required");
            editTextTitle.requestFocus();
            return;
        }

        if (selectedCategories.isEmpty()) {
            Toast.makeText(this, "Please select at least one category", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert priority string to integer
        int priority;
        switch (priorityStr.toLowerCase()) {
            case "high":
                priority = 3;
                break;
            case "medium":
                priority = 2;
                break;
            default:
                priority = 1; // Low
        }

        boolean isCompleted = getIntent().getBooleanExtra(EXTRA_COMPLETED, false);

        Todo todo = new Todo(title, description, calendar.getTime(), priority);
        todo.setId(todoId);
        todo.setCompleted(isCompleted);

        // Set reminder if time is provided
        if (!reminderTimeStr.isEmpty()) {
            todo.setHasReminder(true);
            todo.setReminderTime(reminderCalendar.getTime());
        } else {
            todo.setHasReminder(false);
            todo.setReminderTime(null);
        }

        todoViewModel.update(todo, selectedCategories);

        // Update reminder if needed
        if (todo.hasReminder()) {
            ReminderManager.scheduleReminder(this, todo);
        } else {
            ReminderManager.cancelReminder(this, todo);
        }

        Toast.makeText(this, "Todo updated", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 