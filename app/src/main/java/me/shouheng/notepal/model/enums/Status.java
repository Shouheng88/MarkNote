package me.shouheng.notepal.model.enums;

/**
 * Created by WngShhng on 2017/12/9.*/
public enum Status {
    NORMAL(0),
    ARCHIVED(1),
    TRASHED(2),
    DELETED(3),

    /**
     * Additional condition. If we finally deleted one item, but it is still usable in another condition.
     * For example, if we deleted the category from trash, that means the category is invisible in
     * categories trash list. But if we directly delete the item, that would affect the category in
     * archived list. So in this circumstance, we set it invisible. That means it isn`t really deleted.
     * It is just invisible in current list. But all the items below it would be deleted, so in our
     * logistics of query strategy, this category will not show up in the list. (We only queried the
     * trashed items and the item with trashed sub-items below.)
     */
    INVISIBLE(4);

    public final int id;

    Status(int id) {
        this.id = id;
    }

    public static Status getStatusById(int id) {
        for (Status status : values()) {
            if (status.id == id) {
                return status;
            }
        }
        return NORMAL;
    }
}
