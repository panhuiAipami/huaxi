package com.spriteapp.booklibrary.widget.readview;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;

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
    private SharedPreferences sp;
    //字体
    private Typeface typeface;
    //字体大小
    private float mFontSize = 0;
    //亮度值
    private float light = 0;
    private int bookBG;

    private Config(Context mContext) {
        this.mContext = mContext.getApplicationContext();
        sp = this.mContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
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

    public int getPageMode() {
        return SharedPreferencesUtil.getInstance().getInt(Constant.PAGE_CHANGE_STYLE);
    }


    public int getBookBgType() {
        return SharedPreferencesUtil.getInstance().getInt(com.spriteapp.booklibrary.constant.Constant.READ_PAGE_BG_COLOR, 1);
    }


    public float getFontSize() {
        int font = SharedPreferencesUtil.getInstance().getInt(com.spriteapp.booklibrary.constant.Constant.READ_TEXT_SIZE_POSITION, 16);
        return ScreenUtil.dpToPxInt(font);
    }


    /**
     * 获取夜间还是白天阅读模式,true为夜晚，false为白天
     */
    public boolean getDayOrNight() {
        return SharedPreferencesUtil.getInstance().getBoolean(Constant.IS_NIGHT_MODE, false);
    }
}
