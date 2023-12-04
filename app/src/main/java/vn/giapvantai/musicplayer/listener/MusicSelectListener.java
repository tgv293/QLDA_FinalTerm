package vn.giapvantai.musicplayer.listener;

import java.util.List;

import vn.giapvantai.musicplayer.model.Music;

public interface MusicSelectListener {
    void playQueue(List<Music> musicList, boolean shuffle);

    void addToQueue(List<Music> musicList);

    void refreshMediaLibrary();
}
