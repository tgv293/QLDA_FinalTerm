package vn.giapvantai.musicplayer.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import vn.giapvantai.musicplayer.MPConstants;
import vn.giapvantai.musicplayer.MPPreferences;
import vn.giapvantai.musicplayer.R;
import vn.giapvantai.musicplayer.adapter.SongsAdapter;
import vn.giapvantai.musicplayer.helper.ThemeHelper;
import vn.giapvantai.musicplayer.listener.MusicSelectListener;
import vn.giapvantai.musicplayer.model.Album;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.Locale;

public class SelectedAlbumActivity extends AppCompatActivity {

    private final MusicSelectListener musicSelectListener = MPConstants.musicSelectListener;

    private ImageView albumArt;
    private TextView albumName;
    private TextView albumDetails;
    private MaterialToolbar toolbar;
    private Album album;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Thiết lập chủ đề theo cài đặt người dùng
        setTheme(ThemeHelper.getTheme(MPPreferences.getTheme(getApplicationContext())));
        // Thiết lập chủ đề tối theo cài đặt người dùng
        AppCompatDelegate.setDefaultNightMode(MPPreferences.getThemeMode(getApplicationContext()));
        setContentView(R.layout.activity_selected_album);

        // Lấy thông tin album từ intent
        album = getIntent().getParcelableExtra("album");

        // Khởi tạo các thành phần giao diện
        ExtendedFloatingActionButton shuffleControl = findViewById(R.id.shuffle_button);
        albumArt = findViewById(R.id.album_art);
        albumName = findViewById(R.id.album_name);
        albumDetails = findViewById(R.id.album_details);
        toolbar = findViewById(R.id.search_toolbar);
        // Thiết lập tiêu đề thanh công cụ là tên album và số lượng bài hát
        toolbar.setTitle(album.title);
        toolbar.setSubtitle(String.format(Locale.getDefault(), "%d songs", album.music.size()));

        // Thiết lập RecyclerView để hiển thị danh sách các bài hát trong album
        RecyclerView recyclerView = findViewById(R.id.songs_layout);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new SongsAdapter(musicSelectListener, album.music));

        // Thiết lập sự kiện click cho nút Shuffle
        shuffleControl.setOnClickListener(v -> musicSelectListener.playQueue(album.music, true));

        // Hiển thị dữ liệu album trên giao diện
        setAlbumDataToUi();
        // Thiết lập các tùy chọn trên thanh công cụ
        setUpOptions();
    }

    // Thiết lập các tùy chọn trên thanh công cụ
    private void setUpOptions() {
        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menu_add_to_queue) {
                // Thêm tất cả bài hát trong album vào hàng đợi
                musicSelectListener.addToQueue(album.music);
                return true;
            }

            return false;
        });

        // Thiết lập sự kiện click cho nút quay lại
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    // Hiển thị dữ liệu album lên giao diện
    private void setAlbumDataToUi() {
        albumName.setText(album.title);
        albumDetails.setText(String.format(Locale.getDefault(), "%s . %s . %d songs",
                album.music.get(0).artist,
                album.year,
                album.music.size()));

        // Kiểm tra và hiển thị ảnh album nếu được kích hoạt trong cài đặt
        boolean state = MPPreferences.getAlbumRequest(this);
        if (state)
            Glide.with(this)
                    .load(album.music.get(0).albumArt)
                    .placeholder(R.drawable.ic_album_art)
                    .into(albumArt);
    }
}
