package vn.giapvantai.musicplayer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import vn.giapvantai.musicplayer.R;
import vn.giapvantai.musicplayer.listener.ArtistSelectListener;
import vn.giapvantai.musicplayer.model.Artist;

import java.util.List;
import java.util.Locale;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.MyViewHolder> {

    private final List<Artist> artistList;
    private final ArtistSelectListener selectListener;

    public ArtistAdapter(ArtistSelectListener selectListener, List<Artist> artistList) {
        this.artistList = artistList;
        this.selectListener = selectListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo view cho mỗi item trong RecyclerView từ layout XML
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_artists, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Thiết lập dữ liệu cho các thành phần giao diện của mỗi item
        holder.artistName.setText(artistList.get(position).name);
        holder.artistHistory.setText(String.format(Locale.getDefault(), "%d Albums • %d Songs",
                artistList.get(position).albumCount,
                artistList.get(position).songCount));
    }

    @Override
    public int getItemCount() {
        // Trả về số lượng nghệ sĩ trong danh sách
        return artistList.size();
    }

    // ViewHolder chứa các thành phần giao diện của mỗi item trong RecyclerView
    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView artistName;
        private final TextView artistHistory;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            // Ánh xạ các thành phần giao diện từ layout XML
            artistName = itemView.findViewById(R.id.artist_name);
            artistHistory = itemView.findViewById(R.id.artist_history);

            // Thiết lập sự kiện click cho item nghệ sĩ
            itemView.findViewById(R.id.root_layout).setOnClickListener(v ->
                    selectListener.selectedArtist(artistList.get(getAdapterPosition())));
        }
    }
}
