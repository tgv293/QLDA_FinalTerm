package vn.giapvantai.musicplayer.helper;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import vn.giapvantai.musicplayer.MPConstants;
import vn.giapvantai.musicplayer.R;
import vn.giapvantai.musicplayer.model.Music;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MusicLibraryHelper {

    /**
     * Lấy danh sách bài hát từ thư viện âm nhạc của thiết bị.
     *
     * @param context Context của ứng dụng
     * @return Danh sách bài hát
     */
    public static List<Music> fetchMusicLibrary(Context context) {
        String collection;
        List<Music> musicList = new ArrayList<>();

        // Chọn cách lấy dữ liệu dựa trên phiên bản Android
        if (VersioningHelper.isVersionQ())
            collection = MediaStore.Audio.Media.BUCKET_DISPLAY_NAME;
        else
            collection = MediaStore.Audio.Media.DATA;

        String[] projection = new String[]{
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.YEAR,
                MediaStore.Audio.Media.TRACK,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,  // Lỗi từ phía Android, hoạt động < 29
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.ALBUM,
                collection,
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATE_MODIFIED,
                MediaStore.Audio.Media.DATA
        };

        String selection = MediaStore.Audio.Media.IS_MUSIC + " = 1";
        String sortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;

        Cursor musicCursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null, sortOrder);

        int artistInd = Objects.requireNonNull(musicCursor).getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
        int yearInd = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR);
        int trackInd = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK);
        int titleInd = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
        int displayNameInd = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
        int durationInd = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
        int albumIdInd = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);
        int albumInd = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
        int relativePathInd = musicCursor.getColumnIndexOrThrow(collection);
        int idInd = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
        int dateModifiedInd = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED);
        int contentUriInd = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);

        while (musicCursor.moveToNext()) {
            String artist = musicCursor.getString(artistInd);
            String title = musicCursor.getString(titleInd);
            String displayName = musicCursor.getString(displayNameInd);
            String album = musicCursor.getString(albumInd);
            String relativePath = musicCursor.getString(relativePathInd);
            String absolutePath = musicCursor.getString(contentUriInd);

            // Chỉnh sửa đường dẫn tương đối dựa trên phiên bản Android
            if (VersioningHelper.isVersionQ())
                relativePath += "/";
            else if (relativePath != null) {
                File check = new File(relativePath).getParentFile();
                if (check != null) {
                    relativePath = check.getName() + "/";
                }
            } else {
                relativePath = "/";
            }

            int year = musicCursor.getInt(yearInd);
            int track = musicCursor.getInt(trackInd);
            int startFrom = 0;
            int dateAdded = musicCursor.getInt(dateModifiedInd);

            long id = musicCursor.getLong(idInd);
            long duration = musicCursor.getLong(durationInd);
            long albumId = musicCursor.getLong(albumIdInd);

            // Bỏ qua những bài hát ngắn hơn 20 giây
            if (duration < MPConstants.TWENTY_SECONDS_IN_MS) {
                continue;
            }

            Uri albumArt = Uri.parse("");
            if (!relativePath.contains(album)) {
                albumArt = ContentUris.withAppendedId(Uri.parse(context.getResources().getString(R.string.album_art_dir)), albumId);
            }

            musicList.add(new Music(
                    artist, title, displayName, album, relativePath, absolutePath,
                    year, track, startFrom, dateAdded,
                    id, duration, albumId, albumArt
            ));
        }

        if (!musicCursor.isClosed())
            musicCursor.close();

        return musicList;
    }

    /**
     * Lấy ảnh thumbnail từ một đường dẫn Uri.
     *
     * @param context Context của ứng dụng
     * @param uri     Đường dẫn Uri của ảnh
     * @return Bitmap ảnh thumbnail hoặc null nếu không thành công
     */
    public static Bitmap getThumbnail(Context context, String uri) {
        try {
            ParcelFileDescriptor fileDescriptor = context.getContentResolver().openFileDescriptor(Uri.parse(uri), "r");
            if (uri == null)
                return null;

            Bitmap bitmap = BitmapFactory.decodeFileDescriptor(Objects.requireNonNull(fileDescriptor).getFileDescriptor());
            fileDescriptor.close();

            return bitmap;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Lấy màu chủ đạo từ ảnh thumbnail.
     *
     * @param bitmap Ảnh thumbnail
     * @return Màu chủ đạo hoặc đen nếu không có ảnh
     */
    public static int getDominantColorFromThumbnail(Bitmap bitmap) {
        if (bitmap == null) return Color.BLACK;
        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, 1, 1, true);
        final int color = newBitmap.getPixel(0, 0);
        newBitmap.recycle();
        return color;
    }

    /**
     * Định dạng thời lượng từ milliseconds thành chuỗi hh:mm:ss.
     *
     * @param duration Thời lượng bài hát
     * @return Chuỗi thời lượng đã định dạng
     */
    public static String formatDuration(long duration) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);
        String second = String.valueOf(seconds);

        // Đảm bảo là chuỗi có ít nhất 2 chữ số
        if (second.length() == 1)
            second = "0" + second;
        else
            second = second.substring(0, 2);

        return String.format(Locale.getDefault(), "%02dm %ss",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                second
        );
    }

    /**
     * Định dạng thời lượng từ milliseconds thành chuỗi mm:ss.
     *
     * @param duration Thời lượng bài hát
     * @return Chuỗi thời lượng đã định dạng
     */
    public static String formatDurationTimeStyle(long duration) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);
        String second = String.valueOf(seconds);

        // Đảm bảo là chuỗi có ít nhất 2 chữ số
        if (second.length() == 1)
            second = "0" + second;
        else
            second = second.substring(0, 2);

        return String.format(Locale.getDefault(), "%02d:%s",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                second
        );
    }

    /**
     * Định dạng ngày thêm bài hát.
     *
     * @param dateAdded Ngày thêm bài hát (timestamp)
     * @return Chuỗi ngày đã định dạng
     */
    public static String formatDate(long dateAdded) {
        SimpleDateFormat fromFormat = new SimpleDateFormat("s", Locale.getDefault());
        SimpleDateFormat toFormat = new SimpleDateFormat("d MMM yyyy", Locale.getDefault());

        try {
            Date date = fromFormat.parse(String.valueOf(dateAdded));
            assert date != null;
            return toFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Lấy thông số mẫu của âm nhạc.
     *
     * @param music Thông tin bài hát
     * @return Mảng chứa sample rate và bit rate
     */
    public static int[] getBitSampleRates(Music music) {
        try {
            MediaExtractor extractor = new MediaExtractor();
            extractor.setDataSource(music.absolutePath);

            MediaFormat format = extractor.getTrackFormat(0);
            int sample = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
            int bitrate = format.getInteger(MediaFormat.KEY_BIT_RATE);
            int rate = Math.abs(bitrate / 1000);

            return new int[]{
                    sample,
                    normalizeRate(rate)
            };

        } catch (Exception e) {
            return new int[]{0, 0};
        }
    }

    /**
     * Chuẩn hóa giá trị bitrate.
     *
     * @param rate Bitrate
     * @return Bitrate được chuẩn hóa
     */
    private static int normalizeRate(int rate) {
        return (rate > 320) ? 320 : 120;
    }
}
