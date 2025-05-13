package com.example.todoappv2;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoappv2.adapter.CalendarAdapter;
import com.example.todoappv2.adapter.TodoAdapter;
import com.example.todoappv2.model.CalendarDay;
import com.example.todoappv2.model.Todo;
import com.example.todoappv2.model.TodoWithCategories;
import com.example.todoappv2.repository.TodoRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class CalendarActivity extends AppCompatActivity implements CalendarAdapter.OnDayClickListener {
    private RecyclerView rvCalendar;
    private RecyclerView rvTasks;
    private ImageButton btnPreviousWeek;
    private ImageButton btnNextWeek;
    private Button btnActiveTask;
    private Button btnCompletedTask;
    private TextView tvMonthYear;
    
    private CalendarAdapter calendarAdapter;
    private TodoAdapter todoAdapter;
    private List<CalendarDay> days;
    private List<TodoWithCategories> allTodos;
    private Calendar currentWeek;
    private boolean showActiveTasks = true;
    private TodoRepository todoRepository;
    private SimpleDateFormat monthYearFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        todoRepository = new TodoRepository(getApplication());
        monthYearFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        
        initializeViews();
        setupRecyclerViews();
        setupClickListeners();
        
        // Set current week to start from today
        currentWeek = Calendar.getInstance();
        currentWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // Start week from Monday
        if (currentWeek.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            currentWeek.add(Calendar.DATE, -6); // If today is Sunday, go back to previous Monday
        } else {
            currentWeek.add(Calendar.DATE, -(currentWeek.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY)); // Go back to Monday
        }
        
        loadCurrentWeek();
        
        // Observe tasks changes
        todoRepository.getAllTodos().observe(this, todosWithCategories -> {
            allTodos = todosWithCategories;
            loadCurrentWeek(); // Reload to update dots
            updateTaskList(); // Update task list if needed
        });
    }

    private void initializeViews() {
        rvCalendar = findViewById(R.id.rvCalendar);
        rvTasks = findViewById(R.id.rvTasks);
        btnPreviousWeek = findViewById(R.id.btnPreviousWeek);
        btnNextWeek = findViewById(R.id.btnNextWeek);
        btnActiveTask = findViewById(R.id.btnActiveTask);
        btnCompletedTask = findViewById(R.id.btnCompletedTask);
        tvMonthYear = findViewById(R.id.tvMonthYear);
        
        // Set initial button states
        btnActiveTask.setAlpha(1.0f);
        btnCompletedTask.setAlpha(0.5f);
    }

    private void setupRecyclerViews() {
        // Get screen width to calculate calendar item width
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int itemWidth = displayMetrics.widthPixels / 8; // Divide by 8 to leave some margin
        
        // Setup Calendar RecyclerView
        days = new ArrayList<>();
        calendarAdapter = new CalendarAdapter(days, this, itemWidth);
        rvCalendar.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvCalendar.setAdapter(calendarAdapter);

        // Setup Tasks RecyclerView
        allTodos = new ArrayList<>();
        todoAdapter = new TodoAdapter();
        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        rvTasks.setAdapter(todoAdapter);

        // Setup TodoAdapter click listeners
        todoAdapter.setOnCheckBoxClickListener(todoWithCategories -> {
            Todo todo = todoWithCategories.getTodo();
            todo.setCompleted(!todo.isCompleted());
            todoRepository.update(todo, todoWithCategories.getCategories());
        });
    }

    private void setupClickListeners() {
        btnPreviousWeek.setOnClickListener(v -> {
            currentWeek.add(Calendar.WEEK_OF_YEAR, -1);
            loadCurrentWeek();
        });

        btnNextWeek.setOnClickListener(v -> {
            currentWeek.add(Calendar.WEEK_OF_YEAR, 1);
            loadCurrentWeek();
        });

        btnActiveTask.setOnClickListener(v -> {
            showActiveTasks = true;
            updateTaskList();
            updateButtonStyles();
        });

        btnCompletedTask.setOnClickListener(v -> {
            showActiveTasks = false;
            updateTaskList();
            updateButtonStyles();
        });
    }

    private void loadCurrentWeek() {
        days.clear();
        Calendar calendar = (Calendar) currentWeek.clone();
        
        // Update month/year display using the middle day of the week
        Calendar middleOfWeek = (Calendar) calendar.clone();
        middleOfWeek.add(Calendar.DATE, 3);
        tvMonthYear.setText(monthYearFormat.format(middleOfWeek.getTime()));

        // Get today for comparison
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        for (int i = 0; i < 7; i++) {
            CalendarDay day = new CalendarDay(calendar.getTime());
            day.setHasUncompletedTasks(hasUncompletedTasks(day.getDate()));
            
            // Select today by default if it's in the current week
            Calendar dayCalendar = Calendar.getInstance();
            dayCalendar.setTime(day.getDate());
            dayCalendar.set(Calendar.HOUR_OF_DAY, 0);
            dayCalendar.set(Calendar.MINUTE, 0);
            dayCalendar.set(Calendar.SECOND, 0);
            dayCalendar.set(Calendar.MILLISECOND, 0);
            
            if (dayCalendar.equals(today)) {
                day.setSelected(true);
            }
            
            days.add(day);
            calendar.add(Calendar.DATE, 1);
        }

        calendarAdapter.updateData(days);
        
        // If no day is selected (today is not in the current week), select the first day
        if (days.stream().noneMatch(CalendarDay::isSelected)) {
            onDayClick(days.get(0), 0);
        } else {
            // Update task list for the selected day
            updateTaskList();
        }
    }

    private boolean hasUncompletedTasks(Date date) {
        if (allTodos == null) return false;
        
        Calendar taskCal = Calendar.getInstance();
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(date);
        
        // Reset time part for date comparison
        dateCal.set(Calendar.HOUR_OF_DAY, 0);
        dateCal.set(Calendar.MINUTE, 0);
        dateCal.set(Calendar.SECOND, 0);
        dateCal.set(Calendar.MILLISECOND, 0);
        
        return allTodos.stream().anyMatch(todoWithCategories -> {
            Todo todo = todoWithCategories.getTodo();
            if (todo.isCompleted()) return false;
            if (todo.getDueDate() == null) return false;
            
            taskCal.setTime(todo.getDueDate());
            taskCal.set(Calendar.HOUR_OF_DAY, 0);
            taskCal.set(Calendar.MINUTE, 0);
            taskCal.set(Calendar.SECOND, 0);
            taskCal.set(Calendar.MILLISECOND, 0);
            
            return taskCal.equals(dateCal);
        });
    }

    private void updateTaskList() {
        CalendarDay selectedDay = days.stream()
                .filter(CalendarDay::isSelected)
                .findFirst()
                .orElse(null);

        if (selectedDay != null) {
            List<TodoWithCategories> tasksForDay = getTasksForDay(selectedDay.getDate());
            List<TodoWithCategories> filteredTasks = tasksForDay.stream()
                    .filter(todoWithCategories -> todoWithCategories.getTodo().isCompleted() != showActiveTasks)
                    .collect(Collectors.toList());
            todoAdapter.submitList(filteredTasks);
        }
    }

    private List<TodoWithCategories> getTasksForDay(Date date) {
        if (allTodos == null) return new ArrayList<>();
        
        Calendar taskCal = Calendar.getInstance();
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(date);
        
        // Reset time part for date comparison
        dateCal.set(Calendar.HOUR_OF_DAY, 0);
        dateCal.set(Calendar.MINUTE, 0);
        dateCal.set(Calendar.SECOND, 0);
        dateCal.set(Calendar.MILLISECOND, 0);
        
        return allTodos.stream()
                .filter(todoWithCategories -> {
                    Todo todo = todoWithCategories.getTodo();
                    if (todo.getDueDate() == null) return false;
                    
                    taskCal.setTime(todo.getDueDate());
                    taskCal.set(Calendar.HOUR_OF_DAY, 0);
                    taskCal.set(Calendar.MINUTE, 0);
                    taskCal.set(Calendar.SECOND, 0);
                    taskCal.set(Calendar.MILLISECOND, 0);
                    
                    return taskCal.equals(dateCal);
                })
                .collect(Collectors.toList());
    }

    private void updateButtonStyles() {
        btnActiveTask.setAlpha(showActiveTasks ? 1.0f : 0.5f);
        btnCompletedTask.setAlpha(showActiveTasks ? 0.5f : 1.0f);
    }

    @Override
    public void onDayClick(CalendarDay day, int position) {
        for (CalendarDay d : days) {
            d.setSelected(false);
        }
        day.setSelected(true);
        calendarAdapter.notifyDataSetChanged();
        updateTaskList();
    }
} 