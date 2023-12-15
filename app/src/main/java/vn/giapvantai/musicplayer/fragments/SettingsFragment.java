package vn.giapvantai.musicplayer.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;

import vn.giapvantai.musicplayer.MPConstants;
import vn.giapvantai.musicplayer.MPPreferences;
import vn.giapvantai.musicplayer.R;
import vn.giapvantai.musicplayer.activities.FolderDialog;
import vn.giapvantai.musicplayer.adapter.AccentAdapter;
import vn.giapvantai.musicplayer.helper.ThemeHelper;
import vn.giapvantai.musicplayer.model.Folder;
import vn.giapvantai.musicplayer.viewmodel.MainViewModel;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    private MainViewModel viewModel;
    private RecyclerView accentView;
    private boolean state;
    private boolean autoPlayState;
    private LinearLayout chipLayout;
    private ImageView currentThemeMode;

    private List<Folder> folderList;
    private MaterialToolbar toolbar;
    private FolderDialog folderDialog;

    public SettingsFragment() {
        // Không sử dụng
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Gắn layout cho fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        viewModel.getFolderList().observe(requireActivity(), folders -> {
            if (folderList == null)
                folderList = new ArrayList<>();
            folderList.clear();
            folderList.addAll(folders);
        });

        SwitchMaterial switchMaterial = view.findViewById(R.id.album_switch);
        SwitchMaterial autoPlaySwitch = view.findViewById(R.id.auto_play_switch);
        accentView = view.findViewById(R.id.accent_view);
        chipLayout = view.findViewById(R.id.chip_layout);
        currentThemeMode = view.findViewById(R.id.current_theme_mode);
        toolbar = view.findViewById(R.id.toolbar);

        LinearLayout accentOption = view.findViewById(R.id.accent_option);
        LinearLayout albumOption = view.findViewById(R.id.album_options);
        LinearLayout themeModeOption = view.findViewById(R.id.theme_mode_option);
        LinearLayout folderOption = view.findViewById(R.id.folder_options);
        LinearLayout refreshOption = view.findViewById(R.id.refresh_options);

        state = MPPreferences.getAlbumRequest(requireActivity().getApplicationContext());
        autoPlayState = MPPreferences.getAutoPlay(requireActivity().getApplicationContext());
        switchMaterial.setChecked(state);
        autoPlaySwitch.setChecked(autoPlayState);
        setCurrentThemeMode();

        accentView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        accentView.setAdapter(new AccentAdapter(getActivity()));

        accentOption.setOnClickListener(this);
        albumOption.setOnClickListener(this);
        switchMaterial.setOnClickListener(this);
        autoPlaySwitch.setOnClickListener(this);
        themeModeOption.setOnClickListener(this);
        folderOption.setOnClickListener(this);
        refreshOption.setOnClickListener(this);

        view.findViewById(R.id.night_chip).setOnClickListener(this);
        view.findViewById(R.id.light_chip).setOnClickListener(this);
        view.findViewById(R.id.auto_chip).setOnClickListener(this);
        view.findViewById(R.id.review_options).setOnClickListener(this);

        setUpOptions();

        return view;
    }

    private void setUpOptions() {
        // Xử lý sự kiện khi chọn menu trên thanh toolbar
        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.github) {
                startActivity(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(MPConstants.GITHUB_REPO_URL)
                ));
                return true;
            }

            return false;
        });
        // Xử lý sự kiện khi nhấn nút back trên thanh toolbar
        toolbar.setNavigationOnClickListener(v -> requireActivity().finish());
    }

    private void setCurrentThemeMode() {
        // Thiết lập hình ảnh hiển thị chế độ theme hiện tại
        int mode = MPPreferences.getThemeMode(requireActivity().getApplicationContext());

        if (mode == AppCompatDelegate.MODE_NIGHT_NO)
            currentThemeMode.setImageResource(R.drawable.ic_theme_mode_light);
        else if (mode == AppCompatDelegate.MODE_NIGHT_YES)
            currentThemeMode.setImageResource(R.drawable.ic_theme_mode_night);
        else
            currentThemeMode.setImageResource(R.drawable.ic_theme_mode_auto);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        // Xử lý sự kiện khi click vào các tùy chọn
        if (id == R.id.accent_option) {
            int visibility = (accentView.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE;
            accentView.setVisibility(visibility);
        } else if (id == R.id.album_options)
            setAlbumRequest();
        else if (id == R.id.album_switch)
            setAlbumRequest();
        else if (id == R.id.auto_play_switch)
            setAutoPlay();
        else if (id == R.id.theme_mode_option) {
            int mode = chipLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
            chipLayout.setVisibility(mode);
        } else if (id == R.id.night_chip)
            selectTheme(AppCompatDelegate.MODE_NIGHT_YES);
        else if (id == R.id.light_chip)
            selectTheme(AppCompatDelegate.MODE_NIGHT_NO);
        else if (id == R.id.auto_chip)
            selectTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        else if (id == R.id.folder_options)
            showFolderSelectionDialog();
        else if (id == R.id.refresh_options) {
            refreshMediaLibrary();
        } else if (id == R.id.review_options) {
            setUpRateReview();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (folderDialog != null)
            folderDialog.dismiss();
    }

    private void showFolderSelectionDialog() {
        // Hiển thị dialog chọn thư mục
        if (folderList != null) {
            folderDialog = new FolderDialog(requireActivity(), folderList);
            folderDialog.show();

            folderDialog.setOnDismissListener(dialog -> refreshMediaLibrary());
        } else {
            Toast.makeText(requireActivity(), "Thiếu danh sách thư mục", Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshMediaLibrary() {
        // Cập nhật thư viện media
        Toast.makeText(requireActivity(), "Đang cập nhật thư viện media", Toast.LENGTH_SHORT).show();
        MPConstants.musicSelectListener.refreshMediaLibrary();
    }

    private void selectTheme(int theme) {
        // Chọn chế độ theme và lưu vào SharedPreferences
        AppCompatDelegate.setDefaultNightMode(theme);
        MPPreferences.storeThemeMode(requireActivity().getApplicationContext(), theme);
    }

    private void setAlbumRequest() {
        // Đặt yêu cầu hiển thị album và áp dụng cài đặt
        MPPreferences.storeAlbumRequest(requireActivity().getApplicationContext(), (!state));
        ThemeHelper.applySettings(getActivity());
    }

    private void setAutoPlay() {
        // Đặt tự động phát và lưu vào SharedPreferences
        MPPreferences.storeAutoPlay(requireActivity().getApplicationContext(), (!autoPlayState));
    }

    private void setUpRateReview() {
        // Chuyển đến trang đánh giá ứng dụng trên Play Store
        startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(MPConstants.PLAY_STORE_LINK)));
    }
}
