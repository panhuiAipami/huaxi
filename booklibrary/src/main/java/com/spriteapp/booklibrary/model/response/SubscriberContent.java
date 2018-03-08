package com.spriteapp.booklibrary.model.response;

import com.spriteapp.booklibrary.util.CXAESUtil;
import com.spriteapp.booklibrary.util.PreferenceHelper;

import java.io.Serializable;

/**
 * Created by kuangxiaoguo on 2017/7/7.
 */

public class SubscriberContent implements Serializable {

    public int SQLiteId;
    private int auto_sub;
    private int book_id;
    private String chapter_title;
    private String chapter_intro;
    private int chapter_id;
    private int chapter_price;
    private int chapter_is_vip;
    private long chapter_content_byte;
    private String chapter_content_key;
    public String chapter_content;
    //付费章节订阅后使用的花贝，已订阅过或免费的章节不会出现此字段
    private int used_real_point;
    //付费章节订阅后使用的花瓣，已订阅过或免费的章节不会出现此字段
    private int used_false_point;
    private int chapter_need_buy;
    //书籍购买时是直接扣费还是跳转支付页面
    private int chapter_pay_type;
    private int isAES = 0;

    public boolean getIsAES() {
        return isAES == 1;
    }

    public void setIsAES(int isAES) {
        this.isAES = isAES;
    }

    public int getAuto_sub() {
        return auto_sub;
    }

    public void setAuto_sub(int auto_sub) {
        this.auto_sub = auto_sub;
    }

    public int getBook_id() {
        return book_id;
    }

    public void setBook_id(int book_id) {
        this.book_id = book_id;
    }

    public String getChapter_title() {
        return chapter_title;
    }

    public void setChapter_title(String chapter_title) {
        this.chapter_title = chapter_title;
    }

    public String getChapter_intro() {
        return chapter_intro;
    }

    public void setChapter_intro(String chapter_intro) {
        this.chapter_intro = chapter_intro;
    }

    public int getChapter_id() {
        return chapter_id;
    }

    public void setChapter_id(int chapter_id) {
        this.chapter_id = chapter_id;
    }

    public int getChapter_price() {
        return chapter_price;
    }

    public void setChapter_price(int chapter_price) {
        this.chapter_price = chapter_price;
    }

    public int getChapter_is_vip() {
        return chapter_is_vip;
    }

    public void setChapter_is_vip(int chapter_is_vip) {
        this.chapter_is_vip = chapter_is_vip;
    }

    public long getChapter_content_byte() {
        return chapter_content_byte;
    }

    public void setChapter_content_byte(long chapter_content_byte) {
        this.chapter_content_byte = chapter_content_byte;
    }

    public String getChapter_content_key() {
        return chapter_content_key;
    }

    public void setChapter_content_key(String chapter_content_key) {
        this.chapter_content_key = chapter_content_key;
    }

    public String getChapter_content() {
        if (getIsAES()) {
            try {
                String text = CXAESUtil.decrypt("" + PreferenceHelper.getLong(PreferenceHelper.AES_KEY, 0l), chapter_content);
                return text;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return chapter_content;
    }

    public void setChapter_content(String chapter_content) {
        this.chapter_content = chapter_content;
    }

    public int getUsed_real_point() {
        return used_real_point;
    }

    public void setUsed_real_point(int used_real_point) {
        this.used_real_point = used_real_point;
    }

    public int getUsed_false_point() {
        return used_false_point;
    }

    public void setUsed_false_point(int used_false_point) {
        this.used_false_point = used_false_point;
    }

    public int getChapter_need_buy() {
        return chapter_need_buy;
    }

    public void setChapter_need_buy(int chapter_need_buy) {
        this.chapter_need_buy = chapter_need_buy;
    }

    public int getChapter_pay_type() {
        return chapter_pay_type;
    }

    public void setChapter_pay_type(int chapter_pay_type) {
        this.chapter_pay_type = chapter_pay_type;
    }

    @Override
    public String toString() {
        return "SubscriberContent{" +
                "SQLiteId=" + SQLiteId +
                ", auto_sub=" + auto_sub +
                ", book_id=" + book_id +
                ", chapter_title='" + chapter_title + '\'' +
                ", chapter_intro='" + chapter_intro + '\'' +
                ", chapter_id=" + chapter_id +
                ", chapter_price=" + chapter_price +
                ", chapter_is_vip=" + chapter_is_vip +
                ", chapter_content_byte=" + chapter_content_byte +
                ", chapter_content_key='" + chapter_content_key + '\'' +
                ", chapter_content='" + chapter_content + '\'' +
                ", used_real_point=" + used_real_point +
                ", used_false_point=" + used_false_point +
                ", chapter_need_buy=" + chapter_need_buy +
                ", chapter_pay_type=" + chapter_pay_type +
                '}';
    }
}
