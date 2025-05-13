package com.example.todoappv2;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.todoappv2.model.Category;
import com.example.todoappv2.model.Todo;
import com.example.todoappv2.util.ReminderManager;
import com.example.todoappv2.viewmodel.TodoViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class AddTodoActivity extends AppCompatActivity {
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
    private List<Category> selectedCategories = new ArrayList<>();
    private List<String> categoryNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);

        // Set up toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.add_todo);

        // Initialize views
        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        editTextDueDate = findViewById(R.id.edit_text_due_date);
        editTextReminderTime = findViewById(R.id.edit_text_reminder_time);
        spinnerPriority = findViewById(R.id.spinner_priority);
        spinnerCategories = findViewById(R.id.spinner_categories);
        chipGroupCategories = findViewById(R.id.chip_group_categories);

        // Set up priority spinner
        ArrayAdapter<CharSequence> priorityAdapter = ArrayAdapter.createFromResource(this,
                R.array.priority_levels, android.R.layout.simple_spinner_item);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(priorityAdapter);

        // Initialize calendar
        calendar = Calendar.getInstance();
        reminderCalendar = Calendar.getInstance();

        // Set up due date picker
        editTextDueDate.setOnClickListener(v -> showDatePicker());

        // Set up reminder time picker
        editTextReminderTime.setOnClickListener(v -> showReminderTimePicker());

        // Initialize ViewModel
        todoViewModel = new ViewModelProvider(this).get(TodoViewModel.class);

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
            addCategoryChip(categoryName);
            spinnerCategories.setText(""); // Clear the text after selection
        });

        // Set up save button
        findViewById(R.id.button_save).setOnClickListener(v -> saveTodo());
    }

    private void addCategoryChip(String categoryName) {
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
            selectedCategories.removeIf(category -> category.getName().equals(categoryName));
        });

        // Add category to selected categories
        Category category = new Category(categoryName);
        selectedCategories.add(category);

        chipGroupCategories.addView(chip);
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    updateDueDateField();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void updateDueDateField() {
        String date = String.format("%02d/%02d/%d",
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.YEAR));
        editTextDueDate.setText(date);
    }

    private void showReminderTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    reminderCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    reminderCalendar.set(Calendar.MINUTE, minute);
                    updateReminderTimeField();
                },
                reminderCalendar.get(Calendar.HOUR_OF_DAY),
                reminderCalendar.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private void updateReminderTimeField() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        editTextReminderTime.setText(timeFormat.format(reminderCalendar.getTime()));
    }

    private void saveTodo() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String dueDateStr = editTextDueDate.getText().toString().trim();
        String reminderTimeStr = editTextReminderTime.getText().toString().trim();
        String priorityStr = spinnerPriority.getText().toString();

        if (title.isEmpty()) {
            editTextTitle.setError("Title is required");
            editTextTitle.requestFocus();
            return;
        }

        if (dueDateStr.isEmpty()) {
            editTextDueDate.setError("Due date is required");
            editTextDueDate.requestFocus();
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

        // Create Todo object
        Todo todo = new Todo(title, description, calendar.getTime(), priority);

        // Set reminder if time is provided
        if (!reminderTimeStr.isEmpty()) {
            todo.setHasReminder(true);
            todo.setReminderTime(reminderCalendar.getTime());
        }

        // Insert todo with categories
        todoViewModel.insert(todo, selectedCategories);

        // Schedule reminder if set
        if (todo.hasReminder()) {
            ReminderManager.scheduleReminder(this, todo);
        }

        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}