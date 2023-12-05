package vn.giapvantai.musicplayer.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import vn.giapvantai.musicplayer.helper.ListHelper;

/**
 * Lớp đối tượng Music chứa thông tin về bài hát, bao gồm nghệ sĩ, tiêu đề, tên hiển thị, album, đường dẫn tương đối,
 * đường dẫn tuyệt đối, năm, số thứ tự, thời gian bắt đầu, ngày thêm, ID, thời lượng, ID album và đường dẫn ảnh album.
 */
public class Music implements Parcelable {

    // Interface Creator cho việc tạo đối tượng Music từ Parcel
    public static final Creator<Music> CREATOR = new Creator<Music>() {
        @Override
        public Music createFromParcel(Parcel in) {
            return new Music(in);
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[size];
        }
    };

    // Các thuộc tính của Music
    public String artist;
    public String title;
    public String displayName;
    public String album;
    public String relativePath;
    public String absolutePath;
    public String albumArt;
    public int year;
    public int track;
    public int startFrom;
    public long dateAdded;
    public long id;
    public long duration;
    public long albumId;

    /**
     * Constructor của Music.
     *
     * @param artist      Tên nghệ sĩ của bài hát.
     * @param title       Tiêu đề của bài hát.
     * @param displayName Tên hiển thị của bài hát.
     * @param album       Album của bài hát.
     * @param relativePath Đường dẫn tương đối của bài hát.
     * @param absolutePath Đường dẫn tuyệt đối của bài hát.
     * @param year        Năm phát hành của bài hát.
     * @param track       Số thứ tự của bài hát trong album.
     * @param startFrom   Thời gian bắt đầu của bài hát.
     * @param dateAdded   Ngày thêm bài hát.
     * @param id          ID của bài hát.
     * @param duration    Thời lượng của bài hát.
     * @param albumId     ID của album chứa bài hát.
     * @param albumArt    Đường dẫn ảnh album của bài hát.
     */
    public Music(String artist, String title, String displayName, String album, String relativePath, String absolutePath,
                 int year, int track, int startFrom, long dateAdded,
                 long id, long duration, long albumId,
                 Uri albumArt) {
        this.artist = ListHelper.ifNull(artist);
        this.title = ListHelper.ifNull(title);
        this.displayName = ListHelper.ifNull(displayName);
        this.album = ListHelper.ifNull(album);
        this.relativePath = ListHelper.ifNull(relativePath);
        this.absolutePath = ListHelper.ifNull(absolutePath);
        this.year = year;
        this.track = track;
        this.startFrom = startFrom;
        this.dateAdded = dateAdded;
        this.id = id;
        this.duration = duration;
        this.albumId = albumId;
        this.albumArt = albumArt.toString();
    }

    /**
     * Constructor Parcelable của Music.
     *
     * @param in Parcel để đọc dữ liệu.
     */
    protected Music(Parcel in) {
        artist = in.readString();
        title = in.readString();
        displayName = in.readString();
        album = in.readString();
        relativePath = in.readString();
        absolutePath = in.readString();
        albumArt = in.readString();
        year = in.readInt();
        track = in.readInt();
        startFrom = in.readInt();
        dateAdded = in.readLong();
        id = in.readLong();
        duration = in.readLong();
        albumId = in.readLong();
    }

    /**
     * Phương thức để mô tả nhanh về nội dung của đối tượng.
     *
     * @return Chuỗi mô tả nhanh.
     */
    @NonNull
    @Override
    public String toString() {
        return "Music{" +
                "artist='" + artist + '\'' +
                ", title='" + title + '\'' +
                ", displayName='" + displayName + '\'' +
                ", album='" + album + '\'' +
                ", relativePath='" + relativePath + '\'' +
                ", absolutePath='" + absolutePath + '\'' +
                ", year=" + year +
                ", track=" + track +
                ", startFrom=" + startFrom +
                ", dateAdded=" + dateAdded +
                ", id=" + id +
                ", duration=" + duration +
                ", albumId=" + albumId +
                ", albumArt=" + albumArt +
                '}';
    }

    /**
     * Phương thức để mô tả nhanh về nội dung của đối tượng.
     *
     * @return Số nguyên mô tả nhanh.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Ghi dữ liệu vào Parcel để truyền giữa các thành phần.
     *
     * @param dest  Parcel để ghi dữ liệu.
     * @param flags Các cờ mô tả cách dữ liệu sẽ được đọc.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(artist);
        dest.writeString(title);
        dest.writeString(displayName);
        dest.writeString(album);
        dest.writeString(relativePath);
        dest.writeString(absolutePath);
        dest.writeString(albumArt);
        dest.writeInt(year);
        dest.writeInt(track);
        dest.writeInt(startFrom);
        dest.writeLong(dateAdded);
        dest.writeLong(id);
        dest.writeLong(duration);
        dest.writeLong(albumId);
    }
}
