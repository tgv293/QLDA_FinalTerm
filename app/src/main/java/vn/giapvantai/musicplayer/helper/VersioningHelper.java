package vn.giapvantai.musicplayer.helper;

import android.os.Build;

public class VersioningHelper {

    /**
     * Kiểm tra xem thiết bị có đang chạy trên phiên bản Android Q (Android 10) trở lên hay không.
     *
     * @return True nếu thiết bị đang chạy Android Q hoặc mới hơn, ngược lại là false.
     */
    public static boolean isVersionQ() {
        // SDK_INT là một số nguyên đại diện cho cấp API Android.
        // Build.VERSION_CODES.Q tương ứng với Android 10 (Q).
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    }
}
