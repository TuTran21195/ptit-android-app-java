# To Do App v2

A modern Android To-Do application with advanced productivity features, built using Room, LiveData, and Material Design.

## Features

- **Task Management**
  - Add, edit, and delete tasks with title, description, due date, priority, and categories.
  - Mark tasks as completed or active.
  - Bulk delete all tasks.

- **Category Management**
  - Create, edit, and delete custom categories.
  - Assign multiple categories to each task.

- **Reminders**
  - Set reminders for tasks with notification support.
  - Reminders persist across device reboots.

- **Focus Mode**
  - Set a focus timer for deep work sessions.
  - Animated countdown timer with sound notification on completion.
  - Overview of daily social app usage (YouTube, Gmail, Facebook, etc.), aggregated and visualized.

- **Advanced Filtering**
  - Filter tasks by title, description, due date, priority, reminder time, and category.
  - Flexible filter dialog for easy multi-criteria search.

- **Visual Priority Indicators**
  - Each task displays a colored bar: green (Low), amber (Medium), red (High) for quick priority recognition.

- **Material Design**
  - Clean, modern UI with Material Components and smooth user experience.

## Setup Instructions

1. **Clone the repository**
   ```sh
   git clone <repo-url>
   cd ToDoAppv2
   ```
2. **Open in Android Studio**
   - Open the project folder in Android Studio.
   - Let Gradle sync and download dependencies.
3. **Configure Google Services (optional)**
   - If using Firebase features, add your `google-services.json` to `app/`.
4. **Build and Run**
   - Connect your device or start an emulator.
   - Click Run (▶️) in Android Studio.

## Code Structure
- `app/src/main/java/com/example/todoappv2/` — Main activities and logic
- `app/src/main/java/com/example/todoappv2/adapter/` — RecyclerView adapters
- `app/src/main/java/com/example/todoappv2/model/` — Data models (Todo, Category, etc.)
- `app/src/main/java/com/example/todoappv2/dao/` — Room DAOs
- `app/src/main/java/com/example/todoappv2/repository/` — Data repositories
- `app/src/main/java/com/example/todoappv2/viewmodel/` — ViewModels
- `app/src/main/res/layout/` — XML layouts
- `app/src/main/res/values/` — Colors, strings, styles

## Screenshots
_Add screenshots here to showcase the UI and features._

## License
MIT or specify your license here.

## Project Structure & Architecture

The app follows a clean, modular Android architecture (MVVM) for maintainability and scalability:

```
app/
├── src/
│   └── main/
│       ├── java/com/example/todoappv2/
│       │   ├── adapter/         # RecyclerView adapters for tasks and categories
│       │   ├── dao/             # Room DAOs for database access
│       │   ├── model/           # Data models (Todo, Category, etc.)
│       │   ├── repository/      # Data repositories (abstract data sources)
│       │   ├── util/            # Utilities (reminders, notifications, AI, etc.)
│       │   ├── viewmodel/       # ViewModels for UI logic and LiveData
│       │   ├── MainActivity.java, AddTodoActivity.java, ... # Main screens
│       ├── res/
│       │   ├── layout/          # XML layouts for activities, dialogs, list items
│       │   ├── values/          # Colors, strings, styles
│       └── AndroidManifest.xml
└── build.gradle.kts
```

### Key Layers
- **Activities**: UI screens (task list, add/edit, focus mode, category management)
- **Adapters**: Bind data to RecyclerViews
- **Models**: Data classes for Room and app logic
- **DAOs**: Database queries and relations
- **Repositories**: Abstract data operations for ViewModels
- **ViewModels**: Expose LiveData and business logic to the UI
- **Utilities**: Reminders, notifications, AI/NLP helpers
- **Resources**: Layouts, colors, strings, styles

### Architecture Pattern
- **MVVM (Model-View-ViewModel)**
  - **Model**: Data classes and Room database
  - **View**: Activities and XML layouts
  - **ViewModel**: Exposes data to the UI, handles business logic
  - **Repository**: Mediates between ViewModel and data sources (Room, network, etc.)

This structure ensures separation of concerns, easy feature expansion, and maintainable code.

## Class & Database Design

### Main Data Models

- **Todo** (`todos` table)
  - `id` (int, PK, auto-generated)
  - `title` (String)
  - `description` (String)
  - `isCompleted` (boolean)
  - `createdAt` (Date)
  - `dueDate` (Date)
  - `priority` (int: 1=Low, 2=Medium, 3=High)
  - `hasReminder` (boolean)
  - `reminderTime` (Date, nullable)

- **Category** (`category_table`)
  - `id` (int, PK, auto-generated)
  - `name` (String)

- **TodoCategoryCrossRef** (`todo_category_cross_ref`)
  - `todoId` (int, FK → Todo.id)
  - `categoryId` (int, FK → Category.id)
  - Composite PK: (`todoId`, `categoryId`)

- **TodoWithCategories**
  - Represents a `Todo` with its associated `Category` list (many-to-many via `TodoCategoryCrossRef`).

### Room Database Schema

```
+-------------------+      +---------------------------+      +---------------------+
|     todos         |      |  todo_category_cross_ref  |      |   category_table    |
+-------------------+      +---------------------------+      +---------------------+
| id (PK)           |<-----| todoId (PK, FK)           |----->| id (PK)             |
| title             |      | categoryId (PK, FK)       |      | name                |
| description       |      +---------------------------+      +---------------------+
| isCompleted       |
| createdAt         |
| dueDate           |
| priority          |
| hasReminder       |
| reminderTime      |
+-------------------+
```

- **One Todo** can have multiple Categories.
- **One Category** can be assigned to multiple Todos.
- The cross-ref table enables this many-to-many relationship.

### DAOs
- `TodoDao`: CRUD for todos, manage cross-references, and complex queries (filtering, with categories, etc.)
- `CategoryDao`: CRUD for categories.
- `TodoCategoryCrossRefDao`: Manage cross-reference entries.

### Example Entity Classes (simplified)
```java
@Entity(tableName = "todos")
public class Todo { ... }

@Entity(tableName = "category_table")
public class Category { ... }

@Entity(tableName = "todo_category_cross_ref", primaryKeys = {"todoId", "categoryId"})
public class TodoCategoryCrossRef { ... }
```

See `/model/` and `/dao/` for full details.
