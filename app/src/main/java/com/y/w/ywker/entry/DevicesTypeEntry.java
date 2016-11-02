package com.y.w.ywker.entry;

import java.util.List;

/**
 * Created by lxs on 2016/8/26.
 * 设备类型实体
 */
public class DevicesTypeEntry {

    /**
     * ID : 11
     * MainID : 1
     * FCode : null
     * TypeCode : 0005
     * TypeName : 移动刷卡机11
     * SortNo : 0
     * Remark : null
     * Valid : 1
     * ChildNode : [{"ID":60,"MainID":1,"FCode":"0005","TypeCode":"00050001","TypeName":"dddddaaaa","SortNo":0,"Remark":null,"Valid":1},{"ID":61,"MainID":1,"FCode":"0005","TypeCode":"00050002","TypeName":"ddd","SortNo":0,"Remark":null,"Valid":1},{"ID":62,"MainID":1,"FCode":"0005","TypeCode":"00050003","TypeName":"fff","SortNo":0,"Remark":null,"Valid":1},{"ID":63,"MainID":1,"FCode":"0005","TypeCode":"00050004","TypeName":"25","SortNo":0,"Remark":null,"Valid":1},{"ID":64,"MainID":1,"FCode":"0005","TypeCode":"00050005","TypeName":"ss","SortNo":0,"Remark":null,"Valid":1},{"ID":65,"MainID":1,"FCode":"0005","TypeCode":"00050006","TypeName":"sd","SortNo":0,"Remark":null,"Valid":1},{"ID":66,"MainID":1,"FCode":"0005","TypeCode":"00050007","TypeName":"as","SortNo":0,"Remark":null,"Valid":1},{"ID":67,"MainID":1,"FCode":"0005","TypeCode":"00050008","TypeName":"asd","SortNo":0,"Remark":null,"Valid":1},{"ID":68,"MainID":1,"FCode":"0005","TypeCode":"00050009","TypeName":"as","SortNo":0,"Remark":null,"Valid":1},{"ID":69,"MainID":1,"FCode":"0005","TypeCode":"00050010","TypeName":"as","SortNo":0,"Remark":null,"Valid":1},{"ID":70,"MainID":1,"FCode":"0005","TypeCode":"00050011","TypeName":"jkkjk","SortNo":0,"Remark":null,"Valid":1},{"ID":71,"MainID":1,"FCode":"0005","TypeCode":"00050012","TypeName":"dddd","SortNo":0,"Remark":null,"Valid":1}]
     */

    private int ID;
    private int MainID;
    private Object FCode;
    private String TypeCode;
    private String TypeName;
    private int SortNo;
    private Object Remark;
    private int Valid;
    /**
     * ID : 60
     * MainID : 1
     * FCode : 0005
     * TypeCode : 00050001
     * TypeName : dddddaaaa
     * SortNo : 0
     * Remark : null
     * Valid : 1
     */

    private List<ChildNodeBean> ChildNode;

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

    public Object getFCode() {
        return FCode;
    }

    public void setFCode(Object FCode) {
        this.FCode = FCode;
    }

    public String getTypeCode() {
        return TypeCode;
    }

    public void setTypeCode(String TypeCode) {
        this.TypeCode = TypeCode;
    }

    public String getTypeName() {
        return TypeName;
    }

    public void setTypeName(String TypeName) {
        this.TypeName = TypeName;
    }

    public int getSortNo() {
        return SortNo;
    }

    public void setSortNo(int SortNo) {
        this.SortNo = SortNo;
    }

    public Object getRemark() {
        return Remark;
    }

    public void setRemark(Object Remark) {
        this.Remark = Remark;
    }

    public int getValid() {
        return Valid;
    }

    public void setValid(int Valid) {
        this.Valid = Valid;
    }

    public List<ChildNodeBean> getChildNode() {
        return ChildNode;
    }

    public void setChildNode(List<ChildNodeBean> ChildNode) {
        this.ChildNode = ChildNode;
    }

    public static class ChildNodeBean {
        private int ID;
        private int MainID;
        private String FCode;
        private String TypeCode;
        private String TypeName;
        private int SortNo;
        private Object Remark;
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

        public String getFCode() {
            return FCode;
        }

        public void setFCode(String FCode) {
            this.FCode = FCode;
        }

        public String getTypeCode() {
            return TypeCode;
        }

        public void setTypeCode(String TypeCode) {
            this.TypeCode = TypeCode;
        }

        public String getTypeName() {
            return TypeName;
        }

        public void setTypeName(String TypeName) {
            this.TypeName = TypeName;
        }

        public int getSortNo() {
            return SortNo;
        }

        public void setSortNo(int SortNo) {
            this.SortNo = SortNo;
        }

        public Object getRemark() {
            return Remark;
        }

        public void setRemark(Object Remark) {
            this.Remark = Remark;
        }

        public int getValid() {
            return Valid;
        }

        public void setValid(int Valid) {
            this.Valid = Valid;
        }
    }
}
