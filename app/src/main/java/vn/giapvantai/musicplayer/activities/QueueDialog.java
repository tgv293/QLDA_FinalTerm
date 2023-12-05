package vn.giapvantai.musicplayer.activities;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import vn.giapvantai.musicplayer.R;
import vn.giapvantai.musicplayer.activities.queue.QueueItemCallback;
import vn.giapvantai.musicplayer.adapter.QueueAdapter;
import vn.giapvantai.musicplayer.model.Music;
import vn.giapvantai.musicplayer.player.PlayerQueue;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class QueueDialog extends BottomSheetDialog {

    public QueueDialog(@NonNull Context context, PlayerQueue queue) {
        super(context);
        setContentView(R.layout.dialog_queue);

        // Lấy danh sách nhạc từ hàng đợi hiện tại
        List<Music> musicList = new ArrayList<>(queue.getCurrentQueue());

        // Thiết lập RecyclerView để hiển thị danh sách hàng đợi
        RecyclerView queueLayout = findViewById(R.id.queue_layout);
        assert queueLayout != null;

        // Sử dụng LayoutManager để quản lý việc hiển thị danh sách theo chiều dọc
        queueLayout.setLayoutManager(new LinearLayoutManager(getContext()));

        // Tạo Adapter để kết nối dữ liệu và RecyclerView
        QueueAdapter queueAdapter = new QueueAdapter(context, musicList, queue);

        // Thiết lập ItemTouchHelper để hỗ trợ vuốt để xóa
        ItemTouchHelper.Callback callback = new QueueItemCallback(queueAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(queueLayout);

        // Đặt Adapter cho RecyclerView và cuộn đến vị trí hiện tại trong hàng đợi
        queueLayout.setAdapter(queueAdapter);
        queueLayout.scrollToPosition(queue.getCurrentPosition());
    }
}
