package vn.giapvantai.musicplayer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import vn.giapvantai.musicplayer.MPPreferences;
import vn.giapvantai.musicplayer.R;
import vn.giapvantai.musicplayer.helper.MusicLibraryHelper;
import vn.giapvantai.musicplayer.listener.MusicSelectListener;
import vn.giapvantai.musicplayer.model.Music;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Locale;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.MyViewHolder> {

    public final MusicSelectListener listener;
    private final List<Music> musicList;

    // Constructor để khởi tạo adapter với danh sách nhạc và listener
    public SongsAdapter(MusicSelectListener listener, List<Music> musics) {
        this.listener = listener;
        this.musicList = musics;
    }

    // Tạo ViewHolder mới (được gọi bởi LayoutManager)
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_songs, parent, false);
        return new MyViewHolder(view);
    }

    // Gắn dữ liệu vào ViewHolder (được gọi bởi LayoutManager)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Music music = musicList.get(position);

        // Gắn thông tin của bài hát vào các thành phần UI
        holder.songName.setText(music.title);
        holder.albumName.setText(
                String.format(Locale.getDefault(), "%s • %s",
                        music.artist,
                        music.album)
        );

        // Hiển thị lịch sử nghe nhạc nếu có
        if (music.dateAdded == -1)
            holder.songHistory.setVisibility(View.GONE);
        else
            holder.songHistory.setText(
                    String.format(Locale.getDefault(), "%s • %s",
                            MusicLibraryHelper.formatDuration(music.duration),
                            MusicLibraryHelper.formatDate(music.dateAdded))
            );

        // Load ảnh album bằng thư viện Glide nếu có hoặc sử dụng ảnh mặc định
        if (holder.state && !music.albumArt.equals("")) {
            Glide.with(holder.albumArt.getContext())
                    .load(music.albumArt)
                    .placeholder(R.drawable.ic_album_art)
                    .into(holder.albumArt);
        } else if (music.albumArt.equals("")) {
            holder.albumArt.setImageResource(R.drawable.ic_album_art);
        }
    }

    // Trả về số lượng item trong danh sách
    @Override
    public int getItemCount() {
        return musicList.size();
    }

    // Inner class đại diện cho ViewHolder
    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView songName;
        private final TextView albumName;
        private final TextView songHistory;
        private final ImageView albumArt;
        private final boolean state;

        // Constructor cho ViewHolder
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            // Lấy trạng thái hiển thị album từ Shared Preferences
            state = MPPreferences.getAlbumRequest(itemView.getContext());

            // Khởi tạo các thành phần UI
            albumArt = itemView.findViewById(R.id.album_art);
            songHistory = itemView.findViewById(R.id.song_history);
            songName = itemView.findViewById(R.id.song_name);
            albumName = itemView.findViewById(R.id.song_album);

            // Đặt lắng nghe sự kiện khi click vào item để mở bài hát
            itemView.findViewById(R.id.root_layout).setOnClickListener(v -> {
                List<Music> toPlay = musicList.subList(getAdapterPosition(), musicList.size());
                listener.playQueue(toPlay, false);
            });
        }
    }
}
