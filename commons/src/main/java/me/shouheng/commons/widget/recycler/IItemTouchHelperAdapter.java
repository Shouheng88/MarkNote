package me.shouheng.commons.widget.recycler;

/**
 * Created by wangshouheng on 2017/3/31.*/
public interface IItemTouchHelperAdapter {

    /**
     * View type of current position, here are three options:
     * 1.Header of list;
     * 2.Footer of list;
     * 3.Normal list item.*/
    enum ViewType {
        NORMAL(0),
        HEADER(1),
        FOOTER(2);

        public final int mId;

        ViewType(int mId) {
            this.mId = mId;
        }
    }

    void onItemMoved(int fromPosition, int toPosition);

    void onItemRemoved(int position);

    void afterMoved();
}
