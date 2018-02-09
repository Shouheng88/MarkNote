package me.shouheng.notepal.widget.desktop;

/**
 * Created by wang shouheng on 2018/1/25.*/
public enum ListWidgetType {
    NOTES_LIST(0),
    MINDS_LIST(1);

    public final int id;

    ListWidgetType(int id) {
        this.id = id;
    }

    public static ListWidgetType getListWidgetType(int id){
        for (ListWidgetType type : values()){
            if (type.id == id){
                return type;
            }
        }
        return NOTES_LIST;
    }

    @Override
    public String toString() {
        return "ListWidgetType." + name();
    }
}
