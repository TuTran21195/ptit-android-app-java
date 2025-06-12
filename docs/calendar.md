# Luồng Calendar: Hiển thị và xử lý lịch trong ứng dụng

## 1. Người dùng click vào menu Calendar
- **File:** `app/src/main/res/menu/menu_main.xml`
  - Khai báo item menu `action_calendar`.

## 2. MainActivity xử lý sự kiện click
- **File:** `app/src/main/java/com/example/todoappv2/MainActivity.java`
- **Phương thức:** `onOptionsItemSelected(MenuItem item)`
  - Đoạn code:
    ```java
    if (item.getItemId() == R.id.action_calendar) {
        startActivity(new Intent(MainActivity.this, CalendarActivity.class));
        return true;
    }
    ```
- **Ý nghĩa:** Khi click vào menu Calendar, MainActivity sẽ **khởi động CalendarActivity**.

## 3. CalendarActivity được khởi động
- **File:** `app/src/main/java/com/example/todoappv2/CalendarActivity.java`
- **Class:** `CalendarActivity`
- **Phương thức:** `onCreate(Bundle savedInstanceState)`
  - Được gọi khi Activity khởi tạo.
  - Giao diện được set từ file layout: `activity_calendar.xml`.

## 4. CalendarActivity khởi tạo giao diện và dữ liệu
### 4.1. Khởi tạo View và Adapter
- **Phương thức:** `initializeViews()`
  - Ánh xạ các view từ layout: RecyclerView, Button, TextView,...
- **Phương thức:** `setupRecyclerViews()`
  - Khởi tạo `CalendarAdapter` (cho lịch tuần) và `TodoAdapter` (cho danh sách công việc).
  - Gán adapter cho RecyclerView.

### 4.2. Thiết lập sự kiện click
- **Phương thức:** `setupClickListeners()`
  - Gán sự kiện cho các nút chuyển tuần (`btnPreviousWeek`, `btnNextWeek`), nút lọc (`btnActiveTask`, `btnCompletedTask`).

### 4.3. Quan sát dữ liệu công việc
- **Phương thức:** `onCreate` (trực tiếp)
  - Đăng ký observer với repository:
    ```java
    todoRepository.getAllTodos().observe(this, todosWithCategories -> {
        allTodos = todosWithCategories;
        loadCurrentWeek(); // Reload để cập nhật chấm tròn trên lịch
        updateTaskList();  // Cập nhật danh sách công việc
    });
    ```

## 5. Xử lý logic lịch và công việc
### 5.1. Hiển thị tuần hiện tại
- **Phương thức:** `loadCurrentWeek()`
  - Tạo danh sách 7 ngày cho tuần hiện tại.
  - Đánh dấu ngày hôm nay nếu nằm trong tuần.
  - Cập nhật tiêu đề tháng/năm (`tvMonthYear`).
  - Gọi `calendarAdapter.updateData(days)` để cập nhật lịch.
  - Nếu chưa có ngày nào được chọn, tự động chọn ngày đầu tuần.

### 5.2. Chọn ngày trên lịch
- **File:** `CalendarAdapter.java`
- **Class:** `CalendarAdapter.CalendarViewHolder`
- **Phương thức:** `itemView.setOnClickListener`
  - Khi click vào một ngày, gọi callback `listener.onDayClick(day, position)`.
- **File:** `CalendarActivity.java`
- **Phương thức:** `onDayClick(CalendarDay day, int position)`
  - Đánh dấu ngày được chọn.
  - Gọi `updateTaskList()` để cập nhật danh sách công việc.

### 5.3. Lọc công việc theo trạng thái
- **Phương thức:** `updateTaskList()`
  - Lấy ngày đang chọn.
  - Lọc danh sách công việc theo ngày và trạng thái (đang hoạt động/đã hoàn thành).
  - Gọi `todoAdapter.submitList(filteredTasks)` để cập nhật UI.

### 5.4. Chuyển tuần
- **Phương thức:** `setupClickListeners()`
  - Khi click nút chuyển tuần, cập nhật biến `currentWeek` và gọi lại `loadCurrentWeek()`.

## 6. Tóm tắt các file, class, phương thức chính
| File/Resource                                      | Class/Function/Resource         | Vai trò                                                                 |
|----------------------------------------------------|---------------------------------|-------------------------------------------------------------------------|
| `menu_main.xml`                                    | `<item id="action_calendar">`   | Định nghĩa menu Calendar                                                |
| `MainActivity.java`                                | `onOptionsItemSelected`         | Xử lý click menu, mở CalendarActivity                                   |
| `CalendarActivity.java`                            | `onCreate`                      | Khởi tạo giao diện, adapter, observer dữ liệu                           |
| `CalendarActivity.java`                            | `initializeViews`               | Ánh xạ view từ layout                                                   |
| `CalendarActivity.java`                            | `setupRecyclerViews`            | Khởi tạo adapter, gán cho RecyclerView                                  |
| `CalendarActivity.java`                            | `setupClickListeners`           | Gán sự kiện cho các nút, ngày                                           |
| `CalendarActivity.java`                            | `loadCurrentWeek`               | Tạo danh sách ngày trong tuần, cập nhật UI                              |
| `CalendarActivity.java`                            | `onDayClick`                    | Xử lý khi chọn ngày, cập nhật danh sách công việc                       |
| `CalendarActivity.java`                            | `updateTaskList`                | Lọc và cập nhật danh sách công việc theo ngày và trạng thái             |
| `CalendarAdapter.java`                             | `CalendarViewHolder`            | Xử lý click vào ngày, gọi callback về CalendarActivity                  |
| `activity_calendar.xml`                            | Layout                          | Giao diện CalendarActivity                                              |

## 7. Sơ đồ luồng tổng quát
```
[User click action_calendar]
    |
    v
[MainActivity.onOptionsItemSelected]
    |
    v
[startActivity(CalendarActivity)]
    |
    v
[CalendarActivity.onCreate]
    |
    v
[initializeViews, setupRecyclerViews, setupClickListeners]
    |
    v
[Quan sát dữ liệu từ repository]
    |
    v
[loadCurrentWeek] <--- [Chuyển tuần]
    |
    v
[Chọn ngày] ---> [onDayClick] ---> [updateTaskList]
    |
    v
[Hiển thị danh sách công việc theo ngày & trạng thái]
```

---

**Nếu bạn muốn chi tiết code từng hàm hoặc giải thích sâu hơn về một bước nào, hãy nói rõ nhé!** 