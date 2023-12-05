package vn.giapvantai.musicplayer.dialogs;

import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import vn.giapvantai.musicplayer.R;
import vn.giapvantai.musicplayer.adapter.SleepTimerAdapter;
import vn.giapvantai.musicplayer.listener.MinuteSelectListener;
import vn.giapvantai.musicplayer.listener.SleepTimerSetListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class SleepTimerDialog extends BottomSheetDialog implements MinuteSelectListener {

    private final TextInputEditText minutesEditText;
    private final TextInputLayout minutesLayout;
    private final SleepTimerSetListener sleepTimerSetListener;

    public SleepTimerDialog(@NonNull Context context, @NonNull SleepTimerSetListener listener) {
        super(context);
        setContentView(R.layout.dialog_sleep_timer);

        // Khởi tạo SleepTimerDialog với người nghe SleepTimerSetListener
        this.sleepTimerSetListener = listener;

        // Lấy TextInputLayout và TextInputEditText để nhập số phút
        minutesLayout = findViewById(R.id.sleep_timer_minutes_layout);
        minutesEditText = findViewById(R.id.sleep_timer_minutes);

        // Lấy Button để thêm Sleep Timer
        MaterialButton addSleepTimerButton = findViewById(R.id.add_sleep_timer);

        // Lấy RecyclerView để hiển thị danh sách số phút
        RecyclerView minuteListView = findViewById(R.id.minutes_layout);

        // Thiết lập sự kiện khi người dùng nhập số phút
        setupMinutesEditText();

        // Thiết lập RecyclerView để hiển thị danh sách số phút
        setupMinuteListView(minuteListView);

        // Thiết lập sự kiện khi nhấn nút thêm Sleep Timer
        if (addSleepTimerButton != null) {
            addSleepTimerButton.setOnClickListener(v -> addSleepTimer());
        }
    }

    // Thiết lập sự kiện khi người dùng nhập số phút
    private void setupMinutesEditText() {
        if (minutesEditText != null) {
            minutesEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    // Xóa thông báo lỗi trước khi người dùng bắt đầu nhập
                    Objects.requireNonNull(minutesLayout).setError(null);
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    // Không cần thực hiện gì trong quá trình nhập
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    // Không cần thực hiện gì sau khi nhập xong
                }
            });
        }
    }

    // Thiết lập RecyclerView để hiển thị danh sách số phút
    private void setupMinuteListView(RecyclerView minuteListView) {
        if (minuteListView != null) {
            minuteListView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            minuteListView.setAdapter(new SleepTimerAdapter(this));
        }
    }

    // Thêm Sleep Timer với số phút đã nhập
    private void addSleepTimer() {
        if (minutesEditText == null || minutesEditText.getText() == null || minutesEditText.getText().toString().isEmpty()) {
            // Hiển thị thông báo lỗi nếu người dùng chưa nhập số phút
            minutesLayout.setError("Please enter minutes for the timer");
            return;
        }

        // Lấy số phút từ TextInputEditText và thiết lập cho SleepTimerSetListener
        int minutes = Integer.parseInt(minutesEditText.getText().toString());
        sleepTimerSetListener.setTimer(minutes);

        // Đóng Dialog sau một khoảng thời gian ngắn
        dismissAfterDelay();
    }

    // Đóng Dialog sau một khoảng thời gian ngắn
    private void dismissAfterDelay() {
        new Handler().postDelayed(() -> {
            if (isShowing()) {
                dismiss();
            }
        }, 300);
    }

    // Ghi đè phương thức từ interface MinuteSelectListener để xử lý sự kiện khi chọn số phút
    @Override
    public void select(int minutes) {
        if (minutesEditText != null) {
            // Đặt số phút đã chọn vào TextInputEditText
            minutesEditText.setText(String.valueOf(minutes));
        }
    }
}
