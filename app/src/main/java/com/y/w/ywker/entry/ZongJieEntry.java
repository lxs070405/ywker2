package com.y.w.ywker.entry;

import java.util.List;

/**
 * Created by lxs on 2016/8/31.
 * 总结
 */
public class ZongJieEntry {

    /**
     * SheetSummay :
     * AssetList : [{"AssetID":16033,"AssetName":"as自动填单机 四通 哈哈镜","QBCode":"HF00003452"}]
     * IsExistPic : 0
     */

    private String SheetSummay;
    private int IsExistPic;
    /**
     * AssetID : 16033
     * AssetName : as自动填单机 四通 哈哈镜
     * QBCode : HF00003452
     */

    private List<AssetListBean> AssetList;

    public String getSheetSummay() {
        return SheetSummay;
    }

    public void setSheetSummay(String SheetSummay) {
        this.SheetSummay = SheetSummay;
    }

    public int getIsExistPic() {
        return IsExistPic;
    }

    public void setIsExistPic(int IsExistPic) {
        this.IsExistPic = IsExistPic;
    }

    public List<AssetListBean> getAssetList() {
        return AssetList;
    }

    public void setAssetList(List<AssetListBean> AssetList) {
        this.AssetList = AssetList;
    }

    public static class AssetListBean {
        private int AssetID;
        private String AssetName;
        private String QBCode;

        public int getAssetID() {
            return AssetID;
        }

        public void setAssetID(int AssetID) {
            this.AssetID = AssetID;
        }

        public String getAssetName() {
            return AssetName;
        }

        public void setAssetName(String AssetName) {
            this.AssetName = AssetName;
        }

        public String getQBCode() {
            return QBCode;
        }

        public void setQBCode(String QBCode) {
            this.QBCode = QBCode;
        }
    }
}
