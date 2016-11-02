package com.y.w.ywker.entry;

/**
 * Created by lxs on 16/5/3.
 * 工单信息
 */
public class ReplayMsgEntry {
    /**
     * ID : 1
     * SheetID : 1
     * SendTime : /Date(1461144377000)/
     * SendDetail : 这是杨殿君文字接口测试
     * UserName : 系统管理员
     * MessageType : Detail
     * UserRole : 发起人
     */

    private int ID = 0;
    private int SheetID = 0;
    private String SendTime = "";
    private String SendDetail = "";
    private String UserName = "";
    private String MessageType = "";
    private String UserRole = "";

    public String getFileDown() {
        return FileDown;
    }

    public void setFileDown(String fileDown) {
        FileDown = fileDown;
    }

    private String FileDown = "";
    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getSheetID() {
        return SheetID;
    }

    public void setSheetID(int SheetID) {
        this.SheetID = SheetID;
    }

    public String getSendTime() {
        return SendTime;
    }

    public void setSendTime(String SendTime) {
        this.SendTime = SendTime;
    }

    public String getSendDetail() {
        return SendDetail;
    }

    public void setSendDetail(String SendDetail) {
        this.SendDetail = SendDetail;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    public String getMessageType() {
        return MessageType;
    }

    public void setMessageType(String MessageType) {
        this.MessageType = MessageType;
    }

    public String getUserRole() {
        return UserRole;
    }

    public void setUserRole(String UserRole) {
        this.UserRole = UserRole;
    }
}
