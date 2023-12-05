package vn.giapvantai.musicplayer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import vn.giapvantai.musicplayer.MPPreferences;
import vn.giapvantai.musicplayer.R;
import vn.giapvantai.musicplayer.listener.AlbumSelectListener;
import vn.giapvantai.musicplayer.model.Album;
import com.bumptech.glide.Glide;

import java.util.List;

public class HorizontalAlbumsAdapter extends RecyclerView.Adapter<HorizontalAlbumsAdapter.MyViewHolder> {

    private final List<Album> albumList;
    private final AlbumSelectListener listener;

    public HorizontalAlbumsAdapter(List<Album> albums, AlbumSelectListener listener) {
        this.albumList = albums;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo view cho mỗi item trong RecyclerView từ layout XML
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_item_albums, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Thiết lập dữ liệu cho các thành phần giao diện của mỗi item
        holder.albumTitle.setText(albumList.get(position).title);

        // Nếu có cho phép hiển thị ảnh album
        if (holder.isAlbumArtEnabled()) {
            // Load ảnh album bằng thư viện Glide
            Glide.with(holder.albumArt.getContext())
                    .load(albumList.get(position).music.get(0).albumArt)
                    .placeholder(R.drawable.ic_album_art)
                    .into(holder.albumArt);
        }
    }

    @Override
    public int getItemCount() {
        // Trả về số lượng album trong danh sách
        return albumList.size();
    }

    // ViewHolder chứa các thành phần giao diện của mỗi item trong RecyclerView
    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final ImageView albumArt;
        private final TextView albumTitle;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            // Ánh xạ các thành phần giao diện từ layout XML
            albumArt = itemView.findViewById(R.id.album_art);
            albumTitle = itemView.findViewById(R.id.album_title);

            // Thiết lập sự kiện click cho item album
            itemView.findViewById(R.id.album_art_layout).setOnClickListener(v ->
                    listener.selectedAlbum(albumList.get(getAdapterPosition())));
        }

        // Kiểm tra xem có cho phép hiển thị ảnh album hay không
        public boolean isAlbumArtEnabled() {
            return MPPreferences.getAlbumRequest(itemView.getContext());
        }
    }
}
