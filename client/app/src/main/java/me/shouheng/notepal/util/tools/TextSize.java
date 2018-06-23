package me.shouheng.notepal.util.tools;

/**
 * Created by wangshouheng on 2017/8/26. */
public enum TextSize {
    SMALL(0, 17f, 15f, 15f),
    SMALLER(1, 17.5f, 15.5f, 15.5f),
    MEDIUM(2, 18f, 16f, 16f),
    BIGGER(3, 18.5f, 16.5f, 16.5f),
    BIG(4, 19f, 17f, 17f);

    public final int id;
    public final float titleTextSize;
    public final float contentTextSize;
    public final float commentTextSize;

    TextSize(int id, float titleTextSize, float contentTextSize, float commentTextSize) {
        this.id = id;
        this.titleTextSize = titleTextSize;
        this.contentTextSize = contentTextSize;
        this.commentTextSize = commentTextSize;
    }

    public static TextSize getTextSizeById(int id) {
        for (TextSize textSize : values()){
            if (textSize.id == id){
                return textSize;
            }
        }
        return MEDIUM;
    }
}
