package com.y.w.ywker.entry;

import java.util.List;

/**
 * Created by HF02 on 2016/9/30.
 */

public class AllWeiXiuListEntry {

    /**
     * AcceptName : 李晓帅
     * NowAcceptTask : [{"AcceptID":10049,"AssetTypeID":12,"ClientCode":"0004","ClientID":166,"ClientName":"济源王斌","ID":5,"LinkName":"李小帅","LinkTel":"13598847704","MainID":1,"RealseTime":"2016-09-29 09:41:17","RepaireSummary":"","TaskCode":"1004920160929094116","TaskDetail":"发布维修","TaskPriority":3,"TaskState":2,"TeamID":10,"TypeName":"as自动填单机","UpdateTime":"/Date(1475113277000)/","UpdateWriter":10049,"Valid":1,"WriteAdr":"","WriteID":10049,"WriteTime":"/Date(1475113277000)/","acceptName":"李晓帅"},{"AcceptID":10049,"AssetTypeID":26,"ClientCode":"0036","ClientID":198,"ClientName":"工行（自助中心）","ID":6,"LinkName":"44","LinkTel":"555","MainID":1,"RealseTime":"2016-09-29 09:45:03","RepaireSummary":"","TaskCode":"","TaskDetail":"5555","TaskPriority":3,"TaskState":2,"TeamID":10,"TypeName":"计算机","UpdateTime":"/Date(1475113503000)/","UpdateWriter":10098,"Valid":1,"WriteAdr":"","WriteID":10098,"WriteTime":"/Date(1475113503000)/","acceptName":"李晓帅"},{"AcceptID":10049,"AssetTypeID":25,"ClientCode":"0108","ClientID":270,"ClientName":"安阳晨龙","ID":7,"LinkName":"55","LinkTel":"66","MainID":1,"RealseTime":"2016-09-29 09:46:28","RepaireSummary":"","TaskCode":"","TaskDetail":"ttt","TaskPriority":3,"TaskState":6,"TeamID":10,"TypeName":"公司汽车","UpdateTime":"/Date(1475113588000)/","UpdateWriter":10098,"Valid":1,"WriteAdr":"","WriteID":10098,"WriteTime":"/Date(1475113588000)/","acceptName":"李晓帅"},{"AcceptID":10049,"AssetTypeID":12,"ClientCode":"0004","ClientID":166,"ClientName":"济源王斌","ID":8,"LinkName":"李晓帅","LinkTel":"13598847704","MainID":1,"RealseTime":"2016-09-29 10:13:26","RepaireSummary":"","TaskCode":"1004920160929101326","TaskDetail":"扫描发布维修","TaskPriority":3,"TaskState":2,"TeamID":10,"TypeName":"as自动填单机","UpdateTime":"/Date(1475115206000)/","UpdateWriter":10049,"Valid":1,"WriteAdr":"","WriteID":10049,"WriteTime":"/Date(1475115206000)/","acceptName":"李晓帅"}]
     */

    private String AcceptName;
    private List<NowAcceptTaskEntity> NowAcceptTask;

    public void setAcceptName(String AcceptName) {
        this.AcceptName = AcceptName;
    }

    public void setNowAcceptTask(List<NowAcceptTaskEntity> NowAcceptTask) {
        this.NowAcceptTask = NowAcceptTask;
    }

    public String getAcceptName() {
        return AcceptName;
    }

    public List<NowAcceptTaskEntity> getNowAcceptTask() {
        return NowAcceptTask;
    }

    public static class NowAcceptTaskEntity {
        /**
         * AcceptID : 10049
         * AssetTypeID : 12
         * ClientCode : 0004
         * ClientID : 166
         * ClientName : 济源王斌
         * ID : 5
         * LinkName : 李小帅
         * LinkTel : 13598847704
         * MainID : 1
         * RealseTime : 2016-09-29 09:41:17
         * RepaireSummary :
         * TaskCode : 1004920160929094116
         * TaskDetail : 发布维修
         * TaskPriority : 3
         * TaskState : 2
         * TeamID : 10
         * TypeName : as自动填单机
         * UpdateTime : /Date(1475113277000)/
         * UpdateWriter : 10049
         * Valid : 1
         * WriteAdr :
         * WriteID : 10049
         * WriteTime : /Date(1475113277000)/
         * acceptName : 李晓帅
         */

        private int AcceptID;
        private int AssetTypeID;
        private String ClientCode;
        private int ClientID;
        private String ClientName;
        private int ID;
        private String LinkName;
        private String LinkTel;
        private int MainID;
        private String RealseTime;
        private String RepaireSummary;
        private String TaskCode;
        private String TaskDetail;
        private int TaskPriority;
        private int TaskState;
        private int TeamID;
        private String TypeName;
        private String UpdateTime;
        private int UpdateWriter;
        private int Valid;
        private String WriteAdr;
        private int WriteID;
        private String WriteTime;
        private String acceptName;

        public void setAcceptID(int AcceptID) {
            this.AcceptID = AcceptID;
        }

        public void setAssetTypeID(int AssetTypeID) {
            this.AssetTypeID = AssetTypeID;
        }

        public void setClientCode(String ClientCode) {
            this.ClientCode = ClientCode;
        }

        public void setClientID(int ClientID) {
            this.ClientID = ClientID;
        }

        public void setClientName(String ClientName) {
            this.ClientName = ClientName;
        }

        public void setID(int ID) {
            this.ID = ID;
        }

        public void setLinkName(String LinkName) {
            this.LinkName = LinkName;
        }

        public void setLinkTel(String LinkTel) {
            this.LinkTel = LinkTel;
        }

        public void setMainID(int MainID) {
            this.MainID = MainID;
        }

        public void setRealseTime(String RealseTime) {
            this.RealseTime = RealseTime;
        }

        public void setRepaireSummary(String RepaireSummary) {
            this.RepaireSummary = RepaireSummary;
        }

        public void setTaskCode(String TaskCode) {
            this.TaskCode = TaskCode;
        }

        public void setTaskDetail(String TaskDetail) {
            this.TaskDetail = TaskDetail;
        }

        public void setTaskPriority(int TaskPriority) {
            this.TaskPriority = TaskPriority;
        }

        public void setTaskState(int TaskState) {
            this.TaskState = TaskState;
        }

        public void setTeamID(int TeamID) {
            this.TeamID = TeamID;
        }

        public void setTypeName(String TypeName) {
            this.TypeName = TypeName;
        }

        public void setUpdateTime(String UpdateTime) {
            this.UpdateTime = UpdateTime;
        }

        public void setUpdateWriter(int UpdateWriter) {
            this.UpdateWriter = UpdateWriter;
        }

        public void setValid(int Valid) {
            this.Valid = Valid;
        }

        public void setWriteAdr(String WriteAdr) {
            this.WriteAdr = WriteAdr;
        }

        public void setWriteID(int WriteID) {
            this.WriteID = WriteID;
        }

        public void setWriteTime(String WriteTime) {
            this.WriteTime = WriteTime;
        }

        public void setAcceptName(String acceptName) {
            this.acceptName = acceptName;
        }

        public int getAcceptID() {
            return AcceptID;
        }

        public int getAssetTypeID() {
            return AssetTypeID;
        }

        public String getClientCode() {
            return ClientCode;
        }

        public int getClientID() {
            return ClientID;
        }

        public String getClientName() {
            return ClientName;
        }

        public int getID() {
            return ID;
        }

        public String getLinkName() {
            return LinkName;
        }

        public String getLinkTel() {
            return LinkTel;
        }

        public int getMainID() {
            return MainID;
        }

        public String getRealseTime() {
            return RealseTime;
        }

        public String getRepaireSummary() {
            return RepaireSummary;
        }

        public String getTaskCode() {
            return TaskCode;
        }

        public String getTaskDetail() {
            return TaskDetail;
        }

        public int getTaskPriority() {
            return TaskPriority;
        }

        public int getTaskState() {
            return TaskState;
        }

        public int getTeamID() {
            return TeamID;
        }

        public String getTypeName() {
            return TypeName;
        }

        public String getUpdateTime() {
            return UpdateTime;
        }

        public int getUpdateWriter() {
            return UpdateWriter;
        }

        public int getValid() {
            return Valid;
        }

        public String getWriteAdr() {
            return WriteAdr;
        }

        public int getWriteID() {
            return WriteID;
        }

        public String getWriteTime() {
            return WriteTime;
        }

        public String getAcceptName() {
            return acceptName;
        }
    }
}
