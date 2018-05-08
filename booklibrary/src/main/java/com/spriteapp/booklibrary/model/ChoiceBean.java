package com.spriteapp.booklibrary.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by panhui on 2018/1/26.
 */

public class ChoiceBean implements Serializable{

    /**
     * book_id : 1184
     * book_name : 情不知所起
     * book_image : https://img.huaxi.net/upload/bookimage/2017/11/20171130111634431.jpg
     * book_intro : 家族破产，她被退了婚约，几年后，却发现自己跟前未婚夫的哥哥睡在了一起；
     她以为自己再也不会踏入那些所谓名媛贵族的上流社会，却被尉少迟一把拉入；
     因为尉少迟，苏汐成了全海城女人愤恨的对象，她甘之如饴，只因这个男人深爱自己。
     当阴谋一桩一桩接踵而至，他的全心爱恋，却换来了她的慌乱逃离。
     尉少迟霸道宣言：“小汐儿，你跑的再远，我也能让你回到我的身边。”
     尉少迟，你曾许我一世诺言，别让誓言最后变成了笑言……
     * book_share_url : http://w.huaxi.net/1184
     * book_url : huaxi://app?action=openpage&url=https://s.hxdrive.net/book_detail?format=html&book_id=1184
     * book_total_score : 9
     * book_total_reads : 1
     * book_finish_flag : 0
     * book_is_vip : 3
     * book_content_byte : 235925
     * book_chapter_total : 117
     * book_keywords : ["宠文","苦恋暗","青梅竹马"]
     * book_price : 0
     * book_updatetime : 0
     * book_category : [{"class_id":"2","class_name":"总裁豪门"}]
     * author_id : 7908
     * author_name : YYL曼曼
     * author_avatar : https://img.huaxi.net/userhead/7908.jpg
     * last_update_chapter_id : 566245
     * last_update_chapter_datetime : 1516845727
     * last_update_chapter_title : 第117章 再废话滚出尉家
     * last_update_chapter_intro : 第117章 再废话滚出尉家
     * create_time : 1513235795
     * id : 80
     * widget_id : 8
     * status : 1
     * sort : 89
     * name : 用过了就想跑？没门！
     * images : https://img.hxdrive.net/uploads/2018/01/26/228d42dd4c338aa20dc014423409c72e.jpg
     * tag : null
     * link : 1184
     * intro : 因为尉少迟，苏汐成了全海城女人愤恨的对象，她甘之如饴，只因这个男人深爱自己。
     * extend : 1147
     * edit_time : 1516942260
     * book_store_id : 0
     * url : huaxi://app?action=openpage&url=https://s.hxdrive.net/book_detail?format=html&book_id=1184
     */

    private int book_id;
    private String book_name;
    private String book_image;
    private String book_intro;
    private String book_share_url;
    private String book_url;
    private int book_total_score;
    private int book_total_reads;
    private int book_finish_flag;
    private int book_is_vip;
    private int book_content_byte;
    private int book_chapter_total;
    private int book_price;
    private int book_updatetime;
    private String author_id;
    private String author_name;
    private String author_avatar;
    private int last_update_chapter_id;
    private int last_update_chapter_datetime;
    private String last_update_chapter_title;
    private String last_update_chapter_intro;
    private String create_time;
    private String id;
    private String widget_id;
    private String status;
    private String sort;
    private String name;
    private String images;
    private Object tag;
    private String link;
    private String intro;
    private int extend;
    private String edit_time;
    private String book_store_id;
    private String url;
    private List<String> book_keywords;
    private List<BookCategoryBean> book_category;

    public int getBook_id() {
        return book_id;
    }

    public void setBook_id(int book_id) {
        this.book_id = book_id;
    }

    public String getBook_name() {
        return book_name;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }

    public String getBook_image() {
        return book_image;
    }

    public void setBook_image(String book_image) {
        this.book_image = book_image;
    }

    public String getBook_intro() {
        return book_intro;
    }

    public void setBook_intro(String book_intro) {
        this.book_intro = book_intro;
    }

    public String getBook_share_url() {
        return book_share_url;
    }

    public void setBook_share_url(String book_share_url) {
        this.book_share_url = book_share_url;
    }

    public String getBook_url() {
        return book_url;
    }

    public void setBook_url(String book_url) {
        this.book_url = book_url;
    }

    public int getBook_total_score() {
        return book_total_score;
    }

    public void setBook_total_score(int book_total_score) {
        this.book_total_score = book_total_score;
    }

    public int getBook_total_reads() {
        return book_total_reads;
    }

    public void setBook_total_reads(int book_total_reads) {
        this.book_total_reads = book_total_reads;
    }

    public int getBook_finish_flag() {
        return book_finish_flag;
    }

    public void setBook_finish_flag(int book_finish_flag) {
        this.book_finish_flag = book_finish_flag;
    }

    public int getBook_is_vip() {
        return book_is_vip;
    }

    public void setBook_is_vip(int book_is_vip) {
        this.book_is_vip = book_is_vip;
    }

    public int getBook_content_byte() {
        return book_content_byte;
    }

    public void setBook_content_byte(int book_content_byte) {
        this.book_content_byte = book_content_byte;
    }

    public int getBook_chapter_total() {
        return book_chapter_total;
    }

    public void setBook_chapter_total(int book_chapter_total) {
        this.book_chapter_total = book_chapter_total;
    }

    public int getBook_price() {
        return book_price;
    }

    public void setBook_price(int book_price) {
        this.book_price = book_price;
    }

    public int getBook_updatetime() {
        return book_updatetime;
    }

    public void setBook_updatetime(int book_updatetime) {
        this.book_updatetime = book_updatetime;
    }

    public String getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(String author_id) {
        this.author_id = author_id;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getAuthor_avatar() {
        return author_avatar;
    }

    public void setAuthor_avatar(String author_avatar) {
        this.author_avatar = author_avatar;
    }

    public int getLast_update_chapter_id() {
        return last_update_chapter_id;
    }

    public void setLast_update_chapter_id(int last_update_chapter_id) {
        this.last_update_chapter_id = last_update_chapter_id;
    }

    public int getLast_update_chapter_datetime() {
        return last_update_chapter_datetime;
    }

    public void setLast_update_chapter_datetime(int last_update_chapter_datetime) {
        this.last_update_chapter_datetime = last_update_chapter_datetime;
    }

    public String getLast_update_chapter_title() {
        return last_update_chapter_title;
    }

    public void setLast_update_chapter_title(String last_update_chapter_title) {
        this.last_update_chapter_title = last_update_chapter_title;
    }

    public String getLast_update_chapter_intro() {
        return last_update_chapter_intro;
    }

    public void setLast_update_chapter_intro(String last_update_chapter_intro) {
        this.last_update_chapter_intro = last_update_chapter_intro;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWidget_id() {
        return widget_id;
    }

    public void setWidget_id(String widget_id) {
        this.widget_id = widget_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public int getExtend() {
        return extend;
    }

    public void setExtend(int extend) {
        this.extend = extend;
    }

    public String getEdit_time() {
        return edit_time;
    }

    public void setEdit_time(String edit_time) {
        this.edit_time = edit_time;
    }

    public String getBook_store_id() {
        return book_store_id;
    }

    public void setBook_store_id(String book_store_id) {
        this.book_store_id = book_store_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getBook_keywords() {
        return book_keywords;
    }

    public void setBook_keywords(List<String> book_keywords) {
        this.book_keywords = book_keywords;
    }

    public List<BookCategoryBean> getBook_category() {
        return book_category;
    }

    public void setBook_category(List<BookCategoryBean> book_category) {
        this.book_category = book_category;
    }

    public static class BookCategoryBean implements Serializable{
        /**
         * class_id : 2
         * class_name : 总裁豪门
         */

        private String class_id;
        private String class_name;

        public String getClass_id() {
            return class_id;
        }

        public void setClass_id(String class_id) {
            this.class_id = class_id;
        }

        public String getClass_name() {
            return class_name;
        }

        public void setClass_name(String class_name) {
            this.class_name = class_name;
        }
    }
}
