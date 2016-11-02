package com.y.w.ywker.entry;

/**
 * Created by lxs on 16/5/4.
 */
public class DevicesInfoCommEntry {
    /**
     * ID : 1
     * Name : 测试的型号
     * Type : Model
     */

    private int ID = 0;
    private String Name = "";
    private String Type = "";

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getType() {
        return Type;
    }

    public void setType(String Type) {
        this.Type = Type;
    }
}
