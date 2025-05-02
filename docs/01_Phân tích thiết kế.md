Design: https://www.figma.com/design/honIMLkhU5APnhZqnzSuJR/UpTodo---Todo-list-app-UI-Kit--Community-?node-id=0-1&p=f&t=zeekymasuXxDVX1J-0

# **Quan sát thiết kế (CORE)**

## 📱 **Mapping giữa thiết kế Figma và Fragment trong Android**

| **Fragment / Activity** | **Tên màn hình trong Figma**                       | **Gợi ý tên class Fragment**              | **Mục đích / Ghi chú**                                                      |
| ----------------------- | -------------------------------------------------- | ----------------------------------------- | --------------------------------------------------------------------------- |
| **Onboarding**          | `Onboarding 01 → 03`                               | `OnboardingFragment`                      | Giới thiệu ứng dụng (3 slide đầu tiên)                                      |
| **Login**               | `Login`                                            | `LoginFragment`                           | Đăng nhập                                                                   |
| **Register**            | `Register`                                         | `RegisterFragment`                        | Đăng ký tài khoản                                                           |
| **Home (Task List)**    | `Home - Empty`, `Home - With Tasks`, `Home - Menu` | `HomeFragment`                            | Danh sách công việc theo ngày, có thanh tab “Today”, “Upcoming”, v.v.       |
| **Calendar**            | `Calendar`                                         | `CalendarFragment`                        | Hiển thị lịch tháng, cho phép xem task theo ngày                            |
| **Add New Task**        | `Create Task`                                      | `AddTaskFragment`                         | Giao diện thêm task mới với các lựa chọn như thời gian, nhãn, độ ưu tiên... |
| **Task Detail / Edit**  | `Task Detail`, `Edit Task`                         | `TaskDetailFragment`                      | Hiển thị chi tiết công việc, có thể sửa hoặc đánh dấu hoàn thành            |
| **Search**              | `Search Task`                                      | `SearchFragment`                          | Tìm kiếm task                                                               |
| **Settings / Profile**  | `Settings`, `Profile`, `Theme`                     | `SettingsFragment` hoặc `ProfileFragment` | Thay đổi theme, cấu hình, profile cá nhân                                   |
>[!warning]
>Phần search có hai chỗ để search: search cho task ở trang index screen (Home) và trang Catergory screen.

---

## 🌐 **Thanh điều hướng chính (Bottom Navigation Bar)**

Figma có thanh điều hướng cố định với 4 biểu tượng:

- 🏠 **Home**
    
- 📅 **Calendar**
    
- ➕ **Add task** (nút nổi chính giữa)
    
- 🔍 **Search**
    
- ⚙️ **Settings/Profile**
    

## 🧩 **Phân tích thiết kế - Cấu trúc hệ thống tổng quan**

### 📦 1. **Các màn hình chính (Screens)**

Từ Figma, các màn hình (fragment/activity) chính có thể bao gồm:

- **Onboarding screens**
    
- **Login / Register**
    
- **Home (Task list theo ngày)**
    
- **Calendar view**
    
- **Add new task**
    
- **Task detail**
    
- **Search**
    
- **Settings/Profile**
    

---

### 🧱 2. **Class/Model cơ bản cần có**

```java
class User {
    String id;
    String username;
    String email;
    String passwordHash;
    // Optional: avatar, theme, notification settings
}

class Task {
	String id;  
	String userId; // Foreign Key  
	String title;  
	String description;  
	Date dueDate;  
	Time dueTime;  
	int priority; // từ 1 -> 10 để đánh dấu mức độ ưu tiên theo đúng thiết kế Figma  
	boolean isCompleted;  
	String tagID; // Foreign Key : Catergory table
	  
	List<String> subTaskIds; // Foreign Key  
	boolean isReminderSet;
}

class Tag {
    String id;
    String name;
    String userId; // mỗi người có list tag riêng (list category riêng)
}
```

---

### 🗃️ 3. **Cấu trúc bảng Database (SQLite hoặc backend nếu có)**

|Table: users|
|---|
|id (PK)|
|username|
|email|
|password_hash|
|...|

|Table: tasks|
|---|
|id (PK)|
|user_id (FK)|
|title|
|description|
|due_date|
|due_time|
|is_completed|
|priority|
|is_reminder_set|

|Table: tags|
|---|
|id (PK)|
|user_id (FK)|
|name|

|Table: task_tags (n-n)|
|---|
|task_id (FK)|
|tag_id (FK)|

---

### 🌐 4. **Các API cần có (nếu dùng backend)**

| Endpoint          | Method     | Mô tả                              |
| ----------------- | ---------- | ---------------------------------- |
| `/api/register`   | POST       | Đăng ký                            |
| `/api/login`      | POST       | Đăng nhập                          |
| `/api/tasks`      | GET/POST   | Lấy danh sách task / thêm task     |
| `/api/tasks/{id}` | PUT/DELETE | Cập nhật / Xoá task                |
| `/api/tags`       | GET/POST   | Lấy danh sách tag / tạo tag        |
| `/api/calendar`   | GET        | Lấy danh sách task theo ngày/tháng |
| `/api/profile`    | GET/PUT    | Lấy/Cập nhật thông tin cá nhân     |

### 📂 Tổ chức thư mục (chuẩn MVVM)

```
app/
├── data/
│   ├── model/            # User, Task, Tag...
│   ├── repository/       # Xử lý gọi API hoặc local DB
├── ui/
│   ├── calendar/         # Màn hình mà thành viên phụ trách
│   │   ├── CalendarFragment.java
│   │   ├── CalendarViewModel.java
│   │   └── CalendarAdapter.java
│   ├── home/
│   ├── addtask/
│   ├── login/
│   ├── setting/
│   └── ...
├── utils/                # DateUtils, NotificationUtils, Constants...
├── network/              # Retrofit, API service
```

---


# Thứ tự triển khai (OPTIONAL READING)

Việc bạn chủ động tìm hiểu **quy trình phát triển** trước khi bắt tay vào thiết kế là cực kỳ đúng đắn. Trong một ứng dụng như **todo app** có nhiều màn hình và chức năng liên kết như bạn liệt kê, việc xác định **thứ tự phát triển hợp lý** giúp nhóm dễ phối hợp, kiểm thử và mở rộng sau này.

## ✅ Gợi ý quy trình phát triển theo từng giai đoạn (dành cho nhóm)

### 🎯 **Giai đoạn 1: Thiết lập khung nền (Foundation)**

- ✅ Thiết lập kiến trúc ứng dụng: MVVM hoặc MVP
    
- ✅ Xây `BottomNavigationView` + Navigation Component (fragment chuyển động mượt)
    
- ✅ Chuẩn bị model dữ liệu (`Task`, `User`, ...)
    
- ✅ Tạo mock data để hiển thị thử trên UI
    

> 💡 Mục tiêu: Chạy được khung ứng dụng, chuyển được qua lại giữa các màn

---

### 🧑‍💻 **Giai đoạn 2: Các màn hình cốt lõi (Core Screens)**

|Ưu tiên|Màn hình|Vì sao làm trước?|
|---|---|---|
|🔹 1|**Home (Task List)**|Nơi chính người dùng xem task mỗi ngày|
|🔹 2|**Add Task**|Gắn liền với mọi flow (Home, Calendar, v.v.)|
|🔹 3|**Calendar**|Yêu cầu có `task list`, nên làm sau Home/AddTask|
|🔹 4|**Task Detail / Edit**|Sau khi có thể hiển thị và thêm task|
|🔹 5|**Search**|Dùng được khi task đã nhiều|
|🔹 6|**Settings / Profile**|Không ảnh hưởng logic chính|
|🔹 7|**Login / Register / Onboarding**|Để cuối cùng vì backend có thể chưa sẵn, hoặc login giả lập được|

> 💡 Giai đoạn này nên làm theo hướng: **Home ↔ Add ↔ Calendar** trước → sau đó mở rộng ra Detail/Search/Profile.

---

### ⚙️ **Giai đoạn 3: Dữ liệu & backend**

- Kết nối Room database hoặc Firebase (nếu cần sync đa thiết bị)
    
- Tạo Repository để quản lý data theo cách thống nhất
    
- Dùng ViewModel + LiveData (hoặc StateFlow) để cập nhật UI tự động
    

## 📁 Tổ chức công việc nhóm nên như sau:

- Mỗi người 1 màn chính (Home / Add / Calendar / Search ...)
    
- Cùng sử dụng `TaskRepository`, `TaskViewModel` chung để dùng lại data logic
    
- Merge code vào develop branch, dùng fragment container hoặc navigation component để kết nối
    
