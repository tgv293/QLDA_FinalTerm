package vn.giapvantai.musicplayer.player;

import vn.giapvantai.musicplayer.model.Music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Lớp quản lý hàng đợi phát nhạc của người chơi âm nhạc.
 */
public class PlayerQueue {
    private static PlayerQueue instance = null;
    private final Random random = new Random();
    private List<Music> currentQueue;
    private List<Integer> played;
    private boolean shuffle = false;
    private boolean repeat = false;
    private int currentPosition = 0;

    /**
     * Phương thức để lấy ra một đối tượng PlayerQueue, sử dụng Singleton Pattern.
     *
     * @return Đối tượng PlayerQueue
     */
    public static PlayerQueue getInstance() {
        if (instance == null) {
            instance = new PlayerQueue();
        }
        return instance;
    }

    /**
     * Kiểm tra xem vị trí hiện tại có vượt quá giới hạn không.
     *
     * @param pos Vị trí cần kiểm tra.
     * @return True nếu vị trí hiện tại vượt quá giới hạn, ngược lại là False.
     */
    private boolean isCurrentPositionOutOfBound(int pos) {
        return pos >= currentQueue.size() || pos < 0;
    }

    /**
     * Kiểm tra xem chế độ shuffle có được bật hay không.
     *
     * @return True nếu chế độ shuffle được bật, ngược lại là False.
     */
    public boolean isShuffle() {
        return shuffle;
    }

    /**
     * Bật hoặc tắt chế độ shuffle.
     *
     * @param shuffle True để bật chế độ shuffle, False để tắt.
     */
    public void setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
    }

    /**
     * Kiểm tra xem chế độ lặp lại có được bật hay không.
     *
     * @return True nếu chế độ lặp lại được bật, ngược lại là False.
     */
    public boolean isRepeat() {
        return repeat;
    }

    /**
     * Bật hoặc tắt chế độ lặp lại.
     *
     * @param repeat True để bật chế độ lặp lại, False để tắt.
     */
    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    /**
     * Lấy danh sách nhạc hiện tại trong hàng đợi.
     *
     * @return Danh sách nhạc hiện tại.
     */
    public List<Music> getCurrentQueue() {
        return currentQueue;
    }

    /**
     * Đặt danh sách nhạc hiện tại cho hàng đợi và thiết lập vị trí hiện tại.
     *
     * @param currentQueue Danh sách nhạc hiện tại.
     */
    public void setCurrentQueue(List<Music> currentQueue) {
        this.played = new ArrayList<>();
        this.currentQueue = currentQueue;
        this.currentPosition = 0;
        if (this.shuffle) {
            Collections.shuffle(currentQueue);
        }
    }

    /**
     * Lấy vị trí hiện tại trong danh sách nhạc.
     *
     * @return Vị trí hiện tại.
     */
    public int getCurrentPosition() {
        return currentPosition;
    }

    /**
     * Lấy đối tượng Music hiện tại đang phát.
     *
     * @return Đối tượng Music hiện tại.
     */
    public Music getCurrentMusic() {
        return currentQueue.get(currentPosition);
    }

    /**
     * Thêm danh sách nhạc vào cuối hàng đợi.
     *
     * @param music Danh sách nhạc cần thêm.
     */
    public void addMusicListToQueue(List<Music> music) {
        currentQueue.addAll(music);
        this.currentPosition = (shuffle) ? random.nextInt(currentQueue.size()) : 0;
    }

    /**
     * Chuyển đến bài hát tiếp theo trong hàng đợi.
     */
    public void next() {
        played.add(currentPosition);
        if (!repeat) {
            currentPosition = (shuffle)
                    ? random.nextInt(currentQueue.size())
                    : isCurrentPositionOutOfBound(currentPosition + 1)
                    ? 0
                    : ++currentPosition;
        }
    }

    /**
     * Quay lại bài hát trước đó trong hàng đợi.
     */
    public void prev() {
        if (played.size() == 0) {
            currentPosition = 0;
        } else {
            int lastPosition = played.size() - 1;
            currentPosition = played.get(lastPosition);
            played.remove(lastPosition);
        }
    }

    /**
     * Xóa bài hát khỏi hàng đợi tại vị trí cho trước.
     *
     * @param position Vị trí cần xóa.
     */
    public void removeMusicFromQueue(int position) {
        if (!isCurrentPositionOutOfBound(position)) {
            currentQueue.remove(position);
            if (currentPosition > position)
                currentPosition -= 1;
        }
    }

    /**
     * Hoán đổi vị trí của hai bài hát trong hàng đợi.
     *
     * @param one Vị trí của bài hát thứ nhất.
     * @param two Vị trí của bài hát thứ hai.
     */
    public void swap(int one, int two) {
        if (!isCurrentPositionOutOfBound(one) && !isCurrentPositionOutOfBound(two)) {
            if (one == currentPosition) {
                currentPosition = two;
            } else if (two == currentPosition) {
                currentPosition = one;
            }
            Collections.swap(currentQueue, one, two);
        }
    }
}
