package vn.giapvantai.musicplayer.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import vn.giapvantai.musicplayer.R;
import vn.giapvantai.musicplayer.adapter.SongsAdapter;
import vn.giapvantai.musicplayer.helper.ListHelper;
import vn.giapvantai.musicplayer.listener.MusicSelectListener;
import vn.giapvantai.musicplayer.model.Music;
import vn.giapvantai.musicplayer.viewmodel.MainViewModel;

public class SongsFragment extends Fragment implements SearchView.OnQueryTextListener {

    private static MusicSelectListener listener;
    private final List<Music> musicList = new ArrayList<>();
    private MainViewModel viewModel;
    private SongsAdapter songsAdapter;
    private List<Music> unChangedList = new ArrayList<>();

    private MaterialToolbar toolbar;
    private SearchView searchView;
    private ExtendedFloatingActionButton shuffleControl;

    public SongsFragment() {
        // Unused
    }

    public static SongsFragment newInstance(MusicSelectListener selectListener) {
        SongsFragment.listener = selectListener;
        return new SongsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_songs, container, false);

        // Khởi tạo các thành phần giao diện
        toolbar = view.findViewById(R.id.search_toolbar);
        shuffleControl = view.findViewById(R.id.shuffle_button);

        RecyclerView recyclerView = view.findViewById(R.id.songs_layout);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        songsAdapter = new SongsAdapter(listener, musicList);
        recyclerView.setAdapter(songsAdapter);

        // Thiết lập sự kiện click cho nút shuffle
        shuffleControl.setOnClickListener(v -> listener.playQueue(musicList, true));
        viewModel.getSongsList().observe(requireActivity(), this::setUpUi);

        // Thiết lập các tùy chọn
        setUpOptions();
        return view;
    }

    private void setUpUi(List<Music> songList) {
        // Cập nhật giao diện khi danh sách bài hát thay đổi
        unChangedList = songList;
        musicList.clear();
        musicList.addAll(unChangedList);
        shuffleControl.setText(String.valueOf(songList.size()));
    }

    private void setUpOptions() {
        // Thiết lập sự kiện khi nhấn vào các tùy chọn trên thanh toolbar
        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.search) {
                // Hiển thị ô tìm kiếm khi nhấn vào nút search
                searchView = (SearchView) item.getActionView();
                setUpSearchView();
                return true;
            } else if (id == R.id.menu_sort_asc) {
                // Sắp xếp danh sách theo thứ tự tăng dần
                updateAdapter(ListHelper.sortMusic(musicList, false));
                return true;
            } else if (id == R.id.menu_sort_dec) {
                // Sắp xếp danh sách theo thứ tự giảm dần
                updateAdapter(ListHelper.sortMusic(musicList, true));
                return true;
            } else if (id == R.id.menu_newest_first) {
                // Sắp xếp danh sách theo thứ tự mới nhất đến cũ nhất
                updateAdapter(ListHelper.sortMusicByDateAdded(musicList, false));
                return true;
            } else if (id == R.id.menu_oldest_first) {
                // Sắp xếp danh sách theo thứ tự cũ nhất đến mới nhất
                updateAdapter(ListHelper.sortMusicByDateAdded(musicList, true));
                return true;
            }

            return false;
        });

        // Thiết lập sự kiện khi nhấn nút back trên thanh toolbar
        toolbar.setNavigationOnClickListener(v -> {
            if (searchView == null || searchView.isIconified())
                requireActivity().finish();
        });
    }

    private void setUpSearchView() {
        // Thiết lập ô tìm kiếm
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        onQueryTextChange(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        setSearchResult(query);
        return true;
    }

    private void setSearchResult(String query) {
        if (query.length() > 0) {
            // Tìm kiếm theo tên bài hát khi có ký tự nhập vào ô tìm kiếm
            updateAdapter(ListHelper.searchMusicByName(unChangedList, query.toLowerCase()));
        } else {
            // Hiển thị toàn bộ danh sách khi ô tìm kiếm trống
            updateAdapter(unChangedList);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateAdapter(List<Music> list) {
        // Cập nhật danh sách và số lượng bài hát hiển thị trên nút shuffle
        musicList.clear();
        musicList.addAll(list);
        songsAdapter.notifyDataSetChanged();
        shuffleControl.setText(String.valueOf(musicList.size()));
    }
}
