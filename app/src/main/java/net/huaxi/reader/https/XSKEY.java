package net.huaxi.reader.https;

/**
 * 网络接口KEY值.
 * 2016/1/5.
 */
public class XSKEY {
    /*章节阅读*/
    public static class READER_CHAPTER {
        public static final String KEY_LIST = "list";
        //章节内容下载地址;
        public static final String REQ_URL = "requrl";
        public static final String BOOKID = "bookid";
        public static final String CHAPTERID = "chapterid";
        public static final String CPTTEXTID = "cpttextid";
        //订阅&&阅读币不足
        public static final String INTRO = "intro";
        public static final String PRICE = "price";
        public static final String AUTO_SUB = "auto_sub";
        public static final String ORIGIN_PRICE = "origin_price";
        public static final String HAS_DISCOUNT = "HAS_DISCOUNT";
        public static final String IS_HIRE = "bp_is_hire";
    }

    /*用户信息接口*/
    public static class USER_INFO {
        public static final String KEY_INFO = "info";
        public static final String COIN = "pi_total_coins";
        public static final String EXPIRE = "pi_hire_expire";
        public static final String HIRE_EXPIRE_FLAG = "pi_hire_expire_flag";
    }

    /*用户目录*/
    public static class BOOK_CATALOG {
        public static final String CONTENTS = "list";
        public static final String LAST_UPDATE_TIME = "last_update_time";
    }


    public static final String KEY_LIST = "list";


}
