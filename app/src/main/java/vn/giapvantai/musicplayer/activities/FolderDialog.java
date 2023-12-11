package vn.giapvantai.musicplayer.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

import vn.giapvantai.musicplayer.App;
import vn.giapvantai.musicplayer.MPPreferences;
import vn.giapvantai.musicplayer.R;
import vn.giapvantai.musicplayer.model.Folder;

public class FolderDialog extends BottomSheetDialog {

    private final List<String> exclusionFolders;
    private RecyclerView recyclerView; // Move recyclerView to class-level

    public FolderDialog(@NonNull Context context, List<Folder> folderList) {
        super(context);
        setContentView(R.layout.dialog_folder);

        exclusionFolders = MPPreferences.getExcludedFolders(context);

        recyclerView = findViewById(R.id.folder_layout);
        // Thiết lập RecyclerView
        setupRecyclerView(folderList);
    }

    private void setupRecyclerView(List<Folder> folderList) {
        FolderAdapter adapter = new FolderAdapter(folderList, exclusionFolders, new FolderAdapter.FolderExclusionListListener() {
            @Override
            public void add(String name) {
                // Thêm vào danh sách các thư mục được loại trừ
                addToExcluded(name);
            }

            @Override
            public void remove(String name) {
                // Loại bỏ khỏi danh sách các thư mục được loại trừ
                removeFromExcluded(name);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void addToExcluded(String name) {
        // Kiểm tra xem tên thư mục đã có trong danh sách loại trừ chưa
        if (!exclusionFolders.contains(name)) {
            // Nếu chưa có, thêm vào danh sách loại trừ
            exclusionFolders.add(name);
        }
        // Lưu danh sách loại trừ vào SharedPreferences
        MPPreferences.storeExcludedFolders(App.getContext(), exclusionFolders);
    }

    private void removeFromExcluded(String name) {
        // Loại bỏ thư mục khỏi danh sách loại trừ
        exclusionFolders.remove(name);
        // Lưu danh sách loại trừ vào SharedPreferences
        MPPreferences.storeExcludedFolders(App.getContext(), exclusionFolders);
    }

    static class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.MyViewHolder> {

        private final List<Folder> folderList;
        private final List<String> exclusionList;
        private final FolderExclusionListListener listener;

        public FolderAdapter(List<Folder> folderList, List<String> exclusionList, FolderExclusionListListener listener) {
            this.folderList = folderList;
            this.exclusionList = exclusionList;
            this.listener = listener;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Tạo ViewHolder từ layout XML
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            // Gán dữ liệu thư mục vào ViewHolder
            bindFolderItem(holder, position);
        }

        private void bindFolderItem(MyViewHolder holder, int position) {
            // Kiểm tra xem thư mục có nằm trong danh sách loại trừ hay không
            if (exclusionList.contains(folderList.get(position).name)) {
                // Nếu có, đặt trạng thái của CheckBox là đánh dấu (đã chọn)
                holder.folderSelect.setChecked(true);
            }

            // Đặt tên thư mục cho CheckBox
            holder.folderSelect.setText(folderList.get(position).name);
        }

        @Override
        public int getItemCount() {
            // Trả về số lượng thư mục trong danh sách
            return folderList.size();
        }

        public interface FolderExclusionListListener {
            void add(String name);

            void remove(String name);
        }

        protected class MyViewHolder extends RecyclerView.ViewHolder {

            private final CheckBox folderSelect;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                // Ánh xạ các thành phần từ layout XML
                folderSelect = itemView.findViewById(R.id.control_select_folder);

                // Thiết lập lắng nghe sự kiện cho CheckBox
                setupCheckBoxListener();
            }

            private void setupCheckBoxListener() {
                // Xử lý sự kiện khi trạng thái của CheckBox thay đổi
                folderSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    // Xử lý sự kiện thay đổi trạng thái của CheckBox
                    handleCheckBoxChange(isChecked);
                });
            }

            private void handleCheckBoxChange(boolean isChecked) {
                // Nếu CheckBox được đánh dấu, thêm thư mục vào danh sách loại trừ; ngược lại, loại bỏ nó
                if (isChecked) {
                    listener.add(folderList.get(getAdapterPosition()).name);
                } else {
                    listener.remove(folderList.get(getAdapterPosition()).name);
                }
            }
        }
    }
}
