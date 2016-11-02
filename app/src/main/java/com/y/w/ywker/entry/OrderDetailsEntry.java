package com.y.w.ywker.entry;

/**
 * Created by lxs on 16/4/25.
 */
public class OrderDetailsEntry {
    /**
     * ID : 1
     * MainID : 1
     * ClientID : 1
     * ClientCode : 001
     * WriteID : 1
     * WriteTime : 2016/4/20 17:26:17
     * WriteAdr :
     * SheetTitle : Test 测试工单
     * SheetDetail :
     * SheetPriority : -1
     * SheetState : 1
     * Valid : 1
     * TypeName : 实物
     * WriterName : 系统管理员
     * SheetStateCN : 未受理
     * TeamID : 1
     * AcceptID : 2
     * IsAccept : 1
     * UserName : 杨殿君
     * TeamName : 技术组
     * ClientName : 中国工商银行河南分行
     * SheetType : 2
     * ClientAdr : 工商河分地址
     * Contact : 张经理，科技部；张经理，科技部；
     * Priority :
     * Follow : 系统管理员，
     * lianxiren: 联系人
     * bumen: 部门,
     * dianhua: 电话
     * youxiang: 邮箱
     * weixin:微信
     * FollowID
     * SheetSummary  工作总结
     */
    private String SheetSummary ;
    private String bumen = "";
    private String dianhua = "";
    private String youxiang = "";
    private String winxin = "";
    private String lianxiren = "";
    public String getSheetSummary(){
        return SheetSummary;
    }
    public void setSheetSummary(String sheetSummary){
        this.SheetSummary = sheetSummary;
    }
    public void setLianxiren(String lianxiren){
        this.lianxiren = lianxiren;
    }
    public String getLianxiren(){
        return lianxiren;
    }
    public String getFollowID() {
        return FollowID;
    }

    public void setFollowID(String followID) {
        FollowID = followID;
    }

    private String FollowID = "";

    public String getBumen() {
        return bumen;
    }

    public void setBumen(String bumen) {
        this.bumen = bumen;
    }

    public String getDianhua() {
        return dianhua;
    }

    public void setDianhua(String dianhua) {
        this.dianhua = dianhua;
    }

    public String getWinxin() {
        return winxin;
    }

    public void setWinxin(String winxin) {
        this.winxin = winxin;
    }

    public String getYouxiang() {
        return youxiang;
    }

    public void setYouxiang(String youxiang) {
        this.youxiang = youxiang;
    }

    private String ID = "";
    private String MainID = "";
    private String ClientID = "";
    private String ClientCode = "";
    private String WriteID = "";
    private String WriteTime = "";
    private String WriteAdr = "";
    private String SheetTitle = "";
    private String SheetDetail = "";
    private String SheetPriority = "";
    private String SheetState = "";
    private String Valid = "";
    private String TypeName = "";
    private String WriterName = "";
    private String SheetStateCN = "";
    private String TeamID = "";
    private String AcceptID = "";
    private String IsAccept = "";
    private String UserName = "";
    private String TeamName = "";
    private String ClientName = "";
    private String SheetType = "";
    private String ClientAdr = "";
    private String Contact = "";
    private String Priority = "";
    private String Follow = "";

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

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

    public String getClientCode() {
        return ClientCode;
    }

    public void setClientCode(String ClientCode) {
        this.ClientCode = ClientCode;
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

    public String getValid() {
        return Valid;
    }

    public void setValid(String Valid) {
        this.Valid = Valid;
    }

    public String getTypeName() {
        return TypeName;
    }

    public void setTypeName(String TypeName) {
        this.TypeName = TypeName;
    }

    public String getWriterName() {
        return WriterName;
    }

    public void setWriterName(String WriterName) {
        this.WriterName = WriterName;
    }

    public String getSheetStateCN() {
        return SheetStateCN;
    }

    public void setSheetStateCN(String SheetStateCN) {
        this.SheetStateCN = SheetStateCN;
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

    public String getIsAccept() {
        return IsAccept;
    }

    public void setIsAccept(String IsAccept) {
        this.IsAccept = IsAccept;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    public String getTeamName() {
        return TeamName;
    }

    public void setTeamName(String TeamName) {
        this.TeamName = TeamName;
    }

    public String getClientName() {
        return ClientName;
    }

    public void setClientName(String ClientName) {
        this.ClientName = ClientName;
    }

    public String getSheetType() {
        return SheetType;
    }

    public void setSheetType(String SheetType) {
        this.SheetType = SheetType;
    }

    public String getClientAdr() {
        return ClientAdr;
    }

    public void setClientAdr(String ClientAdr) {
        this.ClientAdr = ClientAdr;
    }

    public String getContact() {
        return Contact;
    }

    public void setContact(String Contact) {
        this.Contact = Contact;
    }

    public String getPriority() {
        return Priority;
    }

    public void setPriority(String Priority) {
        this.Priority = Priority;
    }

    public String getFollow() {
        return Follow;
    }

    public void setFollow(String Follow) {
        this.Follow = Follow;
    }
}
