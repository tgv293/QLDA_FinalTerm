package vn.giapvantai.musicplayer.activities;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import com.bumptech.glide.Glide;
import vn.giapvantai.musicplayer.R;
import vn.giapvantai.musicplayer.helper.MusicLibraryHelper;
import vn.giapvantai.musicplayer.listener.PlayerDialogListener;
import vn.giapvantai.musicplayer.model.Music;
import vn.giapvantai.musicplayer.listener.PlayerListener;
import vn.giapvantai.musicplayer.player.PlayerManager;
import vn.giapvantai.musicplayer.player.PlayerQueue;

import java.util.Locale;

public class PlayerDialog extends BottomSheetDialog implements SeekBar.OnSeekBarChangeListener, PlayerListener, View.OnClickListener {

    private final PlayerManager playerManager;
    private final PlayerDialogListener playerDialogListener;
    private final PlayerQueue playerQueue;

    private final ImageView albumArt;
    private final ImageButton prevControl;
    private final ImageButton nextControl;
    private final ImageButton playPauseControl;
    private final TextView songName;
    private final TextView songAlbum;
    private final TextView currentDuration;
    private final TextView totalDuration;
    private final SeekBar songProgress;

    private Boolean dragging = false;

    public PlayerDialog(@NonNull Context context, PlayerManager playerManager, PlayerDialogListener listener) {
        super(context);
        setContentView(R.layout.dialog_player);

        this.playerDialogListener = listener;
        this.playerManager = playerManager;
        this.playerManager.attachListener(this);
        playerQueue = playerManager.getPlayerQueue();

        albumArt = findViewById(R.id.album_art);
        prevControl = findViewById(R.id.control_prev);
        nextControl = findViewById(R.id.control_next);
        playPauseControl = findViewById(R.id.control_play_pause);
        songName = findViewById(R.id.song_name);
        songAlbum = findViewById(R.id.song_album);
        currentDuration = findViewById(R.id.current_duration);
        totalDuration = findViewById(R.id.total_duration);
        songProgress = findViewById(R.id.song_progress);

        setUpUi();
        setUpListeners();

        this.setOnCancelListener(dialogInterface -> detachListener());
        this.setOnDismissListener(dialogInterface -> detachListener());
    }

    private void detachListener() {
        playerManager.detachListener(this);
    }

    private void setUpUi() {
        Music music = playerManager.getCurrentMusic();

        songName.setText(music.title);
        songAlbum.setText(String.format(Locale.getDefault(), "%s • %s",
                music.artist, music.album));

        Glide.with(getContext().getApplicationContext())
                .load(music.albumArt)
                .placeholder(R.drawable.ic_album_art)
                .into(albumArt);

        int icon = playerManager.isPlaying() ? R.drawable.ic_controls_pause : R.drawable.ic_controls_play;
        playPauseControl.setImageResource(icon);

        totalDuration.setText(MusicLibraryHelper.formatDurationTimeStyle(playerManager.getDuration()));

        if (playerManager.getCurrentPosition() < 100)
            currentDuration.setText(MusicLibraryHelper.formatDurationTimeStyle(percentToPosition(playerManager.getCurrentPosition())));
    }

    private void setUpListeners() {
        songProgress.setOnSeekBarChangeListener(this);
        prevControl.setOnClickListener(this);
        playPauseControl.setOnClickListener(this);
        nextControl.setOnClickListener(this);
    }

    private void showSpeedMenu(View view) {
        // Giữ nguyên phương thức này nếu muốn giữ lại chức năng tốc độ phát nhạc
    }

    private void handleSpeedMenuItemClick(MenuItem item) {
        // Giữ nguyên phương thức này nếu muốn giữ lại chức năng tốc độ phát nhạc
    }

    private int percentToPosition(int percent) {
        return (playerManager.getDuration() * percent) / 100;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        currentDuration.setText(MusicLibraryHelper.formatDurationTimeStyle(percentToPosition(progress)));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        dragging = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        playerManager.seekTo(percentToPosition(seekBar.getProgress()));
        dragging = false;
    }

    @Override
    public void onStateChanged(int state) {
        if (state == State.PLAYING)
            playPauseControl.setImageResource(R.drawable.ic_controls_pause);
        else
            playPauseControl.setImageResource(R.drawable.ic_controls_play);
    }

    @Override
    public void onPositionChanged(int position) {
        if (Boolean.FALSE.equals(dragging))
            songProgress.setProgress(position);
    }

    @Override
    public void onMusicSet(Music music) {
        setUpUi();
    }

    @Override
    public void onPlaybackCompleted() {
        // Không sử dụng
    }

    @Override
    public void onPrepared() {
        // Không sử dụng
    }

    @Override
    public void onRelease() {
        // Không sử dụng
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.control_prev) playerManager.playPrev();
        else if (id == R.id.control_next) playerManager.playNext();
        else if (id == R.id.control_play_pause) playerManager.playPause();
    }
}
