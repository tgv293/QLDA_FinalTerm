package vn.giapvantai.musicplayer.fragments;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

import vn.giapvantai.musicplayer.R;
import vn.giapvantai.musicplayer.activities.SelectedArtistActivity;
import vn.giapvantai.musicplayer.adapter.ArtistAdapter;
import vn.giapvantai.musicplayer.helper.ListHelper;
import vn.giapvantai.musicplayer.listener.ArtistSelectListener;
import vn.giapvantai.musicplayer.model.Artist;
import vn.giapvantai.musicplayer.viewmodel.MainViewModel;

public class ArtistsFragment extends Fragment implements SearchView.OnQueryTextListener, ArtistSelectListener {

    private final List<Artist> artistList = new ArrayList<>();
    private MainViewModel viewModel;
    private ArtistAdapter artistAdapter;
    private List<Artist> unchangedList = new ArrayList<>();

    private MaterialToolbar toolbar;
    private SearchView searchView;

    public ArtistsFragment() {
    }

    public static ArtistsFragment newInstance() {
        return new ArtistsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_artists, container, false);

        // Khởi tạo các thành phần giao diện
        toolbar = view.findViewById(R.id.search_toolbar);
        RecyclerView recyclerView = view.findViewById(R.id.artist_layout);

        // Cấu hình RecyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        artistAdapter = new ArtistAdapter(this, artistList);
        recyclerView.setAdapter(artistAdapter);

        // Quan sát dữ liệu từ ViewModel
        viewModel.getArtistList().observe(requireActivity(), artists -> {
            unchangedList = artists;
            updateAdapter(unchangedList);
        });
        viewModel.getAlbumList().observe(requireActivity(), albums ->
                viewModel.parseArtistList(albums));

        // Cấu hình các tùy chọn và thanh tìm kiếm
        setUpOptions();
        return view;
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
                updateAdapter(ListHelper.sortArtistByName(artistList, false));
                return true;
            } else if (id == R.id.menu_sort_dec) {
                updateAdapter(ListHelper.sortArtistByName(artistList, true));
                return true;
            } else if (id == R.id.menu_most_songs) {
                updateAdapter(ListHelper.sortArtistBySongs(artistList, false));
                return true;
            } else if (id == R.id.menu_least_songs) {
                updateAdapter(ListHelper.sortArtistBySongs(artistList, true));
                return true;
            } else if (id == R.id.menu_most_albums) {
                updateAdapter(ListHelper.sortArtistByAlbums(artistList, false));
                return true;
            } else if (id == R.id.menu_least_albums) {
                updateAdapter(ListHelper.sortArtistByAlbums(artistList, true));
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
        updateAdapter(ListHelper.searchArtistByName(unchangedList, query.toLowerCase()));
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // Xử lý sự kiện khi thay đổi nội dung của thanh tìm kiếm
        updateAdapter(ListHelper.searchArtistByName(unchangedList, newText.toLowerCase()));
        return true;
    }

    private void updateAdapter(List<Artist> list) {
        // Cập nhật dữ liệu của adapter và thông báo thay đổi
        artistList.clear();
        artistList.addAll(list);
        artistAdapter.notifyDataSetChanged();
    }

    @Override
    public void selectedArtist(Artist artist) {
        // Xử lý sự kiện khi chọn một nghệ sĩ
        requireActivity().startActivity(new Intent(
                getActivity(), SelectedArtistActivity.class
        ).putExtra("artist", artist));
    }
}
