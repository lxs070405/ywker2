package com.y.w.ywker.entry;

/**
 * Created by lxs on 2016/6/30.
 * 设备码模糊搜索bean
 */
public class AseetCodeSearchEntry {

    /**
     * ID : 10003  二维码主键
     * MainID : 1  供应商主键
     * QBCode : HF00003314  二维码编号
     * QBDetail : null  二维码存放信息
     * QBPath : null  二维码路径
     * AssetID : 1935  设备主键（已绑定为数字，未绑定为0）
     * PrintNum : 0  打印次数
     * Valid : 1  有效性
     */

    private int ID;
    private int MainID;
    private String QBCode;
    private Object QBDetail;
    private Object QBPath;
    private int AssetID;
    private int PrintNum;
    private int Valid;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getMainID() {
        return MainID;
    }

    public void setMainID(int MainID) {
        this.MainID = MainID;
    }

    public String getQBCode() {
        return QBCode;
    }

    public void setQBCode(String QBCode) {
        this.QBCode = QBCode;
    }

    public Object getQBDetail() {
        return QBDetail;
    }

    public void setQBDetail(Object QBDetail) {
        this.QBDetail = QBDetail;
    }

    public Object getQBPath() {
        return QBPath;
    }

    public void setQBPath(Object QBPath) {
        this.QBPath = QBPath;
    }

    public int getAssetID() {
        return AssetID;
    }

    public void setAssetID(int AssetID) {
        this.AssetID = AssetID;
    }

    public int getPrintNum() {
        return PrintNum;
    }

    public void setPrintNum(int PrintNum) {
        this.PrintNum = PrintNum;
    }

    public int getValid() {
        return Valid;
    }

    public void setValid(int Valid) {
        this.Valid = Valid;
    }
}
