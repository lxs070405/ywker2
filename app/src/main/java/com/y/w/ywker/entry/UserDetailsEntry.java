package com.y.w.ywker.entry;

/**
 * Created by lxs on 16/5/5.
 * 用户详情
 */
public class UserDetailsEntry {

    /**
     * ID : 2
     * UserName : 杨殿君
     * SheetCount : 0
     * UserRole : 普通员工
     * UserTeam : 技术组
     * Tel :
     * CreatTime : 2016/4/14 10:12:43
     */

    private int ID = 0;
    private String UserName = "";
    private int SheetCount = 0;
    private String UserRole = "";
    private String UserTeam = "";
    private String Tel = "";
    private String CreatTime = "";

    public String getSheetTeamID() {
        return SheetTeamID;
    }

    public void setSheetTeamID(String sheetTeamID) {
        SheetTeamID = sheetTeamID;
    }

    public String getSheetTeamName() {
        return SheetTeamName;
    }

    public void setSheetTeamName(String sheetTeamName) {
        SheetTeamName = sheetTeamName;
    }

    private String SheetTeamID = "";
    private String SheetTeamName = "";


    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    public int getSheetCount() {
        return SheetCount;
    }

    public void setSheetCount(int SheetCount) {
        this.SheetCount = SheetCount;
    }

    public String getUserRole() {
        return UserRole;
    }

    public void setUserRole(String UserRole) {
        this.UserRole = UserRole;
    }

    public String getUserTeam() {
        return UserTeam;
    }

    public void setUserTeam(String UserTeam) {
        this.UserTeam = UserTeam;
    }

    public String getTel() {
        return Tel;
    }

    public void setTel(String Tel) {
        this.Tel = Tel;
    }

    public String getCreatTime() {
        return CreatTime;
    }

    public void setCreatTime(String CreatTime) {
        this.CreatTime = CreatTime;
    }
}
