package me.shouheng.notepal.model;


import me.shouheng.notepal.model.enums.ModelType;
import me.shouheng.notepal.provider.annotation.Column;
import me.shouheng.notepal.provider.annotation.Table;

/**
 * Created by wangshouheng on 2017/4/6.*/
@Table(name = "gt_location")
public class Location extends Model {

    @Column(name = "longitude")
    private double longitude;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "country")
    private String country;

    @Column(name = "province")
    private String province;

    @Column(name = "city")
    private String city;

    @Column(name = "district")
    private String district;

    @Column(name = "model_code")
    private long modelCode;

    @Column(name = "model_type")
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
