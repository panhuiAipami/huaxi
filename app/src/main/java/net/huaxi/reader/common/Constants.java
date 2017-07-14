package net.huaxi.reader.common;

import android.os.Environment;

import com.tools.commonlibs.common.CommonApp;

import java.io.File;

public class Constants {


    /****************
     * 网络参数
     ***************/
    public static final int SESSIONCONTINUEMILLIS = 30 * 1000;
    public static final int CONNECT_TIMEOUT = 5 * 1000;

    public static final String DEFAULT_USERID = "-1"; //默认登录用户ID;
    public static final int IMAGE_CACHE_SIZE = 20 * 1024 * 1024;        //图片缓存大小.
    public static final int IS_UPDATE_CHATPER = 1;
    /*阅读页面，购买页面checkbox和订阅提示间距.*/
    public static final int MARGIN_AUTO_TO_TEXT = 2;
    public static final String SERVER_ACCEPT = "application/jiuyuu.com+json+version:%s";
    /*阅读主题模式*/
    public static final int THEME_SIZE = 5; //系统主题数量.
    public static final int THEME_WHITE = 0; //白色
    public static final int THEME_PINK = 1;  //粉红
    public static final int THEME_GREEN = 2; //绿色
    public static final int THEME_YELLOW = 3; //黄褐色
    public static final int THEME_NIGHT = 4; //夜晚
    /*下载列表中规定父列表包含子列表的个数*/
    public static final int DOWNLOAD_LIST_CHILD_NUM = 20;
    /*阅读字体颜色*/
    public static final int[] READ_CONTENT_COLORS = {0xFF323232, 0xFF984A5F, 0xFF3F473A, 0xFF5E4330, 0xFF4D5C6E};
    /*阅读其他颜色(标题和底部)*/
    public static final int[] READ_OTHER_COLORS = {0xFF7F7F7F, 0xFF9A5A67, 0xFF51594B, 0xFF725547, 0xFF3F4C5D};
    /*阅读背景颜色(0表示图片展示)*/
    public static final int[] READ_BACKGROUND_COLORS = {0xFFDCDCDC, 0, 0xFFC5D5BE, 0, 0xFF111419};
    /*阅读支付按钮颜色*/
    public static final int[] READ_PAY_BUTTON_COLORS = {0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFCEAFB3};
//    public static final int[] READ_PAY_BUTTON_COLORS = {0xFF323232, 0xFF984A5F, 0xFF3F473A, 0xFF5E4330, 0xFF4D5C6E};
    /*阅读阅读币颜色*/
//    public static final int[] READ_ORDER_PRICE_COLORS = {0xFFF9494D, 0xFFF9494D, 0xFFF9494D, 0xFFF9494D, 0xFF822A32};
    public static final int[] READ_ORDER_PRICE_COLORS = {0xFF323232, 0xFF984A5F, 0xFF3F473A, 0xFF5E4330, 0xFF4D5C6E};
    /*阅读自动订阅颜色*/
    public static final int[] READ_AUTO_SUB_COLORS = {0xFFA7A7A7, 0xFFA7A7A7, 0xFFA7A7A7, 0xFFA7A7A7, 0xFF65727F};
    /*阅读页面分割线颜色*/
    public static final int[] READ_SPLIT_LINE_COLORS = {0x33000000,0x33000000,0x33000000,0x33000000,0x33FFFFFF};
    /**************
     * 网络参数--END--
     **********/


    private static final String XSREADER_FILE_ROOT_NAME = "xsreader";
    private static final String XSREADER_FILE_ROOT_IMG_NAME = "imgcache";
    private static final String XSREADER_FILE_ROOT_SPLASH_IMAGE_NAME = "splashimgcache";
    private static final String XSREADER_FILE_ROOT_DATA_NAME = "data";
    private static final String XSREADER_FILE_ROOT_BOOK_NAME = "book";
    private static final String XSREADER_FILE_ROOT_TEMP_NAME = "tmp";
    private static final String XSREADER_FILE_ROOT_MIGRATE_NAME = "migrate";
    public static int stautsHeight = 0;
    //检查更新间隔时间(1天-毫秒)
    public static long UPDATE_APK_INTERVAL = 24 * 60 * 60 * 1000;
    //BUGLY应用场景标签
    public static int BUGLY_SCENE_TAG_START = 10799;
    public static int BUGLY_SCENE_TAG_ACCOUNT = 10795;
    public static int BUGLY_SCENE_TAG_BOOKSHELF = 10794;
    public static int BUGLY_SCENE_TAG_READING = 10792;
    private static String ROOTPATH = "";

    static {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            cachePath = Environment.getExternalStorageDirectory() + File.separator + XSREADER_FILE_ROOT_NAME;
        } else {
            cachePath = CommonApp.getInstance().getCacheDir().getPath();
        }
        ROOTPATH = cachePath;
    }

    public static String XSREADER_TEMP = ROOTPATH + File.separator + XSREADER_FILE_ROOT_TEMP_NAME + File.separator;
    public static String XSREADER_IMGCACHE = ROOTPATH + File.separator + XSREADER_FILE_ROOT_IMG_NAME + File.separator;
    public static String XSREADER_DATA = ROOTPATH + File.separator + XSREADER_FILE_ROOT_DATA_NAME + File.separator;
    public static String XSREADER_BOOK = ROOTPATH + File.separator + XSREADER_FILE_ROOT_BOOK_NAME + File.separator;
    public static String XSREADER_SPLASH_IMGCACHE = ROOTPATH + File.separator + XSREADER_FILE_ROOT_SPLASH_IMAGE_NAME + File.separator;
    public static String XSREADER_MIGRATE_PATH = ROOTPATH + File.separator + XSREADER_FILE_ROOT_MIGRATE_NAME + File.separator;    //老版本迁移


}
