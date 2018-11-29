package me.shouheng.commons.widget.recycler;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import me.shouheng.commons.widget.recycler.IItemTouchHelperAdapter;

import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_IDLE;

/**
 * 当用户进行move的时候，每将ViewHolder移动一个位置就会调用一次
 * {@link #onMove(RecyclerView, RecyclerView.ViewHolder, RecyclerView.ViewHolder)}
 * 方法，我们在其中设置{@link #moved}为true来判断发生了移动事件，然后当手从{@link RecyclerView}
 * 抬起的时候，会在{@link #onSelectedChanged(RecyclerView.ViewHolder, int)}中进行判断，
 * 并通过回调方法调用{@link RecyclerView.Adapter#notifyDataSetChanged()}方法来通知对界面进行更新 */
public class CustomItemTouchHelper extends ItemTouchHelper.Callback {

    private IItemTouchHelperAdapter adapter;

    private boolean isLongPressDragEnabled;

    private boolean isItemViewSwipeEnabled;

    private boolean moved = false;

    public CustomItemTouchHelper(boolean isLongPressDragEnabled, boolean isItemViewSwipeEnabled, IItemTouchHelperAdapter adapter) {
        this.isLongPressDragEnabled = isLongPressDragEnabled;
        this.isItemViewSwipeEnabled = isItemViewSwipeEnabled;
        this.adapter = adapter;
    }

    /**
     * 这里我们通过该事件来监听{@link RecyclerView}的触摸抬起的动作，该事件会在{@link ItemTouchHelper}
     * 中的mOnItemTouchListener中的MotionEvent.ACTION_UP时触发，因为它触发的位置可能有多个位置，
     * 而且ACTION_STATE_IDLE也会在多个触发的时候传递进来，所以单纯地通过ACTION_STATE_IDLE的值就认为
     * 是Move动作之后的抬起动作是不可靠的，所以，我们在这里使用了一个boolean类型的{@link #moved}来辅助
     * 判断移动动作
     *
     * @param viewHolder
     * @param actionState */
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if (actionState == ACTION_STATE_IDLE && moved) {
            moved = false;
            adapter.afterMoved();
        }
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return isLongPressDragEnabled;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return isItemViewSwipeEnabled;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (viewHolder.getItemViewType() == IItemTouchHelperAdapter.ViewType.HEADER.mId
                || viewHolder.getItemViewType() == IItemTouchHelperAdapter.ViewType.FOOTER.mId){
            return makeMovementFlags(0,0);
        }
        int upFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(upFlags, swipeFlags);
    }

    /**
     * 当控件被移动到了一个新的位置的时候就触发该方法，所以如果一次拖动（不松开）跨越多个控件的时候
     * 没跨过一个控件就调用一次该方法 */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        if (target.getItemViewType() == IItemTouchHelperAdapter.ViewType.HEADER.mId
                || target.getItemViewType() == IItemTouchHelperAdapter.ViewType.FOOTER.mId){
            return true;
        }
        moved = true;
        adapter.onItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        adapter.onItemRemoved(viewHolder.getAdapterPosition());
    }
}
