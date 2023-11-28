> Sinh viên: Nguyễn Đức Thắng 63 CNTT-CLC 2
 ## Install:
 - Android Studio
 - Android 7.0 (Nougat API 24)
 - Java SE Development Kit (8u77)

***Quá trình thực hiện bài tập***
 #### Preview
 ![image](https://github.com/Mrk4tsu/Mobile-Dev/blob/FirebaseGeneral/Preview/file.gif)

#### Lấy dữ liệu và in ra màn hình
- Sử dụng RecycleView để hiển thị danh sách dữ liệu lấy từ database
- Sử dụng 1 HashTable để quản lý dữ liệu theo dạng [key][value]
```java
    public HashMap<String, String> toFireBaseOB() {
        HashMap<String, String> taskObject = new HashMap<>();
        taskObject.put("name", name);
        taskObject.put("date", date);
        taskObject.put("message", message);
        taskObject.put("priority", priority);
        return taskObject;
    }
```
- Sử dụng 1 class Adapter, gán dữ liệu vào từng item của dữ liệu
```java
@Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        TaskItemViewHolder viewHolder = (TaskItemViewHolder) holder;
        viewHolder.position = position;
        TASKS task = dataSource.get(position);
        ((TaskItemViewHolder) holder).name.setText(task.getName());
        ((TaskItemViewHolder) holder).message.setText(task.getMessage());
        ((TaskItemViewHolder) holder).priority.setText(task.getPriority());
        ((TaskItemViewHolder) holder).date.setText(task.getDate());
    }
```
