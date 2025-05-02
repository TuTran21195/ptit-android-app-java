package com.example.todoapp.ui.calendar;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.todoapp.R;
import com.example.todoapp.model.DayItem;
import com.example.todoapp.model.Task;

import java.sql.Time;
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
    private Calendar displayCalendar; // Quản lý TUẦN hiển thị
    private Calendar selectedDate; // Lưu ngày được chọn

    // Task recycle view    
    private TextView btnTasks;
    private RecyclerView recyclerTasks;
    private CSTaskAdapter taskAdapter;
    private List<Task> taskList;
    

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
        btnTasks = view.findViewById(R.id.btn_cs_tasks);
        recyclerTasks = view.findViewById(R.id.recycler_tasks);



        // Initialize calendar and day list
        calendar = Calendar.getInstance(); // Sử dụng thời gian hiện tại
        displayCalendar = Calendar.getInstance(); // Thời gian hiện tại để hiển thị tuần
        selectedDate = Calendar.getInstance(); // Ngày được chọn, mặc định khi mới load thì ngày được chọn là hôm nay

        dayList = new ArrayList<>();    // khởi tạo danh sách ngày (nó chỉ cần lưu 7 ngày từ t2 -> cn thôi vì đây là recycle view có 7 ô thôi)
        taskList = new ArrayList<>(); // Khởi tạo danh sách task

        // Set up RecyclerView for days
        recyclerDays.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        dayAdapter = new CalendarAdapter(getContext(), dayList, this);
        recyclerDays.setAdapter(dayAdapter);

        // Set up RecyclerView for tasks
        recyclerTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        taskAdapter = new CSTaskAdapter(getContext(), new ArrayList<>()); // Ban đầu không có task hiển thị
        recyclerTasks.setAdapter(taskAdapter);

        // Load initial week (hardcoded for testing  hasTask dot)
//        loadHardcodedWeek();

        // Load hardcoded tasks
        loadHardcodedTasks();

        // Load current week based on system time
        loadWeekDays();


        // Update month/year display
        updateMonthYear();

        // Set up button listeners
        btnPreviousWeek.setOnClickListener(v -> {
            displayCalendar.add(Calendar.WEEK_OF_YEAR, -1); // Chỉ thay đổi tuần hiển thị
            loadWeekDays();
            updateMonthYear();
        });

        btnNextWeek.setOnClickListener(v -> {
            displayCalendar.add(Calendar.WEEK_OF_YEAR, 1); // Chỉ thay đổi tuần hiển thị
            loadWeekDays();
            updateMonthYear();
        });

        btnTasks.setOnClickListener(v -> updateTasks()); // Cập nhật task khi nhấn nút

        return view;
    }

    // Load days of the current week dynamically
    private void loadWeekDays() {
        dayList.clear();
        Calendar tempCalendar = (Calendar) displayCalendar.clone();

//        tempCalendar.set(Calendar.DAY_OF_WEEK, tempCalendar.getFirstDayOfWeek()); // Start from Sunday
        tempCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // Bắt đầu từ Thứ Hai

        SimpleDateFormat dayFormat = new SimpleDateFormat("d", Locale.getDefault());
        SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("E", Locale.getDefault());

        for (int i = 0; i < 7; i++) {
            String dayOfWeek = dayOfWeekFormat.format(tempCalendar.getTime()).toUpperCase();
            String dayOfMonth = dayFormat.format(tempCalendar.getTime());
            // So sánh ngày chính xác (năm, tháng, ngày)
            // Lấy ngày hôm nay ra để xem có khớp với ngày trong calendar được hiển thị kooong, nếu khớp thì highlight ngày hôm nay
            String tempDateString = dayFormat.format(tempCalendar.getTime());
            String selectedDateString = dayFormat.format(selectedDate.getTime());
            // so xem đúng ngày
            boolean isSelected = tempDateString.equals(selectedDateString);
            // so xem đúng tháng
            isSelected = isSelected && tempCalendar.get(Calendar.MONTH) == selectedDate.get(Calendar.MONTH);
            // so xem đúng năm
            isSelected = isSelected && tempCalendar.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR);

            // log ra ngày tháng năm đang được chọn để dễ debug: selectedDate.get(Calendar.MONTH) nó sẽ tính từ tháng 0 -> cần +1 để in ra tháng đúng
            Log.d("trmy", "Ngày được người dùng/auto chọn là selectedDate: " + selectedDate.getTime());

            boolean hasTask = false; // Sẽ cập nhật dựa trên taskList
            dayList.add(new DayItem(dayOfWeek, dayOfMonth, isSelected, hasTask));
            tempCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        // Cập nhật hasTask cho các ngày
        updateHasTaskForDays();
        dayAdapter.notifyDataSetChanged();
    }

    // Update the month/year TextView
    private void updateMonthYear() {
        SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        textMonthYear.setText(monthYearFormat.format(displayCalendar.getTime()).toUpperCase());
    }

    private void loadHardcodedTasks() {
        // Tạo một số task cứng
        Calendar cal = Calendar.getInstance();

        // 27/04/2025 10:00
        cal.set(2025, Calendar.APRIL, 27, 10, 0);
        Task task = new Task("0", "user1", "Prepare for presentation", "Gather slides and rehearse",
                cal.getTime(), new Time(System.currentTimeMillis()), 10, false, "Work", new ArrayList<>(), true);

        // 29/04/2025 14:30
        cal.set(2025, Calendar.APRIL, 29, 14, 30);
        Task task0 = new Task("0", "user1", "Prepare for presentation", "Gather slides and rehearse",
                cal.getTime(), new Time(System.currentTimeMillis()), 10, false, "Work", new ArrayList<>(), true);

        cal.set(2025, Calendar.APRIL, 30, 16, 45); // 30/04/2025 16:45
        Task task1 = new Task("1", "user1", "Do Math Homework", "Complete math exercises",
                cal.getTime(), new Time(System.currentTimeMillis()), 7, false, "University", new ArrayList<>(), false);

        cal.set(2025, Calendar.MAY, 2, 18, 20); // 02/05/2025 18:20
        Task task2 = new Task("2", "user1", "Tack out dogs", "Take dogs for a walk",
                cal.getTime(), new Time(System.currentTimeMillis()), 4, false, "Home", new ArrayList<>(), true);

        cal.set(2025, Calendar.MAY, 2, 8, 15); // 02/05/2025 08:15
        Task task3 = new Task("3", "user1", "Business meeting with CEO", "Prepare presentation",
                cal.getTime(), new Time(System.currentTimeMillis()), 9, false, "Work", new ArrayList<>(), false);

        // 03/05/2025 10:00
        cal.set(2025, Calendar.MAY, 3, 10, 0);
        Task task4 = new Task("4", "user1", "Buy groceries", "Buy vegetables and fruits for the week",
                cal.getTime(), new Time(System.currentTimeMillis()), 10, false, "Shopping", new ArrayList<>(), true);
        // 07/05/2025 11:00
        cal.set(2025, Calendar.MAY, 7, 11, 0);
        Task task5 = new Task("5", "user1", "Dentist appointment", "Check teeth",
                cal.getTime(), new Time(System.currentTimeMillis()), 8, false, "Health", new ArrayList<>(), false);



        // Thêm task vào danh sách
        taskList.add(task);
        taskList.add(task0);
        taskList.add(task1);
        Log.d("trmy", "Add hardcode Task 1: " + task1.getTitle() + ", Due Date: " + task1.getDueDate());

        taskList.add(task2);
        taskList.add(task3);
        taskList.add(task4);
        taskList.add(task5);

        // 08/05/2025 12:00
        cal.set(2025, Calendar.MAY, 8, 12, 0);
        taskList.add(new Task("6", "user1", "Gym workout", "Chest and back",
                cal.getTime(), new Time(System.currentTimeMillis()), 7, false, "Health", new ArrayList<>(), true));

        // 09/05/2025 13:00
        cal.set(2025, Calendar.MAY, 9, 13, 0);
        taskList.add(new Task("7", "user1", "Family dinner", "Cook dinner for family",
                cal.getTime(), new Time(System.currentTimeMillis()), 5, false, "Home", new ArrayList<>(), false));

        // 10/05/2025 14:00
        cal.set(2025, Calendar.MAY, 10, 14, 0);
        taskList.add(new Task("8", "user1", "Project meeting", "Discuss project progress",
                cal.getTime(), new Time(System.currentTimeMillis()), 9, false, "Work", new ArrayList<>(), true));

        // 26/04/2025 15:00
        cal.set(2025, Calendar.APRIL, 26, 15, 0);
        taskList.add(new Task("9", "user1", "Read book", "Read 'The Great Gatsby'",
                cal.getTime(), new Time(System.currentTimeMillis()), 6, false, "Leisure", new ArrayList<>(), false));


    }

    private void updateTasks() {
        if (selectedDate == null) return;
        Calendar selectedCal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd", Locale.getDefault());
        int selectedDayOfMonth = selectedDate.get(Calendar.DAY_OF_MONTH);
        Log.d("trmy", "Ngày được chọn fomart string: " + dateFormat.format(selectedDate.getTime()));
        Log.d("trmy", "Ngày được chọn fomart int: " + selectedDayOfMonth);
        selectedCal.set(selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDayOfMonth);

        List<Task> filteredTasks = new ArrayList<>();
        // Lọc task dựa trên ngày được chọn
        Calendar taskCal = Calendar.getInstance();
        for (Task task : taskList) {
            taskCal.setTime(task.getDueDate());
            if (taskCal.get(Calendar.DAY_OF_MONTH) == selectedCal.get(Calendar.DAY_OF_MONTH) &&
                    taskCal.get(Calendar.MONTH) == selectedCal.get(Calendar.MONTH) &&
                    taskCal.get(Calendar.YEAR) == selectedCal.get(Calendar.YEAR)) {
                filteredTasks.add(task);
            }
        }
        // log so khơp với ngày tháng sau:
        Log.d("trmy", "Ngày được chọn: " + selectedCal.get(Calendar.DAY_OF_MONTH) + "/" + (selectedCal.get(Calendar.MONTH) + 1) + "/" + selectedCal.get(Calendar.YEAR));

        // Cập nhật taskAdapter với danh sách task đã lọc
        taskAdapter = new CSTaskAdapter(getContext(), filteredTasks);
        recyclerTasks.setAdapter(taskAdapter);
        taskAdapter.notifyDataSetChanged();
        //log ra danh sách task đã lọc
        Log.d("trmy", "Danh sách task đã lọc: " + filteredTasks.toString());
    }
    private void updateHasTaskForDays() {
        Calendar dayCal = Calendar.getInstance();
        Calendar taskCal = Calendar.getInstance();

        Log.d("trmy", "updateHasTaskForDays: Danh sách ngày để duyệt laanf lượt từng ngày xem ngày đó có task không: " + dayList.toString());

        for (DayItem day : dayList) {
            int dayOfMonth = Integer.parseInt(day.getDayOfMonth());
            dayCal.set(displayCalendar.get(Calendar.YEAR), displayCalendar.get(Calendar.MONTH), dayOfMonth);
            // log
            Log.d("trmy", "updateHasTaskForDays: Xét ngày :" + dayCal.getTime() + " - trong danh sách 7 ngày: displayCalendar.get(Calendar.YEAR)" + displayCalendar.get(Calendar.YEAR) + " - displayCalendar.get(Calendar.MONTH): " + displayCalendar.get(Calendar.MONTH));
            boolean hasTask = false;
            for (Task task : taskList) {
                taskCal.setTime(task.getDueDate());
                Log.d("trmy", "__Xét ngày trong danh sách:" + taskCal.get(Calendar.DAY_OF_MONTH) + "/" + taskCal.get(Calendar.MONTH) + "/" + taskCal.get(Calendar.YEAR) + " có trùng với dayCal: " + dayCal.get(Calendar.DAY_OF_MONTH) + "/" + dayCal.get(Calendar.MONTH) + "/" + dayCal.get(Calendar.YEAR) + " (___tức là ngày"+ dayCal.getTime()+ ") ?");
                if (taskCal.get(Calendar.DAY_OF_MONTH) == dayCal.get(Calendar.DAY_OF_MONTH) &&
                        taskCal.get(Calendar.MONTH) == dayCal.get(Calendar.MONTH) &&
                        taskCal.get(Calendar.YEAR) == dayCal.get(Calendar.YEAR)) {
                    hasTask = true;
                    Log.d("trmy", "updateHasTaskForDays: Ngày " + dayCal.getTime() + " có task: " + task.getTitle());
                    break;
                }
            }
            day.setHasTask(hasTask);
        }
    }



    @Override
    public void onDateClick(int position) {
        // Cập nhật ngày được chọn
        selectedDate = (Calendar) displayCalendar.clone();
        selectedDate.set(Calendar.DAY_OF_WEEK, selectedDate.getFirstDayOfWeek());
        selectedDate.add(Calendar.DAY_OF_MONTH, position);
        loadWeekDays(); // Tải lại danh sách để cập nhật highlight
        // You can add logic here to refresh the tasks list for the selected day
        Log.d("trmy", "Ngày được người dùng chọn: " + selectedDate.getTime() + ", vị trí thứ " + position + " trong ds 7 ngày đang được hiển thị");
        updateTasks(); // Cập nhật task cho ngày được chọn
    }
}