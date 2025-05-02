package com.example.todoapp.ui.calendar;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.todoapp.R;
import com.example.todoapp.model.DayItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class CalendarFragment extends Fragment implements CalendarAdapter.OnDateClickListener {

    private RecyclerView recyclerDays;
    private CalendarAdapter dayAdapter;
    private List<DayItem> dayList;
    private TextView textMonthYear;
    private ImageButton btnPreviousWeek, btnNextWeek;
    private Calendar calendar;

    public CalendarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.cs_fragment_calendar, container, false);

        // Initialize views
        recyclerDays = view.findViewById(R.id.recycler_days);
        textMonthYear = view.findViewById(R.id.text_month_year);
        btnPreviousWeek = view.findViewById(R.id.btn_previous_week);
        btnNextWeek = view.findViewById(R.id.btn_next_week);

        // Initialize calendar and day list
        calendar = Calendar.getInstance();
        dayList = new ArrayList<>();

        // Set up RecyclerView
        recyclerDays.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        dayAdapter = new CalendarAdapter(getContext(), dayList, this);
        recyclerDays.setAdapter(dayAdapter);

        // Load initial week (hardcoded for testing)
        loadHardcodedWeek();

        // Update month/year display
        updateMonthYear();

        // Set up button listeners
        btnPreviousWeek.setOnClickListener(v -> {
            calendar.add(Calendar.WEEK_OF_YEAR, -1); // Go to previous week
            loadWeekDays();
            updateMonthYear();
        });

        btnNextWeek.setOnClickListener(v -> {
            calendar.add(Calendar.WEEK_OF_YEAR, 1); // Go to next week
            loadWeekDays();
            updateMonthYear();
        });

        return view;
    }
    // Hardcoded test data for the week shown in the screenshot (Feb 6-12, 2022)
    private void loadHardcodedWeek() {
        dayList.clear();
        dayList.add(new DayItem("SUN", "6", false, false));
        dayList.add(new DayItem("MON", "7", false, false));
        dayList.add(new DayItem("TUE", "8", false, true)); // Has a task
        dayList.add(new DayItem("WED", "9", true, true));  // Selected and has a task
        dayList.add(new DayItem("THU", "10", false, true)); // Has a task
        dayList.add(new DayItem("FRI", "11", false, false));
        dayList.add(new DayItem("SAT", "12", false, false));
        dayAdapter.notifyDataSetChanged();

        // Set calendar to Feb 9, 2022 for consistency
        calendar.set(2022, Calendar.FEBRUARY, 9);
    }

    // Load days of the current week dynamically
    private void loadWeekDays() {
        dayList.clear();
        Calendar tempCalendar = (Calendar) calendar.clone();
        tempCalendar.set(Calendar.DAY_OF_WEEK, tempCalendar.getFirstDayOfWeek()); // Start from Sunday

        SimpleDateFormat dayFormat = new SimpleDateFormat("d", Locale.getDefault());
        SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("E", Locale.getDefault());

        for (int i = 0; i < 7; i++) {
            String dayOfWeek = dayOfWeekFormat.format(tempCalendar.getTime()).toUpperCase();
            String dayOfMonth = dayFormat.format(tempCalendar.getTime());
            boolean isSelected = tempCalendar.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH);
            // For testing, simulate tasks on certain days (you can replace this with real task data)
            boolean hasTask = tempCalendar.get(Calendar.DAY_OF_MONTH) % 2 == 0; // Example logic
            dayList.add(new DayItem(dayOfWeek, dayOfMonth, isSelected, hasTask));
            tempCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        dayAdapter.notifyDataSetChanged();
    }

    // Update the month/year TextView
    private void updateMonthYear() {
        SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        textMonthYear.setText(monthYearFormat.format(calendar.getTime()).toUpperCase());
    }

    @Override
    public void onDateClick(int position) {
        // Update the calendar to the selected day
        Calendar tempCalendar = (Calendar) calendar.clone();
        tempCalendar.set(Calendar.DAY_OF_WEEK, tempCalendar.getFirstDayOfWeek());
        tempCalendar.add(Calendar.DAY_OF_MONTH, position);
        calendar.set(Calendar.DAY_OF_MONTH, tempCalendar.get(Calendar.DAY_OF_MONTH));
        // You can add logic here to refresh the tasks list for the selected day
    }
}