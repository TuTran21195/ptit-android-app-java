package com.example.todoapp;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.todoapp.ui.calendar.CalendarFragment;


public class MainActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Kiểm tra nếu Fragment chưa được thêm (tránh thêm lại khi xoay màn hình)
        if (savedInstanceState == null) {
            // Thêm CalendarFragment vào container
            CalendarFragment calendarFragment = new CalendarFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragment_container, calendarFragment);
            fragmentTransaction.commit();
        }
    }
}