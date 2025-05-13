package com.example.todoappv2.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.example.todoappv2.dao.CategoryDao;
import com.example.todoappv2.dao.TodoDao;
import com.example.todoappv2.model.Category;
import com.example.todoappv2.model.Todo;
import com.example.todoappv2.util.DateConverter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Todo.class, Category.class}, version = 5)
@TypeConverters({DateConverter.class})
public abstract class TodoDatabase extends RoomDatabase {
    private static TodoDatabase instance;
    private static final ExecutorService databaseWriteExecutor = Executors.newSingleThreadExecutor();
    public abstract TodoDao todoDao();
    public abstract CategoryDao categoryDao();

    private static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Add hasReminder column with default value false
            database.execSQL("ALTER TABLE todos ADD COLUMN hasReminder INTEGER NOT NULL DEFAULT 0");
            // Add reminderTime column with default value null
            database.execSQL("ALTER TABLE todos ADD COLUMN reminderTime INTEGER DEFAULT NULL");
        }
    };

    public static synchronized TodoDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            TodoDatabase.class,
                            "todo_database"
                    )
                    .addMigrations(MIGRATION_4_5)
                    .build();
        }
        return instance;
    }

    public static ExecutorService getDatabaseWriteExecutor() {
        return databaseWriteExecutor;
    }
} 