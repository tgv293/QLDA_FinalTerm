package vn.giapvantai.musicplayer;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Lớp quản lý và lưu trữ các thiết lập ứng dụng sử dụng SharedPreferences.
 */
public class MPPreferences {

    /**
     * Phương thức trả về Editor để chỉnh sửa SharedPreferences.
     *
     * @param context Context của ứng dụng.
     * @return Editor của SharedPreferences.
     */
    private static SharedPreferences.Editor getEditor(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                MPConstants.PACKAGE_NAME, Context.MODE_PRIVATE
        );
        return sharedPreferences.edit();
    }

    /**
     * Phương thức trả về SharedPreferences.
     *
     * @param context Context của ứng dụng.
     * @return SharedPreferences của ứng dụng.
     */
    private static SharedPreferences getSharedPref(Context context) {
        return context.getSharedPreferences(
                MPConstants.PACKAGE_NAME, Context.MODE_PRIVATE
        );
    }

    /**
     * Lưu trạng thái theme của ứng dụng.
     *
     * @param context Context của ứng dụng.
     * @param theme   Giá trị theme cần lưu.
     */
    public static void storeTheme(Context context, int theme) {
        getEditor(context).putInt(MPConstants.SETTINGS_THEME, theme).apply();
    }

    /**
     * Lấy giá trị theme đã lưu.
     *
     * @param context Context của ứng dụng.
     * @return Giá trị theme đã lưu.
     */
    public static int getTheme(Context context) {
        return getSharedPref(context).getInt(MPConstants.SETTINGS_THEME, R.color.blue);
    }

    /**
     * Lưu trạng thái yêu cầu album của ứng dụng.
     *
     * @param context Context của ứng dụng.
     * @param val     Giá trị trạng thái yêu cầu album cần lưu.
     */
    public static void storeAlbumRequest(Context context, boolean val) {
        getEditor(context).putBoolean(MPConstants.SETTINGS_ALBUM_REQUEST, val).apply();
    }

    /**
     * Lưu trạng thái Auto Play của ứng dụng.
     *
     * @param context Context của ứng dụng.
     * @param val     Giá trị trạng thái Auto Play cần lưu.
     */
    public static void storeAutoPlay(Context context, boolean val) {
        getEditor(context).putBoolean(MPConstants.SETTINGS_AUTO_PLAY, val).apply();
    }

    /**
     * Lấy giá trị trạng thái yêu cầu album đã lưu.
     *
     * @param context Context của ứng dụng.
     * @return Giá trị trạng thái yêu cầu album đã lưu.
     */
    public static boolean getAlbumRequest(Context context) {
        return getSharedPref(context).getBoolean(MPConstants.SETTINGS_ALBUM_REQUEST, false);
    }

    /**
     * Lấy giá trị trạng thái Auto Play đã lưu.
     *
     * @param context Context của ứng dụng.
     * @return Giá trị trạng thái Auto Play đã lưu.
     */
    public static boolean getAutoPlay(Context context) {
        return getSharedPref(context).getBoolean(MPConstants.SETTINGS_AUTO_PLAY, true);
    }

    /**
     * Lưu trạng thái chế độ theme của ứng dụng.
     *
     * @param context Context của ứng dụng.
     * @param theme   Giá trị chế độ theme cần lưu.
     */
    public static void storeThemeMode(Context context, int theme) {
        getEditor(context).putInt(MPConstants.SETTINGS_THEME_MODE, theme).apply();
    }

    /**
     * Lấy giá trị chế độ theme đã lưu.
     *
     * @param context Context của ứng dụng.
     * @return Giá trị chế độ theme đã lưu.
     */
    public static int getThemeMode(Context context) {
        return getSharedPref(context).getInt(MPConstants.SETTINGS_THEME_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    /**
     * Lưu danh sách thư mục được loại trừ khỏi quét nhạc của ứng dụng.
     *
     * @param context Context của ứng dụng.
     * @param folders Danh sách thư mục cần lưu.
     */
    public static void storeExcludedFolders(Context context, List<String> folders) {
        folders.removeAll(Arrays.asList("", null));
        getEditor(context).putString(MPConstants.SETTINGS_EXCLUDED_FOLDER, String.join(MPConstants.EXCLUDED_FOLDER_SEPARATOR, folders)).apply();
    }

    /**
     * Lấy danh sách thư mục được loại trừ đã lưu.
     *
     * @param context Context của ứng dụng.
     * @return Danh sách thư mục được loại trừ.
     */
    public static List<String> getExcludedFolders(Context context) {
        try {
            String[] folders = getSharedPref(context).getString(MPConstants.SETTINGS_EXCLUDED_FOLDER, "").split(MPConstants.EXCLUDED_FOLDER_SEPARATOR);
            return new ArrayList<>(Arrays.asList(folders));
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
