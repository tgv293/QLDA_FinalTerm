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
     * Kiểm tra và trả về chuỗi rỗng nếu giá trị là null.
     *
     * @param val Giá trị cần kiểm tra.
     * @return unknown nếu giá trị là null, ngược lại trả về giá trị ban đầu.
     */
    public static String ifNull(String val) {
        return val == null ? "unknown" : val;
    }

  
}
