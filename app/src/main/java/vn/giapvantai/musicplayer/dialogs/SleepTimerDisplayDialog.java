package vn.giapvantai.musicplayer.dialogs;

import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import vn.giapvantai.musicplayer.R;
import vn.giapvantai.musicplayer.listener.SleepTimerSetListener;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class SleepTimerDisplayDialog extends BottomSheetDialog {

    private final SleepTimerSetListener sleepTimerSetListener;

    public SleepTimerDisplayDialog(@NonNull Context context, SleepTimerSetListener listener) {
        super(context);
        setContentView(R.layout.dialog_display_sleep_timer);

        // Lưu trữ listener để có thể tương tác với SleepTimerSetListener
        this.sleepTimerSetListener = listener;

        // Lấy các thành phần giao diện
        TextView timerView = findViewById(R.id.time_remaining);
        MaterialButton close = findViewById(R.id.close_dialog);
        MaterialButton stop = findViewById(R.id.stop_sleep_timer);

        // Thiết lập sự kiện để cập nhật thời gian còn lại
        if (timerView != null) {
            this.sleepTimerSetListener.getTick().observe((LifecycleOwner) context, tick ->
                    timerView.setText(getTimeRemaining(tick)));
        }

        // Thiết lập sự kiện khi nhấn nút đóng
        if (close != null) {
            close.setOnClickListener(v -> this.dismiss());
        }

        // Thiết lập sự kiện khi nhấn nút dừng Sleep Timer
        if (stop != null) {
            stop.setOnClickListener(v -> {
                // Gọi phương thức cancelTimer từ listener
                sleepTimerSetListener.cancelTimer();
                this.dismiss();
            });
        }
    }

    // Phương thức để định dạng thời gian còn lại
    private String getTimeRemaining(long ms) {
        int totalSeconds = (int) (ms / 1000);
        int hours = totalSeconds / 3600;
        int minutes = totalSeconds / 60 % 60;
        int seconds = totalSeconds % 60;
        return String.format(Locale.getDefault(), "%1d:%02d:%02d", hours, minutes, seconds);
    }
}
