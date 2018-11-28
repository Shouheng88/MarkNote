package me.shouheng.data.entity;

import me.shouheng.data.utils.Selectable;
import me.shouheng.data.utils.annotation.Column;
import me.shouheng.data.utils.annotation.Table;
import me.shouheng.data.model.enums.Portrait;
import me.shouheng.data.schema.CategorySchema;

/**
 * Created by wangshouheng on 2017/3/31.*/
@Table(name = CategorySchema.TABLE_NAME)
public class Category extends Model implements Selectable {

    @Column(name = CategorySchema.NAME)
    private String name;

    @Column(name = CategorySchema.COLOR)
    private int color;

    @Column(name = CategorySchema.PORTRAIT)
    private Portrait portrait;

    @Column(name = CategorySchema.CATEGORY_ORDER)
    private int categoryOrder;

    // region Android端字段，不计入数据库

    private boolean contentChanged;

    private int count;

    private boolean isSelected;

    public boolean isContentChanged() {
        return contentChanged;
    }

    public void setContentChanged(boolean contentChanged) {
        this.contentChanged = contentChanged;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    // endregion

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Portrait getPortrait() {
        return portrait;
    }

    public void setPortrait(Portrait portrait) {
        this.portrait = portrait;
    }

    public int getCategoryOrder() {
        return categoryOrder;
    }

    public void setCategoryOrder(int categoryOrder) {
        this.categoryOrder = categoryOrder;
    }

    @Override
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public String toString() {
        return "CategoryVo{" +
                "name='" + name + '\'' +
                ", color=#" + Integer.toHexString(color) +
                ", portrait=" + portrait +
                ", categoryOrder=" + categoryOrder +
                ", count=" + count +
                "} " + super.toString();
    }
}
