package com.example.todoappv2.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.todoappv2.util.NotificationHelper;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("todo_title");
        String description = intent.getStringExtra("todo_description");
        int todoId = intent.getIntExtra("todo_id", -1);

        if (todoId != -1) {
            // Format the notification message
            String notificationMessage = "Task: " + title + "\n" +
                                      "Description: " + description + "\n" +
                                      "Time: " + new SimpleDateFormat("HH:mm", Locale.getDefault()).format(System.currentTimeMillis());

            NotificationHelper.showNotification(context, title, notificationMessage, todoId);
        }
    }
} 