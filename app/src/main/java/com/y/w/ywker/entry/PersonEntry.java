package com.y.w.ywker.entry;

import java.util.List;

/**
 * Created by HF02 on 2016/8/23.
 * 人员表
 */
public class PersonEntry {

    /**
     * ID : 1
     * MainID : 1
     * Remark : 技术组
     * TeamCode : 001
     * TeamName : 技术组
     * Valid : 1
     * memeber : [{"ID":10246,"MainID":1,"TeamID":1,"TeamName":"技术组","UserCode":"13073709034","UserID":20138,"UserName":"HF07"},{"ID":10182,"MainID":1,"TeamID":1,"TeamName":"技术组","UserCode":"fhl","UserID":10017,"UserName":"冯浩龙"},{"ID":10187,"MainID":1,"TeamID":1,"TeamName":"技术组","UserCode":"jyh","UserID":10022,"UserName":"姜银华"},{"ID":10184,"MainID":1,"TeamID":1,"TeamName":"技术组","UserCode":"lgy","UserID":10019,"UserName":"李光耀"},{"ID":10183,"MainID":1,"TeamID":1,"TeamName":"技术组","UserCode":"lmz","UserID":10018,"UserName":"李明哲"},{"ID":10186,"MainID":1,"TeamID":1,"TeamName":"技术组","UserCode":"qcd","UserID":10035,"UserName":"齐昌东"},{"ID":10185,"MainID":1,"TeamID":1,"TeamName":"技术组","UserCode":"yjw","UserID":10020,"UserName":"杨军伟"},{"ID":10188,"MainID":1,"TeamID":1,"TeamName":"技术组","UserCode":"zq","UserID":10077,"UserName":"张强"}]
     */

    private int ID;
    private int MainID;
    private String Remark;
    private String TeamCode;
    private String TeamName;
    private int Valid;
    /**
     * ID : 10246
     * MainID : 1
     * TeamID : 1
     * TeamName : 技术组
     * UserCode : 13073709034
     * UserID : 20138
     * UserName : HF07
     */

    private List<MemeberBean> memeber;

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

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String Remark) {
        this.Remark = Remark;
    }

    public String getTeamCode() {
        return TeamCode;
    }

    public void setTeamCode(String TeamCode) {
        this.TeamCode = TeamCode;
    }

    public String getTeamName() {
        return TeamName;
    }

    public void setTeamName(String TeamName) {
        this.TeamName = TeamName;
    }

    public int getValid() {
        return Valid;
    }

    public void setValid(int Valid) {
        this.Valid = Valid;
    }

    public List<MemeberBean> getMemeber() {
        return memeber;
    }

    public void setMemeber(List<MemeberBean> memeber) {
        this.memeber = memeber;
    }

    public static class MemeberBean {
        private int ID;
        private int MainID;
        private int TeamID;
        private String TeamName;
        private String UserCode;
        private int UserID;
        private String UserName;

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

        public int getTeamID() {
            return TeamID;
        }

        public void setTeamID(int TeamID) {
            this.TeamID = TeamID;
        }

        public String getTeamName() {
            return TeamName;
        }

        public void setTeamName(String TeamName) {
            this.TeamName = TeamName;
        }

        public String getUserCode() {
            return UserCode;
        }

        public void setUserCode(String UserCode) {
            this.UserCode = UserCode;
        }

        public int getUserID() {
            return UserID;
        }

        public void setUserID(int UserID) {
            this.UserID = UserID;
        }

        public String getUserName() {
            return UserName;
        }

        public void setUserName(String UserName) {
            this.UserName = UserName;
        }
    }
}
