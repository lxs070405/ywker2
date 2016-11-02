package com.y.w.ywker.entry;

import java.io.Serializable;
import java.util.List;

/**
 * Created by HF02 on 2016/10/1.
 * 维修工单详情
 */

public class WeiXiuOrderDetailEntry {

    /**
     * ID : 8
     * MainID : 1
     * ClientID : 166
     * ClientCode : 0004
     * TaskCode : 1004920160929101326
     * TaskDetail : 扫描发布维修
     * TaskPriority : 3
     * LinkName : 李晓帅
     * LinkTel : 13598847704
     * AssetTypeID : 12
     * WriteID : 10049
     * WriteTime : /Date(1475115206000)/
     * WriteAdr :
     * TaskState : 2
     * Valid : 1
     * UpdateTime : /Date(1475115206000)/
     * UpdateWriter : 10049
     * RepaireSummary :
     * AcceptID : 10049
     * TeamID : 10
     * acceptName : 李晓帅
     * ClientName : 济源王斌
     * TypeName : as自动填单机
     * RealseTime : 2016-09-29 10:13:26
     * ClientAdr : null
     * realseName : 李晓帅
     * taskStatus : 已受理
     * UseTime : 6 小时
     * AssetList : [{"ID":8,"MainId":1,"TaskID":8,"AssetID":16075,"RepairSchedule":0,"AssetPic":"","RepairSummary":"","WriteDate":"/Date(1475115206000)/","WriteUserID":10049,"Valid":true,"AssetName":"as自动填单机 四通 咯无聊","SpdName":""}]
     */

    private int ID;
    private int MainID;
    private int ClientID;
    private String ClientCode;
    private String TaskCode;
    private String TaskDetail;
    private int TaskPriority;
    private String LinkName;
    private String LinkTel;
    private int AssetTypeID;
    private int WriteID;
    private String WriteTime;
    private String WriteAdr;
    private int TaskState;
    private int Valid;
    private String UpdateTime;
    private int UpdateWriter;
    private String RepaireSummary;
    private int AcceptID;
    private int TeamID;
    private String acceptName;
    private String ClientName;
    private String TypeName;
    private String RealseTime;
    private Object ClientAdr;
    private String realseName;
    private String taskStatus;
    private String UseTime;
    private List<AssetListEntity> AssetList;

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setMainID(int MainID) {
        this.MainID = MainID;
    }

    public void setClientID(int ClientID) {
        this.ClientID = ClientID;
    }

    public void setClientCode(String ClientCode) {
        this.ClientCode = ClientCode;
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

    public void setLinkName(String LinkName) {
        this.LinkName = LinkName;
    }

    public void setLinkTel(String LinkTel) {
        this.LinkTel = LinkTel;
    }

    public void setAssetTypeID(int AssetTypeID) {
        this.AssetTypeID = AssetTypeID;
    }

    public void setWriteID(int WriteID) {
        this.WriteID = WriteID;
    }

    public void setWriteTime(String WriteTime) {
        this.WriteTime = WriteTime;
    }

    public void setWriteAdr(String WriteAdr) {
        this.WriteAdr = WriteAdr;
    }

    public void setTaskState(int TaskState) {
        this.TaskState = TaskState;
    }

    public void setValid(int Valid) {
        this.Valid = Valid;
    }

    public void setUpdateTime(String UpdateTime) {
        this.UpdateTime = UpdateTime;
    }

    public void setUpdateWriter(int UpdateWriter) {
        this.UpdateWriter = UpdateWriter;
    }

    public void setRepaireSummary(String RepaireSummary) {
        this.RepaireSummary = RepaireSummary;
    }

    public void setAcceptID(int AcceptID) {
        this.AcceptID = AcceptID;
    }

    public void setTeamID(int TeamID) {
        this.TeamID = TeamID;
    }

    public void setAcceptName(String acceptName) {
        this.acceptName = acceptName;
    }

    public void setClientName(String ClientName) {
        this.ClientName = ClientName;
    }

    public void setTypeName(String TypeName) {
        this.TypeName = TypeName;
    }

    public void setRealseTime(String RealseTime) {
        this.RealseTime = RealseTime;
    }

    public void setClientAdr(Object ClientAdr) {
        this.ClientAdr = ClientAdr;
    }

    public void setRealseName(String realseName) {
        this.realseName = realseName;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public void setUseTime(String UseTime) {
        this.UseTime = UseTime;
    }

    public void setAssetList(List<AssetListEntity> AssetList) {
        this.AssetList = AssetList;
    }

    public int getID() {
        return ID;
    }

    public int getMainID() {
        return MainID;
    }

    public int getClientID() {
        return ClientID;
    }

    public String getClientCode() {
        return ClientCode;
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

    public String getLinkName() {
        return LinkName;
    }

    public String getLinkTel() {
        return LinkTel;
    }

    public int getAssetTypeID() {
        return AssetTypeID;
    }

    public int getWriteID() {
        return WriteID;
    }

    public String getWriteTime() {
        return WriteTime;
    }

    public String getWriteAdr() {
        return WriteAdr;
    }

    public int getTaskState() {
        return TaskState;
    }

    public int getValid() {
        return Valid;
    }

    public String getUpdateTime() {
        return UpdateTime;
    }

    public int getUpdateWriter() {
        return UpdateWriter;
    }

    public String getRepaireSummary() {
        return RepaireSummary;
    }

    public int getAcceptID() {
        return AcceptID;
    }

    public int getTeamID() {
        return TeamID;
    }

    public String getAcceptName() {
        return acceptName;
    }

    public String getClientName() {
        return ClientName;
    }

    public String getTypeName() {
        return TypeName;
    }

    public String getRealseTime() {
        return RealseTime;
    }

    public Object getClientAdr() {
        return ClientAdr;
    }

    public String getRealseName() {
        return realseName;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public String getUseTime() {
        return UseTime;
    }

    public List<AssetListEntity> getAssetList() {
        return AssetList;
    }

    public static class AssetListEntity implements Serializable {
        /**
         * ID : 8
         * MainId : 1
         * TaskID : 8
         * AssetID : 16075
         * RepairSchedule : 0
         * AssetPic :
         * RepairSummary :
         * WriteDate : /Date(1475115206000)/
         * WriteUserID : 10049
         * Valid : true
         * AssetName : as自动填单机 四通 咯无聊
         * SpdName :
         */

        private int ID;
        private int MainId;
        private int TaskID;
        private int AssetID;
        private int RepairSchedule;
        private String AssetPic;
        private String RepairSummary;
        private String WriteDate;
        private int WriteUserID;
        private boolean Valid;
        private String AssetName;
        private String SpdName;

        public void setID(int ID) {
            this.ID = ID;
        }

        public void setMainId(int MainId) {
            this.MainId = MainId;
        }

        public void setTaskID(int TaskID) {
            this.TaskID = TaskID;
        }

        public void setAssetID(int AssetID) {
            this.AssetID = AssetID;
        }

        public void setRepairSchedule(int RepairSchedule) {
            this.RepairSchedule = RepairSchedule;
        }

        public void setAssetPic(String AssetPic) {
            this.AssetPic = AssetPic;
        }

        public void setRepairSummary(String RepairSummary) {
            this.RepairSummary = RepairSummary;
        }

        public void setWriteDate(String WriteDate) {
            this.WriteDate = WriteDate;
        }

        public void setWriteUserID(int WriteUserID) {
            this.WriteUserID = WriteUserID;
        }

        public void setValid(boolean Valid) {
            this.Valid = Valid;
        }

        public void setAssetName(String AssetName) {
            this.AssetName = AssetName;
        }

        public void setSpdName(String SpdName) {
            this.SpdName = SpdName;
        }

        public int getID() {
            return ID;
        }

        public int getMainId() {
            return MainId;
        }

        public int getTaskID() {
            return TaskID;
        }

        public int getAssetID() {
            return AssetID;
        }

        public int getRepairSchedule() {
            return RepairSchedule;
        }

        public String getAssetPic() {
            return AssetPic;
        }

        public String getRepairSummary() {
            return RepairSummary;
        }

        public String getWriteDate() {
            return WriteDate;
        }

        public int getWriteUserID() {
            return WriteUserID;
        }

        public boolean getValid() {
            return Valid;
        }

        public String getAssetName() {
            return AssetName;
        }

        public String getSpdName() {
            return SpdName;
        }
    }
}
