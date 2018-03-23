package com.spriteapp.booklibrary.constant;

import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.util.FileUtils;

/**
 * Created by kuangxiaoguo on 2017/7/7.
 */

public class Constant {
    /**
     * 首页书籍列表缓存文件名fragment
     */
    public final static String PathTitle = "path_title";
    public final static String NAVIGATION = "navigation";
    public final static String START_PAGE = "start_page";
    public final static String START_PAGE_URL = "start_page_url";
    public final static String HOT_LIST = "hot_list";


    private static final boolean ISONLINE = true;

    public static final String BASE_URL = ISONLINE ? "https://s.hxdrive.net/" : "http://wuyang.api.huayux.com/api/";
    public static final String IMG_URL = "https:\\/\\/img.hxdrive.net";
    public static final String BOOK_STORE_URL = BASE_URL + "book_store";
    public static final String BOOK_ME_URL = BASE_URL + "user_index";
    public static final String H5_PAY_URL = BASE_URL + "user_recharge";
    public static final String BOOK_WEEKLY_URL = BASE_URL + "book_weekly";
    public static final String BOOK_SEARCH_URL = BASE_URL + "book_search";
    //签到
    public static final String CHECK_IN_URL = BASE_URL + "user_signin";//签到

    public static final String VERSION = "2.5.2";
    public static final String USER_AGENT_KEY = "User-Agent";

    public static final String JSON_TYPE = "json";

    public static String PATH_DATA = FileUtils.createRootPath(AppUtil.getAppContext()) + "HuaXi/cache";

    public static String PATH_TXT = PATH_DATA + "/book/";
    public static String PATH_TITLE = PATH_DATA + "/title/";

    public static final String IS_NIGHT_MODE = "hua_xi_is_night_mode";
    public static final String READ_TEXT_SIZE_POSITION = "hua_xi_read_text_size_position";
    public static final String IS_BOOK_AUTO_SUB = "hua_xi_book_auto_sub";
    public static final String PAGE_CHANGE_STYLE = "hua_xi_page_change_style";
    //阅读页亮度
    public static final String READ_PAGE_BRIGHTNESS = "read_page_brightness";
    //阅读页字体格式
    public static final String READ_PAGE_FONT_STYLE = "read_page_font_style";
    //阅读页字体间距排版
    public static final String READ_PAGE_FONT_FORMAT = "read_page_font_format";
    //阅读页背景
    public static final String READ_PAGE_BG_COLOR = "read_page_bg_color";
    //数值单位（h）
    public static final int UPDATE_BOOK_DETAIL_INTERVAL = 3;
    public static final int UPDATE_CHAPTER_INTERVAL = 6;

    public static final String HAS_SHOW_AUTO_SUB_PROMPT_DIALOG = "hua_xi_has_show_auto_buy_prompt_dialog";

    //是否初始化书架fragment
    public static final String HAS_INIT_BOOK_SHELF = "hua_xi_has_init_book_shelf";
    /**
     * 微信登录
     */
    public static final String USER_WELOGIN = BASE_URL + "/user/wxlogin";
    /**
     * 充值,消费,奖励记录
     */
    public static final String USER_RECHARGE_LOG = BASE_URL + "user_rechargelog";
    public static final String USER_CONSUME_LOG = BASE_URL + "user_consumelog";
    public static final String USER_GIVE_LOG = BASE_URL + "user_givelog";
    public static final String BOOK_DETAIL = BASE_URL + "book_detail?format=html&book_id=";
}
