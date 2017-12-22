package me.shouheng.notepal.provider.schema;

/**
 * Created by WngShhng on 2017/12/10.*/
public interface NoteSchema extends BaseSchema {
    String TABLE_NAME = "gt_note";
    String PARENT_CODE = "parent_code";
    String TITLE = "title";
    String CONTENT_CODE = "content_code";
    String TAGS = "tags";
    String CLASS_CODE = "class_code";
    String PURPOSE_CODE = "purpose_code";
    String TREE_PATH = "tree_path";
}
