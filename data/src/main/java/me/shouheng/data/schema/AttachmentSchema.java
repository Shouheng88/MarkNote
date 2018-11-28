package me.shouheng.data.schema;

/**
 * Created by WngShhng on 2017/12/10.*/
public interface AttachmentSchema extends BaseSchema {
    String TABLE_NAME = "gt_attachment";
    String MODEL_CODE = "model_code";
    String MODEL_TYPE = "model_type";
    String URI = "uri";
    String PATH = "path";
    String NAME = "name";
    String SIZE = "size";
    String LENGTH = "length";
    String MINE_TYPE = "mine_type";
    String ONE_DRIVE_SYNC_TIME = "one_drive_sync_time";
    String ONE_DRIVE_ITEM_ID = "one_drive_item_id";
}
