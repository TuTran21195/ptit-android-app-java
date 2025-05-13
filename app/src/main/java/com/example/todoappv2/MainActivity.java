package com.example.todoappv2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.imageview.ShapeableImageView;

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
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.content.SharedPreferences;
import com.bumptech.glide.Glide;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.ByteArrayOutputStream;
import android.util.Base64;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private TodoViewModel todoViewModel;
    private TodoAdapter adapter;
    private List<TodoWithCategories> allTodos = new ArrayList<>();
    private int currentFilter = R.id.navigation_all;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ShapeableImageView profileImage;
    private TextView profileName;
    private TextView profileEmail;
    private static final int PICK_IMAGE = 100;
    private static final String PREF_NAME = "UserProfile";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PROFILE_IMAGE = "profile_image";
    private SharedPreferences sharedPreferences;
    private Uri currentProfileImageUri;

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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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

        // Setup DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Setup ActionBarDrawerToggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Setup Navigation Header
        View headerView = navigationView.getHeaderView(0);
        profileImage = headerView.findViewById(R.id.nav_header_image);
        profileName = headerView.findViewById(R.id.nav_header_name);
        profileEmail = headerView.findViewById(R.id.nav_header_email);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Load saved profile data
        loadProfileData();

        // Setup profile edit button
        headerView.findViewById(R.id.fab_edit_profile).setOnClickListener(v -> {
            showEditProfileDialog();
        });

        // Setup profile image click
        profileImage.setOnClickListener(v -> {
            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(gallery, PICK_IMAGE);
        });

        // Set default selected item
        navigationView.setCheckedItem(R.id.nav_home);
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
        if (item.getItemId() == R.id.action_calendar) {
            startActivity(new Intent(MainActivity.this, CalendarActivity.class));
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

    private void loadProfileData() {
        String savedUsername = sharedPreferences.getString(KEY_USERNAME, "User");
        String savedEmail = sharedPreferences.getString(KEY_EMAIL, "user@example.com");
        String savedImageBase64 = sharedPreferences.getString(KEY_PROFILE_IMAGE, null);

        profileName.setText(savedUsername);
        profileEmail.setText(savedEmail);

        if (savedImageBase64 != null) {
            try {
                byte[] imageBytes = Base64.decode(savedImageBase64, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                profileImage.setImageBitmap(bitmap);
            } catch (Exception e) {
                profileImage.setImageResource(R.drawable.default_profile);
            }
        } else {
            profileImage.setImageResource(R.drawable.default_profile);
        }
    }

    private void showEditProfileDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);
        EditText editName = dialogView.findViewById(R.id.edit_name);
        EditText editEmail = dialogView.findViewById(R.id.edit_email);

        editName.setText(profileName.getText());
        editEmail.setText(profileEmail.getText());

        new AlertDialog.Builder(this)
            .setTitle("Edit Profile")
            .setView(dialogView)
            .setPositiveButton("Save", (dialog, which) -> {
                String newName = editName.getText().toString().trim();
                String newEmail = editEmail.getText().toString().trim();

                if (!newName.isEmpty()) {
                    profileName.setText(newName);
                    sharedPreferences.edit().putString(KEY_USERNAME, newName).apply();
                }
                if (!newEmail.isEmpty()) {
                    profileEmail.setText(newEmail);
                    sharedPreferences.edit().putString(KEY_EMAIL, newEmail).apply();
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE && data != null) {
            Uri selectedImage = data.getData();
            if (selectedImage != null) {
                try {
                    // Load and resize image
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    bitmap = getResizedBitmap(bitmap, 512); // Resize to max 512px

                    // Convert to Base64 and save
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                    String imageBase64 = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
                    
                    // Save to SharedPreferences
                    sharedPreferences.edit().putString(KEY_PROFILE_IMAGE, imageBase64).apply();
                    
                    // Update UI
                    profileImage.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private Bitmap getResizedBitmap(Bitmap bitmap, int maxSize) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float bitmapRatio = (float) width / (float) height;
        
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.nav_calendar) {
            startActivity(new Intent(this, CalendarActivity.class));
        } else if (id == R.id.nav_focus) {
            startActivity(new Intent(this, FocusActivity.class));
        } else if (id == R.id.nav_categories) {
            startActivity(new Intent(this, CategoryManagementActivity.class));
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(this, AboutActivity.class));
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}