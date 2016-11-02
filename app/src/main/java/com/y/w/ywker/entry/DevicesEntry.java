package com.y.w.ywker.entry;

/**
 * Created by lxs on 16/5/4.
 */
public class DevicesEntry {
    /**
     * ID : 3996
     * ClientID : 321  客户主键
     * DepartID : 317
     * TypeID : 13
     * BrandID : 26
     * ModelID : 94
     * TypeName: 设备类型
     * Remark  描述
     * AssetName : 四通5860针式打印机
     * AssetXuLie : null
     * ModelName : 5860  型号
     * BrandName : 四通  品牌
     * ClientName : 中国邮政储蓄银行郑州分行须水镇支行
     * ContactName : 刘会娟
     * DepartName : 须水镇支行
     * ContactID : 315 联系人主键
     * IsHavePic 是否有图片信息 大于0 标识有图片 等于0 没有
     */

    private int ID;
    private int ClientID;
    private int DepartID;
    private int TypeID;
    private int BrandID;
    private int ModelID;
    private int IsHavePic;
    private String AssetName;
    private String TypeName;
    private String AssetXuLie;
    private String ModelName;
    private String BrandName;
    private String ClientName;
    private String ContactName;
    private String Remark;
    private String DepartName;
    private int ContactID;
    public void setIsHavePic(int isHavePic){
        this.IsHavePic = isHavePic;
    }
    public int getIsHavePic(){
        return IsHavePic;
    }
    public void setRemark(String remark){
        this.Remark = remark;
    }
    public String getRemark(){
        return Remark;
    }
    public void setTypeName(String typeName){
        this.TypeName = TypeName;
    }
    public String getTypeName(){
        return TypeName;
    }
    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getClientID() {
        return ClientID;
    }

    public void setClientID(int ClientID) {
        this.ClientID = ClientID;
    }

    public int getDepartID() {
        return DepartID;
    }

    public void setDepartID(int DepartID) {
        this.DepartID = DepartID;
    }

    public int getTypeID() {
        return TypeID;
    }

    public void setTypeID(int TypeID) {
        this.TypeID = TypeID;
    }

    public int getBrandID() {
        return BrandID;
    }

    public void setBrandID(int BrandID) {
        this.BrandID = BrandID;
    }

    public int getModelID() {
        return ModelID;
    }

    public void setModelID(int ModelID) {
        this.ModelID = ModelID;
    }

    public String getAssetName() {
        return AssetName;
    }

    public void setAssetName(String AssetName) {
        this.AssetName = AssetName;
    }

    public String getAssetXuLie() {
        return AssetXuLie;
    }

    public void setAssetXuLie(String AssetXuLie) {
        this.AssetXuLie = AssetXuLie;
    }

    public String getModelName() {
        return ModelName;
    }

    public void setModelName(String ModelName) {
        this.ModelName = ModelName;
    }

    public String getBrandName() {
        return BrandName;
    }

    public void setBrandName(String BrandName) {
        this.BrandName = BrandName;
    }

    public String getClientName() {
        return ClientName;
    }

    public void setClientName(String ClientName) {
        this.ClientName = ClientName;
    }

    public String getContactName() {
        return ContactName;
    }

    public void setContactName(String ContactName) {
        this.ContactName = ContactName;
    }

    public String getDepartName() {
        return DepartName;
    }

    public void setDepartName(String DepartName) {
        this.DepartName = DepartName;
    }

    public int getContactID() {
        return ContactID;
    }

    public void setContactID(int ContactID) {
        this.ContactID = ContactID;
    }
}
