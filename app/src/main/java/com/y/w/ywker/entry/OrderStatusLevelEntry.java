package com.y.w.ywker.entry;

/**
 * Created by lxs on 16/4/25.
 * 工单的状态和优先级实体类
 */
public class OrderStatusLevelEntry {
    private String ID = "";
    private String DicCode = "";
    private String DicName = "";
    private String DicTypeCode = "";

    public String getDicTypeName() {
        return DicTypeName;
    }

    public void setDicTypeName(String dicTypeName) {
        DicTypeName = dicTypeName;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getIsValid() {
        return IsValid;
    }

    public void setIsValid(String isValid) {
        IsValid = isValid;
    }

    public String getDicName() {
        return DicName;
    }

    public void setDicName(String dicName) {
        DicName = dicName;
    }

    public String getDicTypeCode() {
        return DicTypeCode;
    }

    public void setDicTypeCode(String dicTypeCode) {
        DicTypeCode = dicTypeCode;
    }

    private String DicTypeName = "";
    private String IsValid = "";

    public String getDicCode() {
        return DicCode;
    }

    public void setDicCode(String dicCode) {
        DicCode = dicCode;
    }
}
