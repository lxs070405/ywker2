package com.y.w.ywker.entry;

/**
 * Created by lxs on 2016/8/5.
 */
public class PicEntry {


    /**
     * picHref : http://192.168.1.149:805/uploadfile/郑州浩方科贸有限公司/IMG_20151003_1254481470362228579.jpg
     */

    private String picHref;
    private String FileName;
    private String Base64;
    public String getFileName(){
        return FileName;
    }
    public String getPicHref() {
        return picHref;
    }
    public String getBase64(){
        return Base64;
    }
    public void setFileName(){this.FileName = FileName;}
    public void setPicHref(String picHref) {
        this.picHref = picHref;
    }
    public void setBase64(){this.Base64 = Base64;}
}
