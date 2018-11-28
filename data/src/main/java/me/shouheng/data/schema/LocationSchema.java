package me.shouheng.data.schema;

/**
 * Created by WngShhng on 2017/12/10.*/
public interface LocationSchema extends BaseSchema {
    String TABLE_NAME = "gt_location";
    String LONGITUDE = "longitude";
    String LATITUDE = "latitude";
    String COUNTRY = "country";
    String PROVINCE = "province";
    String CITY = "city";
    String DISTRICT = "district";
    String MODEL_CODE = "model_code";
    String MODEL_TYPE = "model_type";
}
