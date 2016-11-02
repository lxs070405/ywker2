package com.y.w.ywker.entry;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by lxs on 16/5/6.
 */
public class SerializableMap implements Serializable {
    private Map<String,String> map;
    public Map<String,String> getMap()
    {
        return map;
    }
    public void setMap(Map<String,String> map)
    {
        this.map=map;
    }
}
