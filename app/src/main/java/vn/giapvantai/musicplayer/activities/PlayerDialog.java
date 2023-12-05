package vn.giapvantai.musicplayer.activities;

import android.content.Context;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import vn.giapvantai.musicplayer.R;
import vn.giapvantai.musicplayer.helper.MusicLibraryHelper;
import vn.giapvantai.musicplayer.listener.PlayerDialogListener;
import vn.giapvantai.musicplayer.model.Music;
import vn.giapvantai.musicplayer.listener.PlayerListener;
import vn.giapvantai.musicplayer.player.PlayerManager;
import vn.giapvantai.musicplayer.player.PlayerQueue;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PlayerDialog extends BottomSheetDialog implements SeekBar.OnSeekBarChangeListener, PlayerListener, View.OnClickListener {

    private final PlayerManager playerManager;
    private final PlayerDialogListener playerDialogListener;
    private final PlayerQueue playerQueue;

    // Các thành phần giao diện
    private final ImageView albumArt;
    private final ImageButton musicSpeedButton;
    private final ImageButton repeatControl;
    private final ImageButton shuffleControl;
    private final ImageButton prevControl;
    private final ImageButton nextControl;
    private final ImageButton playPauseControl;
    private final ImageButton musicQueue;
    private final ImageButton sleepTimer;
    private final TextView songName;
    private final TextView songAlbum;
    private final TextView currentDuration;
    private final TextView totalDuration;
    private final TextView songDetails;
    private final SeekBar songProgress;

    private Boolean dragging = false;

    public PlayerDialog(@NonNull Context context, PlayerManager playerManager, PlayerDialogListener listener) {
        super(context);
        setContentView(R.layout.dialog_player);

        this.playerDialogListener = listener;
        this.playerManager = playerManager;
        this.playerManager.attachListener(this);
        playerQueue = playerManager.getPlayerQueue();

        // Ánh xạ các thành phần giao diện
        albumArt = findViewById(R.id.album_art);
        repeatControl = findViewById(R.id.control_repeat);
        shuffleControl = findViewById(R.id.control_shuffle);
        prevControl = findViewById(R.id.control_prev);
        nextControl = findViewById(R.id.control_next);
        playPauseControl = findViewById(R.id.control_play_pause);
        songName = findViewById(R.id.song_name);
        songAlbum = findViewById(R.id.song_album);
        currentDuration = findViewById(R.id.current_duration);
        totalDuration = findViewById(R.id.total_duration);
        songProgress = findViewById(R.id.song_progress);
        songDetails = findViewById(R.id.audio_details);
        musicQueue = findViewById(R.id.music_queue);
        sleepTimer = findViewById(R.id.sleep_timer);
        musicSpeedButton = findViewById(R.id.music_speed);

        // Thiết lập giao diện và lắng nghe sự kiện
        setUpUi();
        setUpListeners();

        // Xử lý sự kiện khi hủy hoặc đóng dialog
        this.setOnCancelListener(dialogInterface -> detachListener());
        this.setOnDismissListener(dialogInterface -> detachListener());
    }

    // Phương thức hủy bỏ lắng nghe sự kiện
    private void detachListener() {
        playerManager.detachListener(this);
    }

    // Thiết lập giao diện người dùng
    private void setUpUi() {
        Music music = playerManager.getCurrentMusic();

        // Hiển thị thông tin bài hát và nghệ sĩ
        songName.setText(music.title);
        songAlbum.setText(String.format(Locale.getDefault(), "%s • %s",
                music.artist, music.album));

        // Hiển thị ảnh album bài hát
        Glide.with(getContext().getApplicationContext())
                .load(music.albumArt)
                .placeholder(R.drawable.ic_album_art)
                .into(albumArt);

        // Thiết lập biểu tượng chơi hoặc dừng tùy thuộc vào trạng thái phát nhạc
        int icon = playerManager.isPlaying() ? R.drawable.ic_controls_pause : R.drawable.ic_controls_play;
        playPauseControl.setImageResource(icon);

        // Thiết lập trạng thái của nút Shuffle
        if (playerQueue.isShuffle()) shuffleControl.setAlpha(1f);
        else shuffleControl.setAlpha(0.3f);

        // Thiết lập biểu tượng lặp lại
        int repeat = playerQueue.isRepeat() ? R.drawable.ic_controls_repeat_one : R.drawable.ic_controls_repeat;
        repeatControl.setImageResource(repeat);

        // Hiển thị thời gian tổng cộng của bài hát
        totalDuration.setText(MusicLibraryHelper.formatDurationTimeStyle(playerManager.getDuration()));

        // Hiển thị thời gian hiện tại của bài hát nếu đã phát qua 1 phần trăm
        if (playerManager.getCurrentPosition() < 100)
            currentDuration.setText(MusicLibraryHelper
                    .formatDurationTimeStyle(percentToPosition(playerManager.getCurrentPosition())));

        // Thiết lập thông tin âm thanh
        setUpAudioDetails();
    }

    // Thiết lập thông tin âm thanh (bit rate và sample rate)
    private void setUpAudioDetails() {
        int[] rates = MusicLibraryHelper.getBitSampleRates(playerManager.getCurrentMusic());
        if (rates[0] > 0 && rates[1] > 0) {
            songDetails.setText(
                    String.format(Locale.getDefault(),
                            "%s kHz • %s kbps", rates[0], rates[1]));
        }
    }

    // Thiết lập lắng nghe sự kiện cho các thành phần giao diện
    private void setUpListeners() {
        songProgress.setOnSeekBarChangeListener(this);
        repeatControl.setOnClickListener(this);
        prevControl.setOnClickListener(this);
        playPauseControl.setOnClickListener(this);
        nextControl.setOnClickListener(this);
        shuffleControl.setOnClickListener(this);
        musicQueue.setOnClickListener(this);
        sleepTimer.setOnClickListener(this);
        musicSpeedButton.setOnClickListener(this::showSpeedMenu);

        // Thiết lập thời gian hiện tại là "0:00"
        currentDuration.setText(getContext().getString(R.string.zero_time));
    }

    // Hiển thị menu tốc độ phát nhạc khi người dùng nhấn nút tốc độ
    private void showSpeedMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_speed, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            handleSpeedMenuItemClick(item);
            return true;
        });

        popupMenu.show();
    }

    // Xử lý sự kiện khi người dùng chọn tốc độ phát nhạc từ menu
    private void handleSpeedMenuItemClick(MenuItem item) {
        if (item == null) {
            return;
        }

        Map<Integer, Float> speedMultiplierMap = new HashMap<>();
        speedMultiplierMap.put(R.id.speed_025x, 0.25f);
        speedMultiplierMap.put(R.id.speed_05x, 0.5f);
        speedMultiplierMap.put(R.id.speed_075x, 0.75f);
        speedMultiplierMap.put(R.id.speed_normal, 1.0f);
        speedMultiplierMap.put(R.id.speed_125x, 1.25f);
        speedMultiplierMap.put(R.id.speed_15x, 1.5f);
        speedMultiplierMap.put(R.id.speed_175x, 1.75f);
        speedMultiplierMap.put(R.id.speed_2x, 2.0f);
        speedMultiplierMap.put(R.id.speed_225x, 2.25f);
        speedMultiplierMap.put(R.id.speed_25x, 2.5f);

        Float speedMultiplier = speedMultiplierMap.get(item.getItemId());

        if (speedMultiplier == null) {
            speedMultiplier = 1.0f;
        }

        if (playerDialogListener != null) {
            playerDialogListener.speedOptionSelect(speedMultiplier);
        }
    }

    // Chuyển đổi phần trăm sang vị trí thời gian trong bài hát
    private int percentToPosition(int percent) {
        return (playerManager.getDuration() * percent) / 100;
    }

    // Lắng nghe sự kiện khi người dùng di chuyển thanh tiến độ bài hát
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        currentDuration.setText(MusicLibraryHelper.formatDurationTimeStyle(percentToPosition(progress)));
    }

    // Lắng nghe sự kiện khi người dùng bắt đầu chạm vào thanh tiến độ bài hát
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        dragging = true;
    }

    // Lắng nghe sự kiện khi người dùng ngừng chạm vào thanh tiến độ bài hát
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        playerManager.seekTo(percentToPosition(seekBar.getProgress()));
        dragging = false;
    }

    // Lắng nghe sự kiện thay đổi trạng thái của bài hát (phát, dừng, tạm dừng, ...)
    @Override
    public void onStateChanged(int state) {
        if (state == State.PLAYING)
            playPauseControl.setImageResource(R.drawable.ic_controls_pause);
        else
            playPauseControl.setImageResource(R.drawable.ic_controls_play);
    }

    // Lắng nghe sự kiện khi vị trí thời gian của bài hát thay đổi
    @Override
    public void onPositionChanged(int position) {
        if (Boolean.FALSE.equals(dragging))
            songProgress.setProgress(position);
    }

    // Lắng nghe sự kiện khi một bài hát mới được chọn để phát
    @Override
    public void onMusicSet(Music music) {
        setUpUi();
    }

    // Lắng nghe sự kiện khi bài hát hoàn thành phát
    @Override
    public void onPlaybackCompleted() {
        // Không sử dụng
    }

    // Lắng nghe sự kiện khi trình phát nhạc đã được chuẩn bị sẵn sàng
    @Override
    public void onPrepared() {
        // Không sử dụng
    }

    // Lắng nghe sự kiện khi trình phát nhạc được giải phóng
    @Override
    public void onRelease() {
        // Không sử dụng
    }

    // Lắng nghe sự kiện khi người dùng nhấn vào các nút điều khiển (phát, dừng, lặp lại, ...)
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.control_repeat) setRepeat();
        else if (id == R.id.control_shuffle) setShuffle();
        else if (id == R.id.control_prev) playerManager.playPrev();
        else if (id == R.id.control_next) playerManager.playNext();
        else if (id == R.id.control_play_pause) playerManager.playPause();
        else if (id == R.id.music_queue) this.playerDialogListener.queueOptionSelect();
        else if (id == R.id.sleep_timer) this.playerDialogListener.sleepTimerOptionSelect();
    }

    // Phương thức xử lý sự kiện khi người dùng nhấn vào nút lặp lại
    private void setRepeat() {
        boolean repeatState = !playerQueue.isRepeat();
        playerQueue.setRepeat(repeatState);
        int repeat = repeatState ? R.drawable.ic_controls_repeat_one : R.drawable.ic_controls_repeat;
        repeatControl.setImageResource(repeat);
    }

    // Phương thức xử lý sự kiện khi người dùng nhấn vào nút shuffle
    private void setShuffle() {
        boolean shuffleState = !playerQueue.isShuffle();
        playerQueue.setShuffle(shuffleState);
        if (shuffleState) shuffleControl.setAlpha(1f);
        else shuffleControl.setAlpha(0.3f);
    }
}
