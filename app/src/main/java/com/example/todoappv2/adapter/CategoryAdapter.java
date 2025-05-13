package com.example.todoappv2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoappv2.R;
import com.example.todoappv2.model.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<Category> categories;
    private OnCategoryEditListener editListener;
    private OnCategoryDeleteListener deleteListener;

    public interface OnCategoryEditListener {
        void onEdit(Category category);
    }

    public interface OnCategoryDeleteListener {
        void onDelete(Category category);
    }

    public CategoryAdapter(List<Category> categories, OnCategoryEditListener editListener, OnCategoryDeleteListener deleteListener) {
        this.categories = categories;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView textCategoryName;
        private ImageButton buttonEdit;
        private ImageButton buttonDelete;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textCategoryName = itemView.findViewById(R.id.text_category_name);
            buttonEdit = itemView.findViewById(R.id.button_edit);
            buttonDelete = itemView.findViewById(R.id.button_delete);
        }

        void bind(Category category) {
            textCategoryName.setText(category.getName());
            buttonEdit.setOnClickListener(v -> editListener.onEdit(category));
            buttonDelete.setOnClickListener(v -> deleteListener.onDelete(category));
        }
    }
} 