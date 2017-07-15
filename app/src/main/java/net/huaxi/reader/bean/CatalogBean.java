package net.huaxi.reader.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Map;

/**
 * function:    书城主页分类基类
 * author:      ryantao
 * create:      16/7/19
 * modtime:     16/7/19
 */
public class CatalogBean implements Serializable{

    private static final long serialVersionUID = 8889273224063768452L;

    private String id;                  //分类编号

    @SerializedName("cat_name")
    private String name;                //分类名称
    @SerializedName("book_num")
    private String book_num;            //书籍总数
    @SerializedName("cat_image")
    private String imageUrl;            //分类icon
    @SerializedName("sub_cat")
    Map<String,String> subclass;    //子分类

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBook_num() {
        return book_num;
    }

    public void setBook_num(String book_num) {
        this.book_num = book_num;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Map<String, String> getSubclass() {
        return subclass;
    }

    public void setSubclass(Map<String, String> subclass) {
        this.subclass = subclass;
    }

}
