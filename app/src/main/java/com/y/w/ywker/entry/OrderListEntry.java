package com.y.w.ywker.entry;

import java.util.List;

/**
 * Created by lxs on 16/4/22.
 */
public class OrderListEntry {

    /**
     * ID : 3
     * TypeName : 故障
     * Detail : [{"ID":15,"WriteTime":"/Date(1461288239000)/","SheetTitle":"这是一个测试工单","SheetPriority":4,"SheetStateCN":"未受理","ClientName":"中国工商银行河南分行","Message":""},{"ID":16,"WriteTime":"/Date(1461288358000)/","SheetTitle":"这是一个测试工单","SheetPriority":3,"SheetStateCN":"未受理","ClientName":"中国工商银行河南分行","Message":""}]
     */

    private int ID;
    private String TypeName;

    /**
     * ID : 15
     * WriteTime : /Date(1461288239000)/
     * SheetTitle : 这是一个测试工单
     * SheetPriority : 4
     * SheetStateCN : 未受理
     * ClientName : 中国工商银行河南分行
     * Message :
     */

    private List<DetailBean> Detail;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTypeName() {
        return TypeName;
    }

    public void setTypeName(String TypeName) {
        this.TypeName = TypeName;
    }

    public List<DetailBean> getDetail() {
        return Detail;
    }

    public void setDetail(List<DetailBean> Detail) {
        this.Detail = Detail;
    }

    public static class DetailBean {
        private int ID;
        private String WriteTime;
        private String SheetTitle;
        private int SheetPriority;
        private String SheetStateCN;
        private String ClientName;
        private String Message;

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
}
