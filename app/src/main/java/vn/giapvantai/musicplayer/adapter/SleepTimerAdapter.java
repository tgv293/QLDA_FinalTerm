package vn.giapvantai.musicplayer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import vn.giapvantai.musicplayer.MPConstants;
import vn.giapvantai.musicplayer.R;
import vn.giapvantai.musicplayer.listener.MinuteSelectListener;

import com.google.android.material.button.MaterialButton;

import java.util.List;

public class SleepTimerAdapter extends RecyclerView.Adapter<SleepTimerAdapter.MyViewHolder> {

    private final List<Integer> minutes; // Danh sách các thời gian (phút) có sẵn
    private final MinuteSelectListener listener; // Listener để xử lý sự kiện chọn thời gian

    // Constructor để khởi tạo adapter
    public SleepTimerAdapter(MinuteSelectListener listener) {
        this.minutes = MPConstants.MINUTES_LIST; // Khởi tạo danh sách các thời gian từ hằng số
        this.listener = listener; // Khởi tạo listener để xử lý sự kiện chọn thời gian
    }

    // Tạo view mới (được gọi bởi layout manager)
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo view mới bằng cách inflate layout của từng item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_minutes, parent, false);
        return new MyViewHolder(view); // Trả về ViewHolder đã được tạo
    }

    // Gắn dữ liệu vào view (được gọi bởi layout manager)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Gắn dữ liệu vào ViewHolder tại vị trí cụ thể
        holder.bind(minutes.get(position));
    }

    // Trả về kích thước của dataset (được gọi bởi layout manager)
    @Override
    public int getItemCount() {
        return minutes.size(); // Trả về kích thước của danh sách thời gian
    }

    // Inner class đại diện cho view của từng item
    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final MaterialButton minuteButton; // Button để hiển thị thời gian (phút)

        // Constructor cho ViewHolder
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            minuteButton = itemView.findViewById(R.id.minutes_item); // Khởi tạo button

            // Đặt lắng nghe sự kiện khi click vào button để xử lý sự kiện chọn thời gian
            minuteButton.setOnClickListener(v -> listener.select(minutes.get(getAdapterPosition())));
        }

        // Gắn dữ liệu vào ViewHolder
        public void bind(int minute) {
            minuteButton.setText(String.valueOf(minute)); // Đặt text cho button là thời gian được chọn
        }
    }
}
