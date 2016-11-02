package com.y.w.ywker.entry;

/**
 * Created by lxs on 16/4/22.
 */
public class OrderListItemGroupEntry {

    public OrderListItemGroupEntry(OrderListEntry.DetailBean entry,String title,int parentid){
        if (entry != null){
            setID(entry.getID());
            setSheetTitle(entry.getSheetTitle());
            setClientName(entry.getClientName());
            setMessage(entry.getMessage());
            setSheetPriority(entry.getSheetPriority());
            setSheetStateCN(entry.getSheetStateCN());
            setWriteTime(entry.getWriteTime());

            setParentID(parentid);
            setParentTitle(title);
        }
    }

    private int ID;
    private String WriteTime;
    private String SheetTitle;
    private int SheetPriority;
    private String SheetStateCN;
    private String ClientName;
    private String Message;

    public int getParentID() {
        return parentID;
    }

    public void setParentID(int parentID) {
        this.parentID = parentID;
    }

    public String getParentTitle() {
        return parentTitle;
    }

    public void setParentTitle(String parentTitle) {
        this.parentTitle = parentTitle;
    }

    private int parentID;
    private String parentTitle;
    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getWriteTime() {
        return WriteTime;
    }

    public void setWriteTime(String WriteTime) {
        this.WriteTime = WriteTime;
    }

    public String getSheetTitle() {
        return SheetTitle;
    }

    public void setSheetTitle(String SheetTitle) {
        this.SheetTitle = SheetTitle;
    }

    public int getSheetPriority() {
        return SheetPriority;
    }

    public void setSheetPriority(int SheetPriority) {
        this.SheetPriority = SheetPriority;
    }

    public String getSheetStateCN() {
        return SheetStateCN;
    }

    public void setSheetStateCN(String SheetStateCN) {
        this.SheetStateCN = SheetStateCN;
    }

    public String getClientName() {
        return ClientName;
    }

    public void setClientName(String ClientName) {
        this.ClientName = ClientName;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }
}
