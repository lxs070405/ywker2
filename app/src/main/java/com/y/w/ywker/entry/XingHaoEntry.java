package com.y.w.ywker.entry;

/**
 * Created by lxs on 2016/8/26.
 * 型号实体
 */
public class XingHaoEntry implements Comparable{

    /**
     * ID : 264
     * MainID : 1
     * TypeID : 10
     * TypeCode : 0004
     * ModelName : x20
     * ModelCode :
     * SortNo : 0
     * Remark :
     * Valid : 1
     * BrandID : 6
     * BrandCode :
     * ModelID :
     * mcount :
     * mid :
     * notBindCount :
     */

    private String ID;
    private String MainID;
    private String TypeID;
    private String TypeCode;
    private String ModelName;
    private String ModelCode;
    private String SortNo;
    private String Remark;
    private String Valid;
    private String BrandID;
    private String BrandCode;
    private String ModelID;
    private String mcount;
    private String mid;
    private String notBindCount;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getMainID() {
        return MainID;
    }

    public void setMainID(String MainID) {
        this.MainID = MainID;
    }

    public String getTypeID() {
        return TypeID;
    }

    public void setTypeID(String TypeID) {
        this.TypeID = TypeID;
    }

    public String getTypeCode() {
        return TypeCode;
    }

    public void setTypeCode(String TypeCode) {
        this.TypeCode = TypeCode;
    }

    public String getModelName() {
        return ModelName;
    }

    public void setModelName(String ModelName) {
        this.ModelName = ModelName;
    }

    public String getModelCode() {
        return ModelCode;
    }

    public void setModelCode(String ModelCode) {
        this.ModelCode = ModelCode;
    }

    public String getSortNo() {
        return SortNo;
    }

    public void setSortNo(String SortNo) {
        this.SortNo = SortNo;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String Remark) {
        this.Remark = Remark;
    }

    public String getValid() {
        return Valid;
    }

    public void setValid(String Valid) {
        this.Valid = Valid;
    }

    public String getBrandID() {
        return BrandID;
    }

    public void setBrandID(String BrandID) {
        this.BrandID = BrandID;
    }

    public String getBrandCode() {
        return BrandCode;
    }

    public void setBrandCode(String BrandCode) {
        this.BrandCode = BrandCode;
    }

    public String getModelID() {
        return ModelID;
    }

    public void setModelID(String ModelID) {
        this.ModelID = ModelID;
    }

    public String getMcount() {
        return mcount;
    }

    public void setMcount(String mcount) {
        this.mcount = mcount;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getNotBindCount() {
        return notBindCount;
    }

    public void setNotBindCount(String notBindCount) {
        this.notBindCount = notBindCount;
    }

    @Override
    public int compareTo(Object another) {

        if(!mcount.equals("")){
            int i = Integer.parseInt(mcount);
            return i;
        }
        return Integer.parseInt(ID);
    }
}
