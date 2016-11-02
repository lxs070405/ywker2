package com.y.w.ywker.entry;

/**
 * Created by lxs on 16/5/5.
 */
public class MsgDetailsEntry {

    /**
     * ID : 7
     * SendTime : /Date(1461759269000)/
     * SendDetail : wsfdsfd
     * MessageType : Detail
     */

    private int ID;
    private String SendTime;
    private String SendDetail;
    private String MessageType;
    private int SendID = 0;
    private String ReceiveID = "";
    public String getFileDown() {
        return FileDown;
    }

    public void setFileDown(String fileDown) {
        FileDown = fileDown;
    }

    private String FileDown = "";
    public String getReceiveID() {
        return ReceiveID;
    }

    public void setReceiveID(String receiveID) {
        ReceiveID = receiveID;
    }

    public int getSendID() {
        return SendID;
    }

    public void setSendID(int sendID) {
        SendID = sendID;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
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

    public String getMessageType() {
        return MessageType;
    }

    public void setMessageType(String MessageType) {
        this.MessageType = MessageType;
    }
}
