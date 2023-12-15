package vn.giapvantai.musicplayer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

import vn.giapvantai.musicplayer.R;
import vn.giapvantai.musicplayer.activities.SelectedAlbumActivity;
import vn.giapvantai.musicplayer.adapter.AlbumsAdapter;
import vn.giapvantai.musicplayer.helper.ListHelper;
import vn.giapvantai.musicplayer.listener.AlbumSelectListener;
import vn.giapvantai.musicplayer.model.Album;
import vn.giapvantai.musicplayer.viewmodel.MainViewModel;

public class AlbumsFragment extends Fragment implements AlbumSelectListener, SearchView.OnQueryTextListener {

    private final List<Album> albumList = new ArrayList<>();
    private AlbumsAdapter albumsAdapter;
    private List<Album> unchangedList = new ArrayList<>();
    private MainViewModel viewModel;

    private MaterialToolbar toolbar;
    private SearchView searchView;

    public AlbumsFragment() {
    }

    public static AlbumsFragment newInstance() {
        return new AlbumsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_albums, container, false);

        // Khởi tạo các thành phần giao diện
        toolbar = view.findViewById(R.id.search_toolbar);
        RecyclerView recyclerView = view.findViewById(R.id.albums_layout);

        // Cấu hình RecyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(requireActivity(), 2));
        albumsAdapter = new AlbumsAdapter(albumList, this);
        recyclerView.setAdapter(albumsAdapter);

        // Quan sát dữ liệu từ ViewModel
        viewModel.getAlbumList().observe(requireActivity(), this::setUpAlbumListView);
        viewModel.getSongsList().observe(requireActivity(), songs -> viewModel.parseAlbumList(songs));

        // Cấu hình các tùy chọn và thanh tìm kiếm
        setUpOptions();
        return view;
    }

    private void setUpAlbumListView(List<Album> albums) {
        unchangedList = albums;
        updateAdapter(unchangedList);
    }

    private void setUpOptions() {
        // Xử lý sự kiện khi nhấn vào các tùy chọn trên thanh toolbar
        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.search) {
                searchView = (SearchView) item.getActionView();
                setUpSearchView();
                return true;
            } else if (id == R.id.menu_sort_asc) {
                updateAdapter(ListHelper.sortAlbumByName(albumList, false));
                return true;
            } else if (id == R.id.menu_sort_dec) {
                updateAdapter(ListHelper.sortAlbumByName(albumList, true));
                return true;
            } else if (id == R.id.menu_most_songs) {
                updateAdapter(ListHelper.sortAlbumBySongs(albumList, false));
                return true;
            } else if (id == R.id.menu_least_songs) {
                updateAdapter(ListHelper.sortAlbumBySongs(albumList, true));
                return true;
            } else if (id == R.id.menu_longest_dur) {
                updateAdapter(ListHelper.sortAlbumByDuration(albumList, false));
                return true;
            } else if (id == R.id.menu_shortest_dur) {
                updateAdapter(ListHelper.sortAlbumByDuration(albumList, true));
                return true;
            }

            return false;
        });

        // Xử lý sự kiện khi nhấn vào nút back trên thanh toolbar
        toolbar.setNavigationOnClickListener(v -> {
            if (searchView == null || searchView.isIconified())
                requireActivity().finish();
        });
    }

    private void setUpSearchView() {
        // Xử lý sự kiện khi nhập dữ liệu vào thanh tìm kiếm
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // Xử lý sự kiện khi nhấn nút tìm kiếm trên bàn phím
        updateAdapter(ListHelper.searchByAlbumName(unchangedList, query.toLowerCase()));
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // Xử lý sự kiện khi thay đổi nội dung của thanh tìm kiếm
        updateAdapter(ListHelper.searchByAlbumName(unchangedList, newText.toLowerCase()));
        return true;
    }

    private void updateAdapter(List<Album> list) {
        // Cập nhật dữ liệu của adapter và thông báo thay đổi
        albumList.clear();
        albumList.addAll(list);
        albumsAdapter.notifyDataSetChanged();
    }

    @Override
    public void selectedAlbum(Album album) {
        // Xử lý sự kiện khi chọn một album
        requireActivity().startActivity(new Intent(
                getActivity(),
                SelectedAlbumActivity.class
        ).putExtra("album", album));
    }
}
