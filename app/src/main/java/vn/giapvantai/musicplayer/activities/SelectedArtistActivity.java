package vn.giapvantai.musicplayer.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import vn.giapvantai.musicplayer.MPConstants;
import vn.giapvantai.musicplayer.MPPreferences;
import vn.giapvantai.musicplayer.R;
import vn.giapvantai.musicplayer.adapter.HorizontalAlbumsAdapter;
import vn.giapvantai.musicplayer.adapter.SongsAdapter;
import vn.giapvantai.musicplayer.helper.ThemeHelper;
import vn.giapvantai.musicplayer.listener.AlbumSelectListener;
import vn.giapvantai.musicplayer.listener.MusicSelectListener;
import vn.giapvantai.musicplayer.model.Album;
import vn.giapvantai.musicplayer.model.Artist;
import vn.giapvantai.musicplayer.model.Music;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SelectedArtistActivity extends AppCompatActivity implements AlbumSelectListener {

    private final MusicSelectListener musicSelectListener = MPConstants.musicSelectListener;
    private final List<Music> musicList = new ArrayList<>();
    private TextView albumTitle;
    private TextView albumSongsCount;
    private MaterialToolbar toolbar;
    private SongsAdapter songsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Thiết lập chủ đề theo cài đặt người dùng
        setTheme(ThemeHelper.getTheme(MPPreferences.getTheme(getApplicationContext())));
        // Thiết lập chủ đề tối theo cài đặt người dùng
        AppCompatDelegate.setDefaultNightMode(MPPreferences.getThemeMode(getApplicationContext()));
        setContentView(R.layout.activity_selected_artist);

        // Lấy thông tin nghệ sĩ từ intent
        Artist artist = getIntent().getParcelableExtra("artist");

        // Khởi tạo các thành phần giao diện
        RecyclerView songsRecyclerView = findViewById(R.id.songs_layout);
        RecyclerView albumsRecyclerView = findViewById(R.id.albums_layout);
        albumTitle = findViewById(R.id.album_title);
        albumSongsCount = findViewById(R.id.album_song_count);
        toolbar = findViewById(R.id.search_toolbar);
        // Thiết lập tiêu đề thanh công cụ là tên nghệ sĩ và số lượng album, số lượng bài hát
        toolbar.setTitle(artist.name);
        toolbar.setSubtitle(String.format(Locale.getDefault(), "%d albums • %d songs",
                artist.albumCount, artist.songCount));

        // Lấy album mặc định của nghệ sĩ
        Album defAlbum = artist.albums.get(0);
        albumTitle.setText(defAlbum.title);
        albumSongsCount.setText(String.format(Locale.getDefault(), "%d songs",
                defAlbum.music.size()));

        // Thiết lập RecyclerView để hiển thị danh sách các bài hát trong album mặc định
        songsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        musicList.addAll(defAlbum.music);
        songsAdapter = new SongsAdapter(musicSelectListener,  musicList);
        songsRecyclerView.setAdapter(songsAdapter);

        // Thiết lập RecyclerView để hiển thị danh sách album theo chiều ngang
        albumsRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        List<Album> albumList = artist.albums;
        HorizontalAlbumsAdapter albumsAdapter = new HorizontalAlbumsAdapter(albumList, this);
        albumsRecyclerView.setAdapter(albumsAdapter);

        // Thiết lập sự kiện click cho nút Shuffle
        ExtendedFloatingActionButton shuffleControl = findViewById(R.id.shuffle_button);
        shuffleControl.setOnClickListener(v -> musicSelectListener.playQueue(musicList, true));

        // Thiết lập các tùy chọn trên thanh công cụ
        setUpOptions();
    }

    // Thiết lập các tùy chọn trên thanh công cụ
    private void setUpOptions() {
        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menu_add_to_queue) {
                // Thêm tất cả bài hát trong album mặc định vào hàng đợi
                musicSelectListener.addToQueue(musicList);
                return true;
            }

            return false;
        });

        // Thiết lập sự kiện click cho nút quay lại
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    // Phương thức được gọi khi người dùng chọn một album khác
    @Override
    public void selectedAlbum(Album album) {
        // Xóa danh sách bài hát hiện tại và thêm danh sách mới từ album đã chọn
        musicList.clear();
        musicList.addAll(album.music);
        // Thông báo Adapter về sự thay đổi
        songsAdapter.notifyDataSetChanged();

        // Cập nhật tiêu đề và số lượng bài hát trên giao diện
        albumTitle.setText(album.title);
        albumSongsCount.setText(String.format(Locale.getDefault(), "%d Songs",
                album.music.size()));
    }
}
