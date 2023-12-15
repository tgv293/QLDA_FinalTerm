package vn.giapvantai.musicplayer.helper;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import vn.giapvantai.musicplayer.MPConstants;

public class PermissionHelper {

    /**
     * Kiểm tra quyền đọc bộ nhớ đã được cấp cho ứng dụng hay chưa.
     *
     * @param context Activity hiện tại
     * @return True nếu có quyền, ngược lại False
     */
    public static boolean hasReadStoragePermission(Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    /**
     * Kiểm tra quyền thông báo đã được cấp cho ứng dụng hay chưa.
     *
     * @param context Activity hiện tại
     * @return True nếu có quyền, ngược lại False
     */
    public static boolean hasNotificationPermission(Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    /**
     * Kiểm tra xem có cần hiển thị lời giải thích về quyền hay không.
     *
     * @param context Activity hiện tại
     * @return True nếu cần hiển thị lời giải thích, ngược lại False
     */
    public static boolean requirePermissionRationale(Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.READ_MEDIA_AUDIO);
        } else {
            return ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    /**
     * Yêu cầu quyền đọc bộ nhớ từ người dùng.
     *
     * @param context Activity hiện tại
     */
    public static void requestStoragePermission(Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                    context,
                    new String[]{Manifest.permission.READ_MEDIA_AUDIO},
                    MPConstants.PERMISSION_READ_STORAGE
            );
        } else {
            ActivityCompat.requestPermissions(
                    context,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MPConstants.PERMISSION_READ_STORAGE
            );
        }
    }

    /**
     * Yêu cầu quyền thông báo từ người dùng.
     *
     * @param context Activity hiện tại
     */
    public static void requestNotificationPermission(Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                    context,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    MPConstants.PERMISSION_READ_STORAGE
            );
        }
    }
}
