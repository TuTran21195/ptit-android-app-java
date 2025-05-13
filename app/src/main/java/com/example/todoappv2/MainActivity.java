package com.example.todoappv2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            return true;
        });


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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteAllConfirmationDialog() {
        new AlertDialog.Builder(this)
            .setTitle(R.string.delete_all)
            .setMessage(R.string.delete_all_confirmation)
            .setPositiveButton(R.string.delete, (dialog, which) -> {
                Toast.makeText(this, R.string.all_todos_deleted, Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton(R.string.cancel, null)
            .show();
    }

}