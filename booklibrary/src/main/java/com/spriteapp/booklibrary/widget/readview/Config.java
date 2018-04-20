package com.spriteapp.booklibrary.widget.readview;

import android.content.Context;

import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.util.ScreenUtil;
import com.spriteapp.booklibrary.util.SharedPreferencesUtil;

/**
 * Created by Administrator on 2016/7/18 0018.
 */
public class Config {
    private final static String SP_NAME = "config";
    private final static String BOOK_BG_KEY = "bookbg";
    private final static String FONT_TYPE_KEY = "fonttype";
    private final static String FONT_SIZE_KEY = "fontsize";
    private final static String NIGHT_KEY = "night";
    private final static String LIGHT_KEY = "light";
    private final static String SYSTEM_LIGHT_KEY = "systemlight";
    private final static String PAGE_MODE_KEY = "pagemode";

    public final static String FONTTYPE_DEFAULT = "";
    public final static String FONTTYPE_QIHEI = "font/qihei.ttf";
    public final static String FONTTYPE_WAWA = "font/font1.ttf";

    public final static String FONTTYPE_FZXINGHEI = "font/fzxinghei.ttf";
    public final static String FONTTYPE_FZKATONG = "font/fzkatong.ttf";
    public final static String FONTTYPE_BYSONG = "font/bysong.ttf";

    public final static int BOOK_BG_DEFAULT = 0;
    public final static int BOOK_BG_1 = 1;
    public final static int BOOK_BG_2 = 2;
    public final static int BOOK_BG_3 = 3;
    public final static int BOOK_BG_4 = 4;
    public final static int BOOK_BG_5 = 5;

    public final static int PAGE_MODE_SIMULATION = 0;
    public final static int PAGE_MODE_COVER = 1;
    public final static int PAGE_MODE_SLIDE = 2;
    public final static int PAGE_MODE_NONE = 3;

    private Context mContext;
    private static Config config;

    private Config(Context mContext) {
        this.mContext = mContext.getApplicationContext();
    }

    public static synchronized Config getInstance() {
        return config;
    }

    public static synchronized Config createConfig(Context context) {
        if (config == null) {
            config = new Config(context);
        }

        return config;
    }

    /**
     * 设置亮度
     *
     * @param brightness
     */
    public void setBrightness(float brightness) {
        SharedPreferencesUtil.getInstance().putFloat(Constant.READ_PAGE_BRIGHTNESS, brightness);
    }

    public float getBrightness() {
        return SharedPreferencesUtil.getInstance().getFloat(Constant.READ_PAGE_BRIGHTNESS, -1);
    }

    public void setSystemBrightness(boolean isSystemBrightness) {
        SharedPreferencesUtil.getInstance().putBoolean(Constant.READ_PAGE_SYSTEM_BRIGHTNESS, isSystemBrightness);
    }

    public boolean getSystemBrightness() {
        return SharedPreferencesUtil.getInstance().getBoolean(Constant.READ_PAGE_SYSTEM_BRIGHTNESS);
    }

    /**
     * 字体大小
     *
     * @return
     */
    public int getFontSize() {
        float font = SharedPreferencesUtil.getInstance().getFloat(com.spriteapp.booklibrary.constant.Constant.READ_TEXT_SIZE_POSITION, 16);
        return ScreenUtil.dpToPxInt(font);
    }

    public void setFontSize(float fontSize) {
        SharedPreferencesUtil.getInstance().putFloat(com.spriteapp.booklibrary.constant.Constant.READ_TEXT_SIZE_POSITION, fontSize);
    }

    /**
     * 字体格式
     * @return
     */
    public int getFontStyle() {
        int s = SharedPreferencesUtil.getInstance().getInt(com.spriteapp.booklibrary.constant.Constant.READ_PAGE_FONT_STYLE, 0);
        return s;
    }

    public void setFontStyle(int font_num) {
        SharedPreferencesUtil.getInstance().putInt(com.spriteapp.booklibrary.constant.Constant.READ_PAGE_FONT_STYLE, font_num);
    }

    /**
     * 阅读行间距
     * @return
     */
    public int getFontFormat() {
        int s = SharedPreferencesUtil.getInstance().getInt(com.spriteapp.booklibrary.constant.Constant.READ_PAGE_FONT_FORMAT, 1);
        return s;
    }

    public void setFontFormat(int format) {
        SharedPreferencesUtil.getInstance().putInt(com.spriteapp.booklibrary.constant.Constant.READ_PAGE_FONT_FORMAT, format);
    }

    /**
     * 阅读背景
     *
     * @return
     */
    public int getBookBgType() {
        return SharedPreferencesUtil.getInstance().getInt(com.spriteapp.booklibrary.constant.Constant.READ_PAGE_BG_COLOR, 1);
    }

    public void setBookBgType(int bookBgType) {
        SharedPreferencesUtil.getInstance().putInt(com.spriteapp.booklibrary.constant.Constant.READ_PAGE_BG_COLOR, bookBgType);
    }



    /**
     * 翻页模式(0覆盖，1仿真，2滑动)
     *
     * @return
     */
    public int getPageMode() {
        return SharedPreferencesUtil.getInstance().getInt(Constant.PAGE_CHANGE_STYLE);
    }

    public void setPageMode(int pageMode) {
        SharedPreferencesUtil.getInstance().putInt(Constant.PAGE_CHANGE_STYLE, pageMode);
    }

    /**
     * 进度显示方式
     * @return
     */
    public int getReaderProgressFormat() {
        return SharedPreferencesUtil.getInstance().getInt(Constant.READ_PAGE_PROGRESS_FORMAT);
    }

    public void setReaderProgressFormat(int progressFormat) {
        SharedPreferencesUtil.getInstance().putInt(Constant.READ_PAGE_PROGRESS_FORMAT, progressFormat);
    }


    /**
     * 获取夜间还是白天阅读模式,true为夜晚，false为白天
     */
    public boolean getDayOrNight() {
        return SharedPreferencesUtil.getInstance().getBoolean(Constant.IS_NIGHT_MODE, false);
    }
}
