package com.spriteapp.booklibrary.constant;

/**
 * Created by kuangxiaoguo on 2017/7/13.
 */

public class WebConstant {

    public static final String BANNER_URL = "";
    public static final String DETAIL_URL = "http://w.huaxi.net/";

    public static final String SCHEME_PROMISE = "huaxi";
    public static final String DETAIL_PROMISE = "details";
    public static final String DETAIL_ID_PROMISE = "id";
    public static final String ACTION_AUTHORITY = "action";
    public static final String OPEN_PAGE_AUTHORITY = "openpage";
    public static final String SETTING_AUTHORITY = "setting";
    public static final String ADD_SHELF_AUTHORITY = "add_shelf";
    public static final String BOOK_READ_AUTHORITY = "book_read";
    public static final String BOOK_DETAIL_AUTHORITY = "book_detail";
    public static final String BOOK_CATALOG_AUTHORITY = "book_catalog";
    public static final String PAY_AUTHORITY = "pay";
    public static final String ADD_COMMENT_AUTHORITY = "add_comment";
    public static final String LOGIN_AUTHORITY = "login";

    public static final String URL_QUERY = "url";
    public static final String BOOK_ID_QUERY = "book_id";
    public static final String CHAPTER_ID_QUERY = "chapter_id";
    public static final String PRODUCT_ID_QUERY = "product_id";
    /*
    setting	设置	huaxi://app?action=setting
add_shelf	添加到书架，会带其他参数	huaxi://app?action=add_shelf&book_id=14&chapter_id=16422
book_read	阅读书籍，会带其他参数	huaxi://app?action=book_read&book_id=14&chapter_id=16422
book_detail	书籍详细，会带其他参数	huaxi://app?action=book_detail&book_id=14
book_catalog	书籍目录，会带其他参数	huaxi://app?action=book_catalog&book_id=14
add_comment	写评论，会带其他参数	huaxi://app?action=add_comment&book_id=14&chapter_id=16422
pay	支付，会带其他参数	huaxi://app?action=pay&product_id=net.huaxi.1yuan
     */
}
