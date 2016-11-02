package com.y.w.ywker.entry;

/**
 * Created by lxs on 16/5/3.
 */
public class UnReadMsgEntry {
    /**
     * ID : 1
     * Name : 系统管理员
     * Count : 2
     * MessageTime : /Date(1460599963000)/
     * Detail : 一样样图一样
     */

    private int ID = 0;
    private String Name = "";
    private int Count = 0;
    private String MessageTime = "";
    private String Detail = "";

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public int getCount() {
        return Count;
    }

    public void setCount(int Count) {
        this.Count = Count;
    }

    public String getMessageTime() {
        return MessageTime;
    }

    public void setMessageTime(String MessageTime) {
        this.MessageTime = MessageTime;
    }

    public String getDetail() {
        return Detail;
    }

    public void setDetail(String Detail) {
        this.Detail = Detail;
    }
}
