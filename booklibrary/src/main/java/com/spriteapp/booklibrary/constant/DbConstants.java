package com.spriteapp.booklibrary.constant;

/**
 * Created by kuangxiaoguo on 2017/7/8.
 */

public class DbConstants {
    public static final String BOOK_ID = "book_id";
    public static final String CHAPTER_ID = "chapter_id";
    public static final String BOOK_ADD_SHELF = "book_add_shelf";

    public static final String RECENT_BOOK_TABLE_NAME = "recent_book_table";

    /**
     * 书架表及字段
     */
    public static final String BOOK_TABLE_NAME = "book_table";
    public static final String BOOK_NAME = "book_name";
    public static final String LAST_UPDATE_CHAPTER_TITLE = "last_update_chapter_title";
    public static final String BOOK_IMAGE = "book_image";
    public static final String LAST_CHAPTER_ID = "last_chapter_id";
    public static final String TOTAL_CHAPTER = "total_chapter";
    public static final String LAST_READ_TIME = "last_read_time";
    public static final String LAST_CHAPTER_INDEX = "last_chapter_index";

    //用于是否请求书籍详情接口，只有3小时外才更新
    public static final String LAST_UPDATE_BOOK_DATETIME = "last_update_book_datetime";
    public static final String LAST_UPDATE_CHAPTER_DATETIME = "last_update_chapter_datetime";
    public static final String UPDATE_TIME = "update_time";
    public static final String BOOK_FINISH_FLAG = "book_finish_flag";
    public static final String BOOK_IS_VIP = "book_is_vip";
    public static final String BOOK_IS_RECOMMEND_DATA = "book_is_recommend_data";
    public static final String BOOK_INTRODUCTION = "book_introduction";
    public static final String BOOK_SHARE_URL = "book_share_url";
    //增加  作者名字和头像
    public static final String AUTHOR_AVATAR = "author_avatar";
    public static final String AUTHOR_NAME = "author_name";
    /*
    private int book_id;
    private String chapter_title;
    private int chapter_price;
    private int chapter_is_vip;
    private long chapter_content_byte;
     */
    /**
     * 书的内容表及字段
     */
    public static final String CONTENT_TABLE_NAME = "content_table";
    public static final String AUTO_SUB = "auto_sub";
    public static final String CHAPTER_INTRO = "chapter_intro";
    public static final String CHAPTER_CONTENT_KEY = "chapter_content_key";
    public static final String CHAPTER_CONTENT = "chapter_content";
    public static final String CHAPTER_NEED_BUY = "chapter_need_buy";
    public static final String CHAPTER_PAY_TYPE = "chapter_pay_type";
    public static final String CHAPTER_ISAES = "isAES";

    /**
     * 书章节表及字段
     */
    public static final String CHAPTER_TABLE_NAME = "chapter_table";
    public static final String CHAPTER_TITLE = "chapter_title";
    public static final String CHAPTER_ORDER = "chapter_order";
    public static final String CHAPTER_CONTENT_BYTE = "chapter_content_byte";
    public static final String CHAPTER_IS_SUB = "chapter_is_sub";
    public static final String CHAPTER_PRICE = "chapter_price";
    public static final String CHAPTER_IS_VIP = "chapter_is_vip";
    public static final String CHAPTER_READ_STATE = "chapter_read_state";
    public static final String CHAPTER_IS_DOWN_LOAD = "chapter_is_down_load";
}
