package vn.giapvantai.musicplayer.player;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;

import vn.giapvantai.musicplayer.listener.PlayerListener;
import vn.giapvantai.musicplayer.model.Music;

public class PlayerManager implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {

    private final Context context;
    private final PlayerService playerService;
    private final AudioManager audioManager;
    private final List<PlayerListener> playerListeners = new ArrayList<>();
    private final PlayerQueue playerQueue;
    private final MutableLiveData<Integer> progressPercent = new MutableLiveData<>();
    private int playerState;
    private MediaPlayer mediaPlayer;
    private PlayerNotificationManager notificationManager;

    public PlayerManager(@NonNull PlayerService playerService) {
        this.playerService = playerService;
        this.context = playerService.getApplicationContext();
        this.playerQueue = PlayerQueue.getInstance();
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        Observer<Integer> progressObserver = percent -> {
            for (PlayerListener playerListener : playerListeners)
                playerListener.onPositionChanged(percent);
        };
        progressPercent.observeForever(progressObserver);
    }

    public void setPlaybackSpeed(float speedMultiplier) {
        if (mediaPlayer != null) {
            mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(speedMultiplier));
        }
    }

    public void attachListener(PlayerListener playerListener) {
        playerListeners.add(playerListener);
    }

    public void detachListener(PlayerListener playerListener) {
        if (playerListeners.size() > 2) {
            playerListeners.remove(playerListener);
        }
    }

    private void setPlayerState(@PlayerListener.State int state) {
        playerState = state;
        for (PlayerListener listener : playerListeners) {
            listener.onStateChanged(state);
        }

        playerService.getNotificationManager().updateNotification();

        int playbackState = isPlaying() ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED;
        int currentPosition = mediaPlayer == null ? 0 : mediaPlayer.getCurrentPosition();

        playerService.getMediaSessionCompat().setPlaybackState(
                new PlaybackStateCompat.Builder()
                        .setActions(
                                PlaybackStateCompat.ACTION_PLAY |
                                        PlaybackStateCompat.ACTION_PAUSE |
                                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                        PlaybackStateCompat.ACTION_SEEK_TO |
                                        PlaybackStateCompat.ACTION_PLAY_PAUSE)
                        .setState(playbackState, currentPosition, 0)
                        .build());
    }

    public Music getCurrentMusic() {
        return playerQueue.getCurrentMusic();
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public PlayerQueue getPlayerQueue() {
        return playerQueue;
    }

    public void setPlayerListener(PlayerListener listener) {
        playerListeners.add(listener);
        listener.onPrepared();
    }

    public void setMusicList(List<Music> musicList) {
        playerQueue.setCurrentQueue(new ArrayList<>(musicList));
        initMediaPlayer(); // chạy ngay bây giờ
    }

    public void setMusic(Music music) {
        List<Music> musicList = new ArrayList<>();
        musicList.add(music);
        playerQueue.setCurrentQueue(musicList);
        initMediaPlayer();
    }

    public void addMusicQueue(List<Music> musicList) {
        playerQueue.addMusicListToQueue(new ArrayList<>(musicList));

        if (!mediaPlayer.isPlaying())
            initMediaPlayer();  // chạy khi sẵn sàng
    }

    public int getAudioSessionId() {
        return (mediaPlayer != null) ? mediaPlayer.getAudioSessionId() : -1;
    }

    public void detachService() {
        playerService.stopForeground(false);
    }

    public void attachService() {
        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                playerService.startForeground(PlayerService.NOTIFICATION_ID, notificationManager.createNotification(), PlayerService.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
            } else {
                playerService.startForeground(PlayerService.NOTIFICATION_ID, notificationManager.createNotification());
            }
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        playNext();
        for (PlayerListener listener : playerListeners)
            listener.onPlaybackCompleted();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                playerService.startForeground(PlayerService.NOTIFICATION_ID, notificationManager.createNotification(), PlayerService.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
            } else {
                playerService.startForeground(PlayerService.NOTIFICATION_ID, notificationManager.createNotification());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (PlayerListener listener : playerListeners)
            listener.onMusicSet(playerQueue.getCurrentMusic());

        // The MediaObserver is not removed in this example. Be sure to handle it appropriately.
        MediaObserver mediaObserver = new MediaObserver();
        new Thread(mediaObserver).start();
    }

    public boolean isPlaying() {
        return isMediaPlayer() && mediaPlayer.isPlaying();
    }

    public boolean isMediaPlayer() {
        return mediaPlayer != null;
    }

    public void pauseMediaPlayer() {
        setPlayerState(PlayerListener.State.PAUSED);
        mediaPlayer.pause();

        playerService.stopForeground(false);
        notificationManager.getNotificationManager().notify(PlayerService.NOTIFICATION_ID, notificationManager.createNotification());
    }

    public void resumeMediaPlayer() {
        if (!isPlaying()) {
            if (mediaPlayer == null) {
                initMediaPlayer();
            }
            mediaPlayer.start();
            setPlayerState(PlayerListener.State.RESUMED);
            playerService.startForeground(PlayerService.NOTIFICATION_ID, notificationManager.createNotification());
            notificationManager.updateNotification();
        }
    }

    public void playPrev() {
        playerQueue.prev();
        initMediaPlayer();
    }

    public void playNext() {
        playerQueue.next();
        initMediaPlayer();
    }

    public void playPause() {
        if (isPlaying()) {
            mediaPlayer.pause();
            setPlayerState(PlayerListener.State.PAUSED);
        } else {
            if (mediaPlayer == null) {
                initMediaPlayer();
            }
            mediaPlayer.start();
            setPlayerState(PlayerListener.State.PLAYING);
        }
    }

    public void release() {
        if (playerService != null) {
            playerService.stopForeground(true);
            playerService.stopSelf();
        }

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        for (PlayerListener playerListener : playerListeners)
            playerListener.onRelease();
    }

    public void seekTo(int position) {
        mediaPlayer.seekTo(position);
    }

    private void initMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
        } else {
            mediaPlayer = new MediaPlayer();

            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build());

            notificationManager = playerService.getNotificationManager();
        }

        tryToGetAudioFocus();
        Music currentMusic = playerQueue.getCurrentMusic();
        if (currentMusic != null) {
            Uri trackUri = Uri.parse(currentMusic.uri); // Assuming uri is a String

            try {
                mediaPlayer.setDataSource(context, trackUri);
                mediaPlayer.prepareAsync();
                setPlayerState(PlayerListener.State.PLAYING);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
    }

    private class MediaObserver implements Runnable {
        private final Handler handler = new Handler();

        @Override
        public void run() {
            if (mediaPlayer != null && isPlaying()) {
                int percent = mediaPlayer.getCurrentPosition() * 100 / mediaPlayer.getDuration();
                progressPercent.postValue(percent);
            }
            // Lên lịch thực hiện tiếp theo sau một khoảng thời gian trễ
            handler.postDelayed(this, 100);
        }

        // Dừng việc thực hiện các cập nhật định kỳ bằng cách loại bỏ các lời gọi callback đang chờ
        public void stop() {
            handler.removeCallbacks(this);
        }
    }
}
