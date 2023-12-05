package vn.giapvantai.musicplayer.player;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.annotation.NonNull;

import vn.giapvantai.musicplayer.listener.PlayerListener;

/**
 * Lớp PlayerBuilder chịu trách nhiệm xây dựng và quản lý PlayerService trong ứng dụng.
 */
public class PlayerBuilder {

    private final Context context;
    private final PlayerListener playerListener;
    private PlayerService playerService;
    private PlayerManager playerManager;

    /**
     * ServiceConnection để quản lý kết nối với PlayerService.
     */
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(@NonNull final ComponentName componentName, @NonNull final IBinder iBinder) {
            // Khi kết nối với PlayerService được thiết lập, lấy ra PlayerManager từ service.
            playerService = ((PlayerService.LocalBinder) iBinder).getInstance();
            playerManager = playerService.getPlayerManager();

            // Thiết lập PlayerListener nếu đã được chỉ định.
            if (playerListener != null) {
                playerManager.setPlayerListener(playerListener);
            }
        }

        @Override
        public void onServiceDisconnected(@NonNull final ComponentName componentName) {
            // Khi kết nối với PlayerService bị ngắt, giải phóng playerService.
            playerService = null;
        }
    };

    /**
     * Constructor của PlayerBuilder.
     *
     * @param context        Context của ứng dụng.
     * @param playerListener PlayerListener để lắng nghe sự kiện từ PlayerService.
     */
    public PlayerBuilder(Context context, PlayerListener playerListener) {
        this.context = context;
        this.playerListener = playerListener;

        // Kết nối và bắt đầu dịch vụ khi PlayerBuilder được tạo.
        bindService();
    }

    /**
     * Phương thức để lấy PlayerManager từ PlayerBuilder.
     *
     * @return PlayerManager để quản lý trạng thái và sự kiện của bộ phát nhạc.
     */
    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    /**
     * Phương thức để kết nối với PlayerService.
     */
    public void bindService() {
        // Kết nối với PlayerService và tự động tạo mới nếu chưa tồn tại.
        context.bindService(new Intent(context, PlayerService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        context.startService(new Intent(context, PlayerService.class));
    }

    /**
     * Phương thức để ngắt kết nối với PlayerService.
     */
    public void unBindService() {
        if (playerManager != null) {
            // Giải phóng PlayerManager và ngắt kết nối với PlayerService.
            playerManager.detachService();
            context.unbindService(serviceConnection);
        }
    }
}
