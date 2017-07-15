package net.huaxi.reader.bean;

import java.io.Serializable;

/**
 * @Description: [ 一句话描述该类的功能 ]
 * @Author: [ Saud ]
 * @CreateDate: [ 16/8/5 10:25 ]
 * @UpDate: [ 16/8/5 10:25 ]
 * @Version: [ v1.0 ]
 */
public class ClassifyDataBean implements Serializable {


    private static final long serialVersionUID = 7898500217426403313L;
    /**
     * cat_mid : 1011011016161204312707114019
     * cat_name : 架空历史
     */

    private String cat_mid;
    private String cat_name;

    public String getCat_mid() {
        return cat_mid;
    }

    public void setCat_mid(String cat_mid) {
        this.cat_mid = cat_mid;
    }

    public String getCat_name() {
        return cat_name;
    }

    public void setCat_name(String cat_name) {
        this.cat_name = cat_name;
    }
}
