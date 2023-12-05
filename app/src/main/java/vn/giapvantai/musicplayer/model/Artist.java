package vn.giapvantai.musicplayer.model;

import android.os.Parcel;
import android.os.Parcelable;

import vn.giapvantai.musicplayer.helper.ListHelper;

import java.util.List;

/**
 * Đối tượng Artist chứa thông tin về nghệ sĩ, bao gồm tên, danh sách album, số bài hát và số album.
 */
public class Artist implements Parcelable {
    public static final Creator<Artist> CREATOR = new Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel in) {
            return new Artist(in);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };

    // Các thuộc tính của Artist
    public String name;
    public List<Album> albums;
    public int songCount;
    public int albumCount;

    /**
     * Constructor của Artist.
     *
     * @param name       Tên của nghệ sĩ.
     * @param albums     Danh sách album của nghệ sĩ.
     * @param songCount  Số lượng bài hát của nghệ sĩ.
     * @param albumCount Số lượng album của nghệ sĩ.
     */
    public Artist(String name, List<Album> albums, int songCount, int albumCount) {
        this.name = ListHelper.ifNull(name);
        this.albums = albums;
        this.songCount = songCount;
        this.albumCount = albumCount;
    }

    /**
     * Constructor Parcelable của Artist.
     *
     * @param in Parcel để đọc dữ liệu.
     */
    protected Artist(Parcel in) {
        name = in.readString();
        albums = in.createTypedArrayList(Album.CREATOR);
        songCount = in.readInt();
        albumCount = in.readInt();
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
        dest.writeString(name);
        dest.writeTypedList(albums);
        dest.writeInt(songCount);
        dest.writeInt(albumCount);
    }
}
