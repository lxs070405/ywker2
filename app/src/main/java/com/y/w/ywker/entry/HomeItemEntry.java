package com.y.w.ywker.entry;

/**
 * Created by lxs on 16/4/19.
 */
public class HomeItemEntry {

    /**
     * NoAccepted : 1
     * MyNoFinish : 1
     * TeamNoFinish : 1
     * AllNoFinish : 2
     * MyFinish : 2
     * All : 2
     * Urgent : 2
     */

    private String NoAccepted = "0";
    private String MyNoFinish = "0";
    private String TeamNoFinish  = "0";
    private String AllNoFinish = "0";
    private String MyFinish = "0";
    private String All = "0";
    private String Urgent = "0";

    public String getAll() {
        return All;
    }

    public void setAll(String all) {
        All = all;
    }
}
