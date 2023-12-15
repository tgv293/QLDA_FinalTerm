package vn.giapvantai.musicplayer.player;

import static vn.giapvantai.musicplayer.MPConstants.*;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import vn.giapvantai.musicplayer.MainActivity;
import vn.giapvantai.musicplayer.R;
import vn.giapvantai.musicplayer.helper.MusicLibraryHelper;
import vn.giapvantai.musicplayer.model.Music;

public class PlayerNotificationManager {

    private final NotificationManager notificationManager;
    private final PlayerService playerService;
    private NotificationCompat.Builder notificationBuilder;
    private final androidx.media.app.NotificationCompat.MediaStyle notificationStyle;

    PlayerNotificationManager(@NonNull final PlayerService playerService) {
        // Khởi tạo đối tượng PlayerNotificationManager với PlayerService
        this.playerService = playerService;
        // Cài đặt kiểu hiển thị của notification media
        notificationStyle = new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1, 2);
        // Lấy đối tượng NotificationManager từ dịch vụ
        notificationManager = (NotificationManager) playerService.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public final NotificationManager getNotificationManager() {
        return notificationManager;
    }

    // Phương thức xây dựng PendingIntent cho các hành động của người chơi như play, pause, next, prev
    private PendingIntent playerAction(@NonNull final String action) {
        final Intent pauseIntent = new Intent();
        pauseIntent.setAction(action);

        return PendingIntent.getBroadcast(playerService, REQUEST_CODE, pauseIntent, PendingIntent.FLAG_IMMUTABLE);
    }

    // Phương thức tạo notification
    public Notification createNotification() {
        // Lấy thông tin bài hát hiện tại từ PlayerManager
        final Music song = playerService.getPlayerManager().getCurrentMusic();

        // Tạo Intent để mở MainActivity khi notification được nhấn
        final Intent openPlayerIntent = new Intent(playerService, MainActivity.class);
        openPlayerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent contentIntent = PendingIntent.getActivity(playerService, REQUEST_CODE,
                openPlayerIntent, PendingIntent.FLAG_IMMUTABLE);

        // Nếu đối tượng NotificationCompat.Builder chưa được khởi tạo, thì khởi tạo nó
        if (notificationBuilder == null) {
            notificationBuilder = new NotificationCompat.Builder(playerService, CHANNEL_ID);
            notificationBuilder
                    .setShowWhen(false)
                    .setSmallIcon(R.drawable.ic_notif_music_note)
                    .setColorized(true)
                    .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        }

        // Nếu phiên bản Android >= O, tạo Notification Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        // Lấy hình ảnh album từ thư viện âm nhạc
        Bitmap albumArt = MusicLibraryHelper.getThumbnail(playerService.getApplicationContext(), song.albumArt);

        // Cập nhật thông tin trong notification
        notificationBuilder
                .setContentTitle(song.title)
                .setContentText(song.artist)
                .setColor(MusicLibraryHelper.getDominantColorFromThumbnail(albumArt))
                .setLargeIcon(albumArt)
                .setStyle(notificationStyle);

        // Xóa các hành động hiện tại và thêm lại các hành động mới
        notificationBuilder.clearActions();
        notificationBuilder
                .addAction(notificationAction(PREV_ACTION))
                .addAction(notificationAction(PLAY_PAUSE_ACTION))
                .addAction(notificationAction(NEXT_ACTION));

        // Trả về notification đã được xây dựng
        return notificationBuilder.build();
    }

    // Phương thức cập nhật notification
    public void updateNotification() {
        if (notificationBuilder == null)
            return;

        // Thiết lập trạng thái "đang chơi" hoặc "đang tạm dừng"
        notificationBuilder.setOngoing(playerService.getPlayerManager().isPlaying());
        PlayerManager playerManager = playerService.getPlayerManager();
        Music song = playerManager.getCurrentMusic();
        Bitmap albumArt = MusicLibraryHelper.getThumbnail(playerService.getApplicationContext(),
                song.albumArt);

        // Xóa các hành động hiện tại và thêm lại các hành động mới
        notificationBuilder.clearActions();
        notificationBuilder
                .addAction(notificationAction(PREV_ACTION))
                .addAction(notificationAction(PLAY_PAUSE_ACTION))
                .addAction(notificationAction(NEXT_ACTION));

        // Cập nhật thông tin trong notification
        notificationBuilder
                .setLargeIcon(albumArt)
                .setColor(MusicLibraryHelper.getDominantColorFromThumbnail(albumArt))
                .setContentTitle(song.title)
                .setContentText(song.artist)
                .setColorized(true)
                .setAutoCancel(true);

        // Hiển thị notification đã cập nhật
        NotificationManagerCompat.from(playerService).notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    // Phương thức xây dựng đối tượng NotificationCompat.Action cho các hành động
    @NonNull
    private NotificationCompat.Action notificationAction(@NonNull final String action) {
        int icon = -1;
        switch (action) {
            case PREV_ACTION:
                icon = R.drawable.ic_controls_prev;
                break;
            case NEXT_ACTION:
                icon = R.drawable.ic_controls_next;
                break;
            case PLAY_PAUSE_ACTION:
                icon =
                        playerService.getPlayerManager().isPlaying()
                                ? R.drawable.ic_controls_pause
                                : R.drawable.ic_controls_play;
                break;
        }
        return new NotificationCompat.Action.Builder(icon, action, playerAction(action)).build();
    }

    // Phương thức tạo Notification Channel cho phiên bản Android >= O
    @RequiresApi(26)
    private void createNotificationChannel() {
        // Nếu Notification Channel chưa tồn tại, tạo mới nó
        if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            final NotificationChannel notificationChannel =
                    new NotificationChannel(CHANNEL_ID,
                            playerService.getString(R.string.app_name),
                            NotificationManager.IMPORTANCE_LOW);

            notificationChannel.setDescription(playerService.getString(R.string.app_name));
            notificationChannel.enableLights(false);
            notificationChannel.enableVibration(false);
            notificationChannel.setShowBadge(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
