package com.y.w.ywker.entry;

/**
 * Created by lxs on 16/4/21.
 */
public class NewOrderEntry {

    /**
     * MainID : 服务商主键
     * ClientID : 客户主键
     * WriteID : 发布人主键及登录人主键
     * WriteTime : 发布时间
     * WriteAdr : 发布时所处地点
     * SheetType : 工单类型主键
     * SheetTitle : 主题
     * SheetDetail : 描述
     * SheetPriority : 优先级
     * SheetState : 当前状态
     * TeamID : 服务组主键
     * AcceptID : 受理人主键
     * FollowID : 关注人主键以逗号隔开选择的关注人的主键拼接到一起
     */

    private String MainID = "";
    private String ClientID = "";
    private String WriteID = "";
    private String WriteTime = "";
    private String WriteAdr = "";
    private String SheetType = "";
    private String SheetTitle = "";
    private String SheetDetail = "";
    private String SheetPriority = "";
    private String SheetState = "";
    private String TeamID = "";
    private String AcceptID = "";
    private String FollowID = "";

    public String getMainID() {
        return MainID;
    }

    public void setMainID(String MainID) {
        this.MainID = MainID;
    }

    public String getClientID() {
        return ClientID;
    }

    public void setClientID(String ClientID) {
        this.ClientID = ClientID;
    }

    public String getWriteID() {
        return WriteID;
    }

    public void setWriteID(String WriteID) {
        this.WriteID = WriteID;
    }

    public String getWriteTime() {
        return WriteTime;
    }

    public void setWriteTime(String WriteTime) {
        this.WriteTime = WriteTime;
    }

    public String getWriteAdr() {
        return WriteAdr;
    }

    public void setWriteAdr(String WriteAdr) {
        this.WriteAdr = WriteAdr;
    }

    public String getSheetType() {
        return SheetType;
    }

    public void setSheetType(String SheetType) {
        this.SheetType = SheetType;
    }

    public String getSheetTitle() {
        return SheetTitle;
    }

    public void setSheetTitle(String SheetTitle) {
        this.SheetTitle = SheetTitle;
    }

    public String getSheetDetail() {
        return SheetDetail;
    }

    public void setSheetDetail(String SheetDetail) {
        this.SheetDetail = SheetDetail;
    }

    public String getSheetPriority() {
        return SheetPriority;
    }

    public void setSheetPriority(String SheetPriority) {
        this.SheetPriority = SheetPriority;
    }

    public String getSheetState() {
        return SheetState;
    }

    public void setSheetState(String SheetState) {
        this.SheetState = SheetState;
    }

    public String getTeamID() {
        return TeamID;
    }

    public void setTeamID(String TeamID) {
        this.TeamID = TeamID;
    }

    public String getAcceptID() {
        return AcceptID;
    }

    public void setAcceptID(String AcceptID) {
        this.AcceptID = AcceptID;
    }

    public String getFollowID() {
        return FollowID;
    }

    public void setFollowID(String FollowID) {
        this.FollowID = FollowID;
    }
}
