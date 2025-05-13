package com.example.todoappv2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoappv2.R;
import com.example.todoappv2.model.Category;
import com.example.todoappv2.model.TodoWithCategories;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class TodoAdapter extends ListAdapter<TodoWithCategories, TodoAdapter.TodoViewHolder> {
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private OnCheckBoxClickListener onCheckBoxClickListener;
    private OnDeleteClickListener onDeleteClickListener;
    private SimpleDateFormat dateFormat;

    public TodoAdapter() {
        super(DIFF_CALLBACK);
        dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    }

    private static final DiffUtil.ItemCallback<TodoWithCategories> DIFF_CALLBACK = new DiffUtil.ItemCallback<TodoWithCategories>() {
        @Override
        public boolean areItemsTheSame(@NonNull TodoWithCategories oldItem, @NonNull TodoWithCategories newItem) {
            return oldItem.getTodo().getId() == newItem.getTodo().getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull TodoWithCategories oldItem, @NonNull TodoWithCategories newItem) {
            return oldItem.getTodo().equals(newItem.getTodo()) &&
                   oldItem.getCategories().equals(newItem.getCategories());
        }
    };

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_todo, parent, false);
        return new TodoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        TodoWithCategories current = getItem(position);
        holder.textTitle.setText(current.getTodo().getTitle());
        holder.textDescription.setText(current.getTodo().getDescription());
        holder.textDueDate.setText(dateFormat.format(current.getTodo().getDueDate()));
        holder.checkBox.setChecked(current.getTodo().isCompleted());

        // Set priority indicator color
        int priority = current.getTodo().getPriority();
        int colorRes;
        switch (priority) {
            case 3: colorRes = R.color.priority_high; break;
            case 2: colorRes = R.color.priority_medium; break;
            default: colorRes = R.color.priority_low; break;
        }
        holder.priorityIndicator.setBackgroundResource(colorRes);

        // Clear existing chips
        holder.chipGroupCategories.removeAllViews();

        // Add category chips
        for (Category category : current.getCategories()) {
            Chip chip = new Chip(holder.itemView.getContext());
            chip.setText(category.getName());
            chip.setCloseIconVisible(false);
            holder.chipGroupCategories.addView(chip);
        }
    }

    public TodoWithCategories getTodoAt(int position) {
        return getItem(position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
    }

    public void setOnCheckBoxClickListener(OnCheckBoxClickListener listener) {
        this.onCheckBoxClickListener = listener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }

    class TodoViewHolder extends RecyclerView.ViewHolder {
        private TextView textTitle;
        private TextView textDescription;
        private TextView textDueDate;
        private CheckBox checkBox;
        private ChipGroup chipGroupCategories;
        private ImageButton buttonDelete;
        private View priorityIndicator;

        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.text_title);
            textDescription = itemView.findViewById(R.id.text_description);
            textDueDate = itemView.findViewById(R.id.text_due_date);
            checkBox = itemView.findViewById(R.id.checkBox);
            chipGroupCategories = itemView.findViewById(R.id.chip_group_categories);
            buttonDelete = itemView.findViewById(R.id.button_delete);
            priorityIndicator = itemView.findViewById(R.id.priority_indicator);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onItemClickListener != null) {
                    onItemClickListener.onItemClick(getItem(position));
                }
            });

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onItemLongClickListener != null) {
                    return onItemLongClickListener.onItemLongClick(getItem(position));
                }
                return false;
            });

            checkBox.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onCheckBoxClickListener != null) {
                    onCheckBoxClickListener.onCheckBoxClick(getItem(position));
                }
            });

            buttonDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onDeleteClickListener != null) {
                    onDeleteClickListener.onDeleteClick(getItem(position));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(TodoWithCategories todoWithCategories);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(TodoWithCategories todoWithCategories);
    }

    public interface OnCheckBoxClickListener {
        void onCheckBoxClick(TodoWithCategories todoWithCategories);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(TodoWithCategories todoWithCategories);
    }
} 