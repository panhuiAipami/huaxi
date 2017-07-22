package net.huaxi.reader.common;

public class URLConstants {
    public static  String url_base = " https://api.hxdrive.net";

    public static final String PAY_URL = "https://pay.xs.cn";
    public static final String REDIRECT_URL = "http://rd.xs.cn";
    //数据中心根路径
    private static final String DATA_ROOT_URL = "http://data.xs.cn";
    //****************************
    // 网络h5充值页面
     public static final String H5PAGE_CHARGE = url_base+"/pay/index";
    // 网络h5会员页面
//    public static final String H5PAGE_VIP = PAY_URL + "/view/product/list/hire/?a=1";
    public static final String H5PAGE_VIP = "http:\\/\\/rd.xs.cn/1005?1=1";

    // 精选页面
    public static final String H5PAGE_SELECTION = url_base+"/book/store?1=1";
    //周刊页面
    public static final String H5PAGE_WEEKLY = url_base+"/book/weekly?1=1";


    //免费
    public static final String H5PAGE_FREE = "http:\\/\\/rd.xs.cn/1201?1=1";

    /*==================================================================*/
    //会员
    public static final String H5PAGE_MEMBER = "http:\\/\\/rd.xs.cn/1207?1=1";
    //新书
    public static final String H5PAGE_NEW_BOOK = "http:\\/\\/rd.xs.cn/1206?1=1";
    //排行
    public static final String H5PAGE_RANKING = "http:\\/\\/rd.xs.cn/1208?1=1";
    //分类
    public static final String H5PAGE_GAMBIT = "http:\\/\\/rd.xs.cn/1210?1=1";
    //分类本地接口。
    public static final String NATIVE_CATALOG_URL = DATA_ROOT_URL + "/api/catelistone?1=1";
    //帮助页面
    public static final String H5PAGE_HELP = "http:\\/\\/rd.xs.cn/1007?1=1";
    public static final String GET_USER_HEAD_IMAGE = "http://res-image.chumang.wang/%s.jpg";
    //调用微信支付接口
    public static final String PAY_WXPAY = PAY_URL + "/api/pay/wechat/newpay";
   // public static final String PAY_ALIPAY = PAY_URL + "/api/pay/alipay/newpay";

    public static final String PAY_ALIPAY = url_base+"/recharge/alipay";
    public static final String PAY_QQ =  PAY_URL + "/api/pay/qqwallet/newpay";
    /*==================================================================*/
    //充值记录 get
     //public static final String PAY_ORDER_LIST = PAY_URL + "/api/order/list?page=%d&pagenum=%d";
     public static final String PAY_ORDER_LIST = url_base + "/recharge/log?page=%d&pagenum=%d";
    //获取支付接口
    public static final String PAY_INFO = url_base+"/user/coins";

    //消费记录 get
    public static final String PAY_CONSUME_LSIT = url_base + "/consume/log?page=%d&pagenum=%d";

    //搜索接口
    public static final String SEARCH_BOOK_URL = url_base+"/book/search?searchword=%s&offset=%d&limit=%d";
    //public static final String SEARCH_ABOUT_RECOMMED_BOOK_URL = "http://192.168.10.5/views/book/rcm?block=%s";

    public static final String SEARCH_ABOUT_RECOMMED_BOOK_URL = url_base+"/book/rcm?block=%s";
    //分享接口
    public static final String SHARE_URL = REDIRECT_URL + "/1006?bookid=%s" + CommonUtils.getPublicGetArgs();
    //App检查更新
     public static final String APP_CHECK_UPDATE_URL = "http://api.hxdrive.net/api/checkver";
//    public static final String APP_CHECK_UPDATE_URL = url_base+"api/checkver";
//http://api.hxdrive.net/api/checkver

    //   11位数字      是      13810669999  验证码类型    u_usetype     数字        是      1-注册 2-激活 3-重置 4-登录
    //数据评论接口
    public static final String APP_BOOK_COMMENT_URL = url_base+"/book/comment?bk_mid=%s&p_size=%d&p_num=%d";
    public static final String APP_SEND_COMMENT_URL = "http://comment.xs.cn/api/addcomment";
    //闪屏接口
    public static final String APP_SPLASH_URL = "http://cc.service.xs.cn/api/spl/info?1=1";
    /****************************
     * 根目录地址(除数据中心，其他都用HTTPS方式请求)
     ******************************/
    //账户中心
    private static final String ACCOUNT_ROOT_URL = "https://account.xs.cn";
    public static final String PHONE_LOGIN = ACCOUNT_ROOT_URL + "/api/login";//登录接口 post请求
    public static final String QQ_LOGIN = ACCOUNT_ROOT_URL + "/api/qq_app_login";//qq登录
    //public static final String WX_LOGIN = ACCOUNT_ROOT_URL + "/api/wechat_app_login";//微信登录
    public static final String WX_LOGIN = url_base+"/login/wechat";//微信登录


// public static final String USER_DETAIL = ACCOUNT_ROOT_URL + "/api/user";//获取用户资料
   public static final String USER_DETAIL = url_base+"/user/info";


    public static final String USER_REGISTER = ACCOUNT_ROOT_URL + "/api/register";//手机用户注册 post
    //验证码发送 post请求
    public static final String SEND_VERIFICATION_CODE = ACCOUNT_ROOT_URL + "/api/mobileverification";//验证码发送 post请求  手机号       u_name
    public static final String RESET_PASSWORD = ACCOUNT_ROOT_URL + "/api/password";//密码重置
    //public static final String PHONE_LOGOUT = ACCOUNT_ROOT_URL + "/api/logout";//登出 post请求
    public static final String PHONE_LOGOUT = url_base+"/user/logout";
    public static final String GET_VERIFICATION_PHONE = ACCOUNT_ROOT_URL + "/api/captcha";//获取图片验证码 post请求

    //public static final String UPDATE_USER = ACCOUNT_ROOT_URL + "/api/user";//修改用户信息
    public static final String UPDATE_USER = url_base+"/user/info";//修改用户信息

    public static final String BIND_USER = ACCOUNT_ROOT_URL + "/api/bind";//绑定手机号
    public static final String UPLOAD_IMAGE = url_base + "/user/info";//上传头像 表单提交

    //章节阅读[GET/POST]
   // public static final String READPAGE_READ_CHAPTER_POST = DATA_ROOT_URL + "/reader/chapter";
    public static final String READPAGE_READ_CHAPTER_POST = url_base+"/chapter/content";
    //public static final String READPAGE_READ_CHAPTER_POST= eric_url_base+"/book/loadContent";
    //public static final String READPAGE_READ_CHAPTER = READPAGE_READ_CHAPTER_POST + "?bookid=%s&chapterid=%s";
    //public static final String READPAGE_READ_CHAPTER =  eric_url_base+"/book/loadContent?bookid=%s&chapterid=%s";
    public static final String READPAGE_READ_CHAPTER = url_base+"/chapter/content?bookid=%s&chapterid=%s";
    /*章节阅读统计接口*/
    public static final String READPAGE_ANALYSIS_COUNT_URL = DATA_ROOT_URL + "/anay/insert?bookid=%s&cptmid=%s";
    public static final String RECOMMEND_URL = DATA_ROOT_URL + "/api/rcmpublic?block=bookshelves?" + CommonUtils.getPublicGetArgs();
    //未登录获取书籍目录
    // public static final String GET_BOOKCONTENT_CATALOGUE = DATA_ROOT_URL + "/book/contents?bookid=%s&last_update_time=%s";
     public static final String GET_BOOKCONTENT_CATALOGUE = url_base+"/book/catalog?bookid=%s";
    //登录获取书籍目录
    //public static final String GET_BOOKCONTENT_DOWNLOAD = DATA_ROOT_URL + "/reader/contents?bookid=%s&last_update_time=%s";
     public static final String GET_BOOKCONTENT_DOWNLOAD =  url_base+"/book/catalog?bookid=%s";
     //public static final String GET_BOOKDETAIL = DATA_ROOT_URL + "/book/arrinfo?bookid=%s";

    public static final String GET_BOOKDETAIL = url_base+"/book/info?bookid=%s";
    public static final String GET_CAT_LIST = DATA_ROOT_URL + "/book/catlist?bookMid=%s&book_num=%s";//获取同类作品
    public static final String GET_AUTHOR_OTHER = DATA_ROOT_URL + "/api/books/aumid";
    //下载
    public static final String POST_DOWNLOAD_CHAPTER = "http://192.168.10.8/v1/book/cptloadlist";//批量下载
    public static final String POST_DOWNLOAD_CHAPTER_TEXT = DATA_ROOT_URL + "/reader/chaptertextsub"; //批量下载
    //获取推荐书架信息请求[GET]
    public static final String RECOMMEND_BOOK_URL = DATA_ROOT_URL + "/views/book/rcm?block=%s";
    // public static final String APP_SEARCH_HOTKEY = DATA_ROOT_URL + "/api/rcmpublic?block=hotsearch";
    public static final String APP_SEARCH_HOTKEY = "http://192.168.10.5/v1/book/hotkey?block=hotsearch";


    //用户中心
    private static final String USER_URL = "http://user.xs.cn";
    public static final String BOOKSHELF_BOOKS = USER_URL + "/api/bookrack?u_lut=%s";//获取书架列表
    //public static final String BOOKSHELF_BOOKS = url_base+"/book/shelf?u_lut=%s";//获取书架列表
    //添加书籍[POST]
    //public sta`tic final String BOOKSHELF_DEL_ADD_BOOK = USER_URL + "/api/bookrack";  //书架单本书操作

    public static final String BOOKSHELF_DEL_ADD_BOOK = url_base+ "/book/shelf";  //书架单本书操作
    public static final String BOOKSHELF_MULTI_OPERATE_BOOK_URL = url_base + "/api/batchbookrack";   //书架多本书操作.
    //自动订阅书籍接口(GET/POST)
   // public static final String USER_AUTO_SUB_URL = USER_URL + "/api/bookautoorder";
    public static final String USER_AUTO_SUB_URL = url_base + "/chapter/autosub";
    private static final String IMAGE_SRC = "http://res-image.chumang.wang";
    //用户迁移
    public static final String MIGRATE_TODO = "http://nirvana.xs.cn/api/nirvana/startnvn?nvncode=%s";
    //分类子页面
    public static final String CLASSIFY_BOOK_LIST = "http://data.xs.cn/api/book/list" +
            "?cat_mid=%s" +
            "&cat_pmid=%s" +
            "&offset=%d" +
            "&limit=%d";
    //订阅记录
    //public static final String SUB_SCRIBE_URL = DATA_ROOT_URL + "/api/chapter/sublist?u_fresh_time=%d&p_num=%d&p_size=%s";
     public static final String SUB_SCRIBE_URL = url_base+"/user/sub?u_fresh_time=%d&p_num=%d&p_size=%s";

    //检测版本更新的接口
//    public static final String APP_CHECK_UPDATE_URL = "http://update.service.xs.cn/check/?1=1";
}
