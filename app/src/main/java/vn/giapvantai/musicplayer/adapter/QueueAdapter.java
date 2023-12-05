package vn.giapvantai.musicplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import vn.giapvantai.musicplayer.R;
import vn.giapvantai.musicplayer.activities.queue.QueueItemCallback;
import vn.giapvantai.musicplayer.helper.ThemeHelper;
import vn.giapvantai.musicplayer.model.Music;
import vn.giapvantai.musicplayer.player.PlayerQueue;

import java.util.Collections;
import java.util.List;

public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.MyViewHolder> implements QueueItemCallback.QueueItemInterface {

    private final List<Music> musicList;
    private final PlayerQueue playerQueue;
    private final Music currentMusic;
    private final @ColorInt int colorInt;
    private int defaultTint;

    public QueueAdapter(Context context, List<Music> musics, PlayerQueue playerQueue) {
        this.musicList = musics;
        this.playerQueue = playerQueue;
        this.currentMusic = playerQueue.getCurrentMusic();

        // Lấy màu chủ đạo từ theme
        colorInt = ThemeHelper.resolveColorAttr(
                context,
                androidx.appcompat.R.attr.colorPrimary
        );

        // Màu mặc định cho vị trí không phát nhạc
        defaultTint = context.getColor(R.color.colorTextMed);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo ViewHolder từ layout XML
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_queue, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Thiết lập dữ liệu cho từng item trong danh sách hàng đợi
        holder.songName.setText(musicList.get(position).title);

        if (currentMusic.title.equals(musicList.get(position).title)) {
            // Đang phát nhạc, hiển thị trạng thái "Now Playing"
            holder.albumName.setText(R.string.now_playing);
            holder.albumName.setTextColor(colorInt);
            holder.drag.setImageResource(R.drawable.ic_current_playing);
        } else {
            // Không phát nhạc, hiển thị thông tin về nghệ sĩ và icon kéo thả
            holder.albumName.setText(musicList.get(position).artist);
            holder.albumName.setTextColor(defaultTint);
            holder.drag.setImageResource(R.drawable.ic_drag_handle);
        }
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        // Di chuyển item trong danh sách và thông báo cập nhật
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(musicList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(musicList, i, i - 1);
            }
        }

        // Di chuyển item trong hàng đợi của người nghe nhạc
        playerQueue.swap(fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onRowSelected(MyViewHolder myViewHolder) {
        // Xử lý khi một item được chọn (chưa cần thiết cho mục đích hiện tại)
    }

    @Override
    public void onRowClear(MyViewHolder myViewHolder) {
        // Xử lý khi một item được bỏ chọn (chưa cần thiết cho mục đích hiện tại)
    }

    // ViewHolder đại diện cho mỗi item trong danh sách
    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView songName;
        private final TextView albumName;
        private final ImageButton drag;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            // Ánh xạ các thành phần giao diện trong mỗi item
            songName = itemView.findViewById(R.id.song_name);
            albumName = itemView.findViewById(R.id.song_album);
            drag = itemView.findViewById(R.id.control_drag);

            // Đặt sự kiện click cho nút xóa một bài hát khỏi hàng đợi
            itemView.findViewById(R.id.control_close).setOnClickListener(v -> {
                int position = getAdapterPosition();

                if (position >= 0 && position < musicList.size()) {
                    boolean isPlaying = currentMusic.title.equals(musicList.get(position).title);

                    // Không cho phép xóa bài hát đang phát
                    if (!isPlaying) {
                        musicList.remove(position);
                        playerQueue.removeMusicFromQueue(position);
                        notifyItemRemoved(position);
                    }
                }
            });
        }
    }
}
