package vn.giapvantai.musicplayer.activities;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
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

    // UI components
    private final ImageView albumArt;
    private final ImageButton repeatControl;
    private final ImageButton shuffleControl;
    private final ImageButton prevControl;
    private final ImageButton nextControl;
    private final ImageButton playPauseControl;
    private final TextView songName;
    private final TextView songAlbum;
    private final TextView currentDuration;
    private final TextView totalDuration;
    private final TextView songDetails;
    private final SeekBar songProgress;
    private final ImageButton musicQueue;

    private Boolean dragging = false;

    public PlayerDialog(@NonNull Context context, PlayerManager playerManager, PlayerDialogListener listener) {
        super(context);
        setContentView(R.layout.dialog_player);

        this.playerDialogListener = listener;
        this.playerManager = playerManager;
        this.playerManager.attachListener(this);
        playerQueue = playerManager.getPlayerQueue();

        // UI components initialization
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

        // Setting up UI and listeners
        setUpUi();
        setUpListeners();

        // Handling events when the dialog is canceled or dismissed
        this.setOnCancelListener(dialogInterface -> detachListener());
        this.setOnDismissListener(dialogInterface -> detachListener());
    }

    private void detachListener() {
        playerManager.detachListener(this);
    }

    private void setUpUi() {
        Music music = playerManager.getCurrentMusic();

        // Displaying song and artist information
        songName.setText(music.title);
        songAlbum.setText(String.format(Locale.getDefault(), "%s • %s", music.artist, music.album));

        // Displaying album art
        Glide.with(getContext().getApplicationContext())
                .load(music.albumArt)
                .placeholder(R.drawable.ic_album_art)
                .into(albumArt);

        // Setting play or pause icon based on the music playback state
        int icon = playerManager.isPlaying() ? R.drawable.ic_controls_pause : R.drawable.ic_controls_play;
        playPauseControl.setImageResource(icon);

        // Setting Shuffle button state
        if (playerQueue.isShuffle()) shuffleControl.setAlpha(1f);
        else shuffleControl.setAlpha(0.3f);

        // Setting repeat icon
        int repeat = playerQueue.isRepeat() ? R.drawable.ic_controls_repeat_one : R.drawable.ic_controls_repeat;
        repeatControl.setImageResource(repeat);

        // Displaying total duration of the song
        totalDuration.setText(MusicLibraryHelper.formatDurationTimeStyle(playerManager.getDuration()));

        // Displaying current duration of the song if played beyond 1%
        if (playerManager.getCurrentPosition() < 100)
            currentDuration.setText(MusicLibraryHelper.formatDurationTimeStyle(percentToPosition(playerManager.getCurrentPosition())));

        // Setting up audio details
        setUpAudioDetails();
    }

    private void setUpAudioDetails() {
        int[] rates = MusicLibraryHelper.getBitSampleRates(playerManager.getCurrentMusic());
        if (rates[0] > 0 && rates[1] > 0) {
            songDetails.setText(
                    String.format(Locale.getDefault(), "%s kHz • %s kbps", rates[0], rates[1]));
        }
    }

    private void setUpListeners() {
        songProgress.setOnSeekBarChangeListener(this);
        repeatControl.setOnClickListener(this);
        prevControl.setOnClickListener(this);
        playPauseControl.setOnClickListener(this);
        nextControl.setOnClickListener(this);
        shuffleControl.setOnClickListener(this);
        musicQueue.setOnClickListener(this);

        // Setting current time to "0:00"
        currentDuration.setText(getContext().getString(R.string.zero_time));
    }

    private void showSpeedMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.inflate(R.menu.menu_speed);

        popupMenu.setOnMenuItemClickListener(item -> {
            handleSpeedMenuItemClick(item);
            return true;
        });

        popupMenu.show();
    }

    private void handleSpeedMenuItemClick(MenuItem item) {
        if (item == null) {
            return;
        }

        Map<Integer, Float> speedMultiplierMap = new HashMap<>();
        speedMultiplierMap.put(R.id.speed_025x, 0.25f);
        speedMultiplierMap.put(R.id.speed_05x, 0.5f);
        // ... (other speed multipliers)
        speedMultiplierMap.put(R.id.speed_25x, 2.5f);

        Float speedMultiplier = speedMultiplierMap.get(item.getItemId());

        if (speedMultiplier == null) {
            speedMultiplier = 1.0f;
        }

        if (playerDialogListener != null) {
            playerDialogListener.speedOptionSelect(speedMultiplier);
        }
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
        // Not used
    }

    @Override
    public void onPrepared() {
        // Not used
    }

    @Override
    public void onRelease() {
        // Not used
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.control_repeat) setRepeat();
        else if (id == R.id.control_shuffle) setShuffle();
        else if (id == R.id.control_prev) playerManager.playPrev();
        else if (id == R.id.control_next) playerManager.playNext();
        else if (id == R.id.control_play_pause) playerManager.playPause();
        else if (id == R.id.music_queue) this.playerDialogListener.queueOptionSelect();
    }

    private void setRepeat() {
        boolean repeatState = !playerQueue.isRepeat();
        playerQueue.setRepeat(repeatState);
        int repeat = repeatState ? R.drawable.ic_controls_repeat_one : R.drawable.ic_controls_repeat;
        repeatControl.setImageResource(repeat);
    }

    private void setShuffle() {
        boolean shuffleState = !playerQueue.isShuffle();
        playerQueue.setShuffle(shuffleState);
        if (shuffleState) shuffleControl.setAlpha(1f);
        else shuffleControl.setAlpha(0.3f);
    }
}
