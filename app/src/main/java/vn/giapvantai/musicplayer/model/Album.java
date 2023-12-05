package vn.giapvantai.musicplayer.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import vn.giapvantai.musicplayer.helper.ListHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Đối tượng Album chứa thông tin về một album âm nhạc, bao gồm tiêu đề, năm, nghệ sĩ, thời lượng và danh sách các bài hát.
 */
public class Album implements Parcelable {
    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    // Các thuộc tính của Album
    public String title;
    public String year;
    public String artist;
    public Long duration;
    public List<Music> music;

    /**
     * Constructor của Album.
     *
     * @param artist   Nghệ sĩ của album.
     * @param title    Tiêu đề của album.
     * @param year     Năm phát hành của album.
     * @param duration Thời lượng của album.
     * @param music    Danh sách các bài hát trong album.
     */
    public Album(String artist, String title, String year, Long duration, List<Music> music) {
        this.artist = ListHelper.ifNull(artist);
        this.title = ListHelper.ifNull(title);
        this.year = ListHelper.ifNull(year);
        this.duration = duration;
        this.music = music;
    }

    /**
     * Constructor Parcelable của Album.
     *
     * @param in Parcel để đọc dữ liệu.
     */
    protected Album(Parcel in) {
        title = in.readString();
        year = in.readString();
        artist = in.readString();
        music = new ArrayList<>();
        in.readTypedList(music, Music.CREATOR);

        if (in.readByte() == 0) {
            duration = null;
        } else {
            duration = in.readLong();
        }
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
        dest.writeString(title);
        dest.writeString(year);
        dest.writeString(artist);
        dest.writeTypedList(music);

        if (duration == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(duration);
        }
    }

    /**
     * Phương thức toString giúp chuyển đối tượng thành một xâu kí tự để dễ dàng hiển thị và debug.
     *
     * @return Xâu kí tự biểu diễn đối tượng Album.
     */
    @NonNull
    @Override
    public String toString() {
        return "Album{" +
                "title='" + title + '\'' +
                ", year='" + year + '\'' +
                ", artist='" + artist + '\'' +
                ", duration=" + duration +
                ", music=" + music +
                '}';
    }
}
