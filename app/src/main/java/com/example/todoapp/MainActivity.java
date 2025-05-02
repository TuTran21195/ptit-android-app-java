package com.example.todoapp;


import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.todoapp.ui.calendar.CalendarFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

            //setOnNavigationItemSelectedListener: Xử lý sự kiện khi người dùng chọn một item trong BottomNavigationView.
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            // Chọn Fragment dựa trên itemId của BottomNavigationView
            if (itemId == R.id.navigation_calendar) {
                selectedFragment = new CalendarFragment();
            }
            // Placeholder cho các Fragment khác (Home, add, focus, profile)
            else if (itemId == R.id.navigation_home ||
                    itemId == R.id.navigation_add ||
                    itemId == R.id.navigation_focus ||
                    itemId == R.id.navigation_setting) {
                // Handle other fragments here if needed
            } else {
                selectedFragment = new CalendarFragment(); // Mặc định hiển thị Calendar
            }

            // Thay đổi Fragment: replace thay thế Fragment trong fragment_container khi người dùng chuyển đổi.
            if (selectedFragment != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, selectedFragment);
                fragmentTransaction.commit();
            }
            return true;
        });

        // Đặt CalendarFragment làm mặc định khi khởi động
        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, new CalendarFragment());
            fragmentTransaction.commit();
        }
    }
}