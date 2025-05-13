package com.example.todoappv2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoappv2.adapter.CategoryAdapter;
import com.example.todoappv2.model.Category;
import com.example.todoappv2.viewmodel.TodoViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class CategoryManagementActivity extends AppCompatActivity {
    private TodoViewModel todoViewModel;
    private CategoryAdapter categoryAdapter;
    private List<Category> categories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_management);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.manage_categories);

        // Initialize ViewModel
        todoViewModel = new ViewModelProvider(this).get(TodoViewModel.class);

        // Setup RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_view_categories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoryAdapter = new CategoryAdapter(categories, this::showEditCategoryDialog, this::showDeleteCategoryDialog);
        recyclerView.setAdapter(categoryAdapter);

        // Setup FAB
        FloatingActionButton fab = findViewById(R.id.fab_add_category);
        fab.setOnClickListener(v -> showAddCategoryDialog());

        // Observe categories
        todoViewModel.getAllCategories().observe(this, categoryList -> {
            categories.clear();
            categories.addAll(categoryList);
            categoryAdapter.notifyDataSetChanged();
        });
    }

    private void showAddCategoryDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_category, null);
        EditText editTextName = dialogView.findViewById(R.id.edit_text_category_name);

        new AlertDialog.Builder(this)
                .setTitle(R.string.add_category)
                .setView(dialogView)
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    String name = editTextName.getText().toString().trim();
                    if (!name.isEmpty()) {
                        Category category = new Category(name);
                        todoViewModel.insertCategory(category);
                    } else {
                        Toast.makeText(this, R.string.category_name_required, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showEditCategoryDialog(Category category) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_category, null);
        EditText editTextName = dialogView.findViewById(R.id.edit_text_category_name);
        editTextName.setText(category.getName());

        new AlertDialog.Builder(this)
                .setTitle(R.string.edit_category)
                .setView(dialogView)
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    String name = editTextName.getText().toString().trim();
                    if (!name.isEmpty()) {
                        category.setName(name);
                        todoViewModel.updateCategory(category);
                    } else {
                        Toast.makeText(this, R.string.category_name_required, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showDeleteCategoryDialog(Category category) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_category)
                .setMessage(getString(R.string.delete_category_confirmation, category.getName()))
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    todoViewModel.deleteCategory(category);
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
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