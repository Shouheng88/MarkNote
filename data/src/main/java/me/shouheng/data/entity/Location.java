package me.shouheng.data.entity;

import me.shouheng.data.utils.annotation.Column;
import me.shouheng.data.utils.annotation.Table;
import me.shouheng.data.model.enums.ModelType;
import me.shouheng.data.schema.LocationSchema;

/**
 * Created by wangshouheng on 2017/4/6.*/
@Table(name = LocationSchema.TABLE_NAME)
public class Location extends Model {

    @Column(name = LocationSchema.LONGITUDE)
    private double longitude;

    @Column(name = LocationSchema.LATITUDE)
    private double latitude;

    @Column(name = LocationSchema.COUNTRY)
    private String country;

    @Column(name = LocationSchema.PROVINCE)
    private String province;

    @Column(name = LocationSchema.CITY)
    private String city;

    @Column(name = LocationSchema.DISTRICT)
    private String district;

    @Column(name = LocationSchema.MODEL_CODE)
    private long modelCode;

    @Column(name = LocationSchema.MODEL_TYPE)
    private ModelType modelType;

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public long getModelCode() {
        return modelCode;
    }

    public void setModelCode(long modelCode) {
        this.modelCode = modelCode;
    }

    public ModelType getModelType() {
        return modelType;
    }

    public void setModelType(ModelType modelType) {
        this.modelType = modelType;
    }

    @Override
    public String toString() {
        return "Location{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                ", country='" + country + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", modelCode=" + modelCode +
                ", modelType=" + (modelType == null ? null : modelType.name()) +
                "} " + super.toString();
    }
}
