package vn.giapvantai.musicplayer.adapter;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import vn.giapvantai.musicplayer.MPConstants;
import vn.giapvantai.musicplayer.MPPreferences;
import vn.giapvantai.musicplayer.R;
import vn.giapvantai.musicplayer.helper.ThemeHelper;

import java.util.List;

public class AccentAdapter extends RecyclerView.Adapter<AccentAdapter.MyViewHolder> {

    private final Activity activity;
    private final List<Integer> accentList;

    public AccentAdapter(Activity activity) {
        // Khởi tạo danh sách màu sắc từ MPConstants
        this.accentList = MPConstants.ACCENT_LIST;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo view cho mỗi item trong RecyclerView từ layout XML
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_accent, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Thiết lập màu sắc cho ImageButton tương ứng với vị trí trong danh sách
        ImageViewCompat.setImageTintList(
                holder.accent,
                ColorStateList.valueOf(activity.getColor(accentList.get(position)))
        );

        // Hiển thị hoặc ẩn icon check tùy thuộc vào việc màu sắc có được chọn hay không
        if (accentList.get(position) == MPPreferences.getTheme(activity.getApplicationContext()))
            holder.check.setVisibility(View.VISIBLE);
        else
            holder.check.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        // Trả về số lượng màu sắc trong danh sách
        return accentList.size();
    }

    // ViewHolder chứa các thành phần giao diện của mỗi item trong RecyclerView
    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final ImageButton accent;
        private final ImageButton check;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            // Ánh xạ các thành phần giao diện từ layout XML
            accent = itemView.findViewById(R.id.accent);
            check = itemView.findViewById(R.id.check);

            // Thiết lập sự kiện click cho ImageButton
            accent.setOnClickListener(v -> {
                // Lưu trạng thái màu sắc đã chọn vào SharedPreferences
                MPPreferences.storeTheme(activity.getApplicationContext(), accentList.get(getAdapterPosition()));
                // Áp dụng cài đặt màu sắc cho ứng dụng
                ThemeHelper.applySettings(activity);
            });
        }
    }
}
