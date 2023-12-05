package vn.giapvantai.musicplayer.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import vn.giapvantai.musicplayer.fragments.AlbumsFragment;
import vn.giapvantai.musicplayer.fragments.ArtistsFragment;
import vn.giapvantai.musicplayer.fragments.SettingsFragment;
import vn.giapvantai.musicplayer.fragments.SongsFragment;
import vn.giapvantai.musicplayer.listener.MusicSelectListener;

import java.util.ArrayList;
import java.util.List;

public class MainPagerAdapter extends FragmentStateAdapter {

    private final MusicSelectListener selectListener;
    private final List<Fragment> fragments;

    public MainPagerAdapter(FragmentActivity fragmentActivity, MusicSelectListener selectListener) {
        super(fragmentActivity);
        this.selectListener = selectListener;

        // Khởi tạo danh sách các fragment
        fragments = new ArrayList<>();
        setupFragments();
    }

    private void setupFragments() {
        // Thêm các fragment vào danh sách
        fragments.add(SongsFragment.newInstance(selectListener));
        fragments.add(ArtistsFragment.newInstance());
        fragments.add(AlbumsFragment.newInstance());
        fragments.add(SettingsFragment.newInstance());
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Trả về fragment tại vị trí cụ thể trong danh sách
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        // Trả về tổng số lượng fragment
        return fragments.size();
    }
}
