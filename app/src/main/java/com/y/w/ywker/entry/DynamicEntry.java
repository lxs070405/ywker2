package com.y.w.ywker.entry;

/**
 * Created by lxs on 16/4/27.
 */
public class DynamicEntry {


    /**
     * Client : 邮政银行
     * DataType : 1
     * ID : 64919
     * IsRead : false
     * MessageType : 工单状态
     * SendDetail : 新工单，受理人：李晓帅，工单状态：已受理。
     * SendTime : /Date(1476949030000)/
     * SheetID : 21234
     * Title : 李晓帅发布了故障类型的工单(2016/10/20 15:37)
     */

    private String Client;
    private int DataType;
    private int ID;
    private boolean IsRead;
    private String MessageType;
    private String SendDetail;
    private String SendTime;
    private int SheetID;
    private String Title;

    public void setClient(String Client) {
        this.Client = Client;
    }

    public void setDataType(int DataType) {
        this.DataType = DataType;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setIsRead(boolean IsRead) {
        this.IsRead = IsRead;
    }

    public void setMessageType(String MessageType) {
        this.MessageType = MessageType;
    }

    public void setSendDetail(String SendDetail) {
        this.SendDetail = SendDetail;
    }

    public void setSendTime(String SendTime) {
        this.SendTime = SendTime;
    }

    public void setSheetID(int SheetID) {
        this.SheetID = SheetID;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public String getClient() {
        return Client;
    }

    public int getDataType() {
        return DataType;
    }

    public int getID() {
        return ID;
    }

    public boolean getIsRead() {
        return IsRead;
    }

    public String getMessageType() {
        return MessageType;
    }

    public String getSendDetail() {
        return SendDetail;
    }

    public String getSendTime() {
        return SendTime;
    }

    public int getSheetID() {
        return SheetID;
    }

    public String getTitle() {
        return Title;
    }
}
