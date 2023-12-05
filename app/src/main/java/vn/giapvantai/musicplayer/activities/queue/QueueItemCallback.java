package vn.giapvantai.musicplayer.activities.queue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import vn.giapvantai.musicplayer.adapter.QueueAdapter;

public class QueueItemCallback extends ItemTouchHelper.Callback {

    private final QueueItemInterface queueItemInterface;

    public QueueItemCallback(QueueItemInterface queueItemInterface) {
        this.queueItemInterface = queueItemInterface;
    }

    // Cho phép giữ lâu để di chuyển
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    // Cho phép vuốt để xóa
    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    // Xác định các hành động di chuyển hỗ trợ (chỉ hỗ trợ lên và xuống)
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags, 0);
    }

    // Xử lý sự kiện di chuyển item
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        // Thông báo cho interface về việc di chuyển hàng
        queueItemInterface.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    // Xử lý sự kiện vuốt item (chưa được xử lý trong mã nguồn hiện tại)
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        // Có thể xử lý sự kiện vuốt nếu cần thiết
    }

    // Thông báo khi một item được chọn
    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder instanceof QueueAdapter.MyViewHolder) {
            QueueAdapter.MyViewHolder myViewHolder = (QueueAdapter.MyViewHolder) viewHolder;
            // Thông báo cho interface về việc chọn hàng
            queueItemInterface.onRowSelected(myViewHolder);
        }

        super.onSelectedChanged(viewHolder, actionState);
    }

    // Thông báo khi item được thả ra (kết thúc sự kiện di chuyển)
    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if (viewHolder instanceof QueueAdapter.MyViewHolder) {
            QueueAdapter.MyViewHolder myViewHolder = (QueueAdapter.MyViewHolder) viewHolder;
            // Thông báo cho interface về việc dỡ chọn hàng
            queueItemInterface.onRowClear(myViewHolder);
        }
    }

    // Giao diện cho các sự kiện liên quan đến các hàng trong danh sách phát
    public interface QueueItemInterface {
        void onRowMoved(int fromPosition, int toPosition);

        void onRowSelected(QueueAdapter.MyViewHolder myViewHolder);

        void onRowClear(QueueAdapter.MyViewHolder myViewHolder);
    }
}
