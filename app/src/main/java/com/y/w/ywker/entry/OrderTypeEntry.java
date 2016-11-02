package com.y.w.ywker.entry;

/**
 * Created by lxs on 16/4/25.
 * 工单类型
 */
public class OrderTypeEntry {

    private String ID = "";
    private String MainID = "";
    private String TypeName = "";
    private String TypeCode = "";
    private String Remark = "";
    private String Valid = "";

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getMainID() {
        return MainID;
    }

    public void setMainID(String mainID) {
        MainID = mainID;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public String getTypeCode() {
        return TypeCode;
    }

    public void setTypeCode(String typeCode) {
        TypeCode = typeCode;
    }

    public String getTypeName() {
        return TypeName;
    }

    public void setTypeName(String typeName) {
        TypeName = typeName;
    }

    public String getValid() {
        return Valid;
    }

    public void setValid(String valid) {
        Valid = valid;
    }
}
