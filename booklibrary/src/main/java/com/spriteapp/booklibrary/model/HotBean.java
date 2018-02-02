package com.spriteapp.booklibrary.model;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by Administrator on 2018/2/1.
 */

public class HotBean implements Serializable {
    private Map<String,String> hot;

    public Map<String, String> getHot() {
        return hot;
    }

    public void setHot(Map<String, String> hot) {
        this.hot = hot;
    }
}
