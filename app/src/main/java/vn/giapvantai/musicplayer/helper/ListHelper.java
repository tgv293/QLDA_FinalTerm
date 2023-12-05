package vn.giapvantai.musicplayer.helper;

import vn.giapvantai.musicplayer.MPConstants;
import vn.giapvantai.musicplayer.model.Album;
import vn.giapvantai.musicplayer.model.Artist;
import vn.giapvantai.musicplayer.model.Music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Lớp trợ giúp thực hiện các thao tác trên danh sách âm nhạc, nghệ sĩ và album.
 */
public class ListHelper {

    /**
     * Tìm kiếm danh sách âm nhạc theo tên.
     *
     * @param list  Danh sách âm nhạc cần tìm kiếm.
     * @param query Từ khóa tìm kiếm.
     * @return Danh sách âm nhạc đã lọc theo tên.
     */
    public static List<Music> searchMusicByName(List<Music> list, String query) {
        List<Music> filterList = new ArrayList<>();
        for (Music m : list) {
            if ((m.title.toLowerCase().contains(query) || m.displayName.toLowerCase().contains(query)) ||
                    (m.artist.toLowerCase().contains(query) || m.album.toLowerCase().contains(query))) {
                filterList.add(m);
            }
        }
        return filterList;
    }

    /**
     * Sắp xếp danh sách âm nhạc theo ngày thêm vào.
     *
     * @param list    Danh sách âm nhạc cần sắp xếp.
     * @param reverse True nếu muốn sắp xếp ngược lại, ngược lại là False.
     * @return Danh sách âm nhạc đã sắp xếp theo ngày thêm vào.
     */
    public static List<Music> sortMusicByDateAdded(List<Music> list, boolean reverse) {
        List<Music> newList = new ArrayList<>(list);
        newList.sort(new SongComparator(MPConstants.SORT_MUSIC_BY_DATE_ADDED));

        if (reverse)
            Collections.reverse(newList);

        return newList;
    }

    /**
     * Sắp xếp danh sách âm nhạc theo tiêu đề.
     *
     * @param list    Danh sách âm nhạc cần sắp xếp.
     * @param reverse True nếu muốn sắp xếp ngược lại, ngược lại là False.
     * @return Danh sách âm nhạc đã sắp xếp theo tiêu đề.
     */
    public static List<Music> sortMusic(List<Music> list, boolean reverse) {
        List<Music> newList = new ArrayList<>(list);
        newList.sort(new SongComparator(MPConstants.SORT_MUSIC_BY_TITLE));

        if (reverse)
            Collections.reverse(newList);

        return newList;
    }

    /**
     * Tìm kiếm danh sách nghệ sĩ theo tên.
     *
     * @param artistList Danh sách nghệ sĩ cần tìm kiếm.
     * @param query      Từ khóa tìm kiếm.
     * @return Danh sách nghệ sĩ đã lọc theo tên.
     */
    public static List<Artist> searchArtistByName(List<Artist> artistList, String query) {
        List<Artist> filterList = new ArrayList<>();
        for (Artist a : artistList) {
            if (a.name.toLowerCase().contains(query)) {
                filterList.add(a);
            }
        }
        return filterList;
    }

    /**
     * Sắp xếp danh sách nghệ sĩ theo tên.
     *
     * @param artistList Danh sách nghệ sĩ cần sắp xếp.
     * @param reverse    True nếu muốn sắp xếp ngược lại, ngược lại là False.
     * @return Danh sách nghệ sĩ đã sắp xếp theo tên.
     */
    public static List<Artist> sortArtistByName(List<Artist> artistList, boolean reverse) {
        List<Artist> list = new ArrayList<>(artistList);
        list.sort(new ArtistComparator(MPConstants.SORT_ARTIST_BY_NAME));

        if (reverse)
            Collections.reverse(list);

        return list;
    }

    /**
     * Sắp xếp danh sách nghệ sĩ theo số bài hát.
     *
     * @param artistList Danh sách nghệ sĩ cần sắp xếp.
     * @param reverse    True nếu muốn sắp xếp ngược lại, ngược lại là False.
     * @return Danh sách nghệ sĩ đã sắp xếp theo số bài hát.
     */
    public static List<Artist> sortArtistBySongs(List<Artist> artistList, boolean reverse) {
        List<Artist> list = new ArrayList<>(artistList);
        list.sort(new ArtistComparator(MPConstants.SORT_ARTIST_BY_SONGS));

        if (reverse)
            Collections.reverse(list);

        return list;
    }

    /**
     * Sắp xếp danh sách nghệ sĩ theo số album.
     *
     * @param artistList Danh sách nghệ sĩ cần sắp xếp.
     * @param reverse    True nếu muốn sắp xếp ngược lại, ngược lại là False.
     * @return Danh sách nghệ sĩ đã sắp xếp theo số album.
     */
    public static List<Artist> sortArtistByAlbums(List<Artist> artistList, boolean reverse) {
        List<Artist> list = new ArrayList<>(artistList);
        list.sort(new ArtistComparator(MPConstants.SORT_ARTIST_BY_ALBUMS));

        if (reverse)
            Collections.reverse(list);

        return list;
    }

    /**
     * Tìm kiếm danh sách album theo tên.
     *
     * @param albumList Danh sách album cần tìm kiếm.
     * @param query     Từ khóa tìm kiếm.
     * @return Danh sách album đã lọc theo tên.
     */
    public static List<Album> searchByAlbumName(List<Album> albumList, String query) {
        List<Album> filterList = new ArrayList<>();
        for (Album a : albumList) {
            if (a.title.toLowerCase().contains(query) || a.artist.toLowerCase().equals(query)) {
                filterList.add(a);
            }
        }
        return filterList;
    }

    /**
     * Sắp xếp danh sách album theo tên.
     *
     * @param albumList Danh sách album cần sắp xếp.
     * @param reverse   True nếu muốn sắp xếp ngược lại, ngược lại là False.
     * @return Danh sách album đã sắp xếp theo tên.
     */
    public static List<Album> sortAlbumByName(List<Album> albumList, boolean reverse) {
        List<Album> list = new ArrayList<>(albumList);
        list.sort(new AlbumComparator(MPConstants.SORT_ALBUM_BY_TITLE));

        if (reverse)
            Collections.reverse(list);

        return list;
    }

    /**
     * Sắp xếp danh sách album theo số bài hát.
     *
     * @param albumList Danh sách album cần sắp xếp.
     * @param reverse   True nếu muốn sắp xếp ngược lại, ngược lại là False.
     * @return Danh sách album đã sắp xếp theo số bài hát.
     */
    public static List<Album> sortAlbumBySongs(List<Album> albumList, boolean reverse) {
        List<Album> list = new ArrayList<>(albumList);
        list.sort(new AlbumComparator(MPConstants.SORT_ALBUM_BY_SONGS));

        if (reverse)
            Collections.reverse(list);

        return list;
    }

    /**
     * Sắp xếp danh sách album theo thời lượng.
     *
     * @param albumList Danh sách album cần sắp xếp.
     * @param reverse   True nếu muốn sắp xếp ngược lại, ngược lại là False.
     * @return Danh sách album đã sắp xếp theo thời lượng.
     */
    public static List<Album> sortAlbumByDuration(List<Album> albumList, boolean reverse) {
        List<Album> list = new ArrayList<>(albumList);
        list.sort(new AlbumComparator(MPConstants.SORT_ALBUM_BY_DURATION));

        if (reverse)
            Collections.reverse(list);

        return list;
    }

    /**
     * Kiểm tra và trả về chuỗi rỗng nếu giá trị là null.
     *
     * @param val Giá trị cần kiểm tra.
     * @return unknown nếu giá trị là null, ngược lại trả về giá trị ban đầu.
     */
    public static String ifNull(String val) {
        return val == null ? "unknown" : val;
    }

    /**
     * Lớp so sánh âm nhạc.
     */
    public static class SongComparator implements Comparator<Music> {
        private final int mode;

        public SongComparator(int mode) {
            this.mode = mode;
        }

        @Override
        public int compare(Music m1, Music m2) {
            if (mode == MPConstants.SORT_MUSIC_BY_TITLE)
                return m1.title.compareTo(m2.title);

            else if (mode == MPConstants.SORT_MUSIC_BY_DATE_ADDED)
                return Long.compare(m2.dateAdded, m1.dateAdded);

            return 0;
        }
    }

    /**
     * Lớp so sánh nghệ sĩ.
     */
    static class ArtistComparator implements Comparator<Artist> {
        private final int mode;

        public ArtistComparator(int mode) {
            this.mode = mode;
        }

        @Override
        public int compare(Artist a1, Artist a2) {
            if (mode == MPConstants.SORT_ARTIST_BY_NAME)
                return a1.name.compareTo(a2.name);

            else if (mode == MPConstants.SORT_ARTIST_BY_SONGS)
                return Integer.compare(a2.songCount, a1.songCount);

            else if (mode == MPConstants.SORT_ARTIST_BY_ALBUMS)
                return Integer.compare(a2.albumCount, a1.albumCount);

            return 0;
        }
    }

    /**
     * Lớp so sánh album.
     */
    public static class AlbumComparator implements Comparator<Album> {
        private final int mode;

        public AlbumComparator(int mode) {
            this.mode = mode;
        }

        @Override
        public int compare(Album a1, Album a2) {
            if (mode == MPConstants.SORT_ALBUM_BY_TITLE)
                return a1.title.compareTo(a2.title);

            else if (mode == MPConstants.SORT_ALBUM_BY_SONGS)
                return Integer.compare(a2.music.size(), a1.music.size());

            else if (mode == MPConstants.SORT_ALBUM_BY_DURATION)
                return Long.compare(a2.duration, a1.duration);

            return 0;
        }
    }
}
