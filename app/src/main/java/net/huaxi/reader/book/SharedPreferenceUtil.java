package net.huaxi.reader.book;

import android.content.Context;
import android.content.SharedPreferences;

import net.huaxi.reader.common.Constants;

/**
 * 保存阅读参数配置.
 */
public class SharedPreferenceUtil {

    /**
     * 保存屏幕亮度
     */
    private static final String SP_SCREEN_BRIGHTNESS = "sp_screen_brightness";
    /**
     * 设置横竖屏
     */
    private static final String SP_SCREEN_PORTRAIT = "screenOrientation_portrait";
    /**
     * 默认竖屏
     */
    private static final boolean DEFAULT_SCREEN_PORTRAIT = true;
    /**
     * 阅读页设置相关参数
     */
    private static final String SP_SP_NAME_SETTING = "booksettings";
    /**
     * 阅读页设置相关参数
     */
    private static final String SP_SAVE_TEXT = "app_string";
    /**
     * 设置全屏
     */
    private static final String SP_SETTING_IS_FULL_SCREEN = "isfullscreen";
    /**
     * 音量翻页键
     */
    private static final String SP_SETTING_IS_VOLUME_ENABLED = "isvolumeenabled";
    /**
     * 夜间模式
     */
    private static final String SP_SETTING_IS_NIGHT_MODE = "isnightmode";
    /**
     * 点击两侧翻页模式
     */
    private static final String SP_SETTING_IS_CLICKSIDE_TURNPAGEMODE = "clickSideturnpage";
    /**
     * 夜间模式不再提醒
     */
    private static final String SP_SETTING_NIGHT_MODE_NOWARN = "nightmodenowarn";
    /**
     * 阅读页屏幕常亮时间
     */
    private static final String SP_SETTING_KEEP_SCREEN_TIME = "keepscreentime";
    /**
     * 主题模式
     */
    private static final String SP_SETTING_THEME = "ptheme";
    /**
     * 上次的主题
     */
    private static final String SP_SETTING_LAST_THEME = "pthemelast";
    /**
     * 阅读页帮助引导图
     */
    private static final String SP_SETTING_HELP_SHOW = "helpshow";
    /**
     * 屏幕亮度
     */
    private static final String SP_SETTING_SCREEN_LIGHT = "screenlight";
    /**
     * 系统按钮是否被点击
     */
    private static final String SP_SETTING_SCREEN_SYSTEM_BUTTON = "clickSysbtn";
    /**
     * 阅读页翻页模式
     */
    private static final String SP_SETTING_PAGE_TURN_MODE = "pageturnmode";
    /**
     * 默认全屏
     */
    private static final boolean DEFAULT_FULLSCREEN = true;
    /**
     * 音量键翻页默认值
     */
    private static final boolean DEFAULT_VOLUME_ENABLED = true;
    /**
     * 点击页面两侧翻页默认值
     */
    private static final boolean DEFAULT_SIDE_ENABLED = false;
    /** 默认行间距 (此值改变时也要重新resolve，以便重新计算少于一屏的章节) */
    // private static final int DEFAULT_LINESPACE = 30;
    /**
     * 默认夜间模式对话框不再提示状态
     */
    private static final boolean DEFAULT_NIGHT_MODE_NOWARN = false;
    /**
     * 默认阅读页屏幕亮时间
     */
    private static final int DEFAULT_KEEP_SCREEN_TIME = 5 * 60 * 1000;
    /**
     * 默认阅读页屏幕亮时间
     */
    private static final int SCREEN_BRIGHTNESS = -1;
    /**
     * 默认主题模式
     */
    private static final int DEFAULT_THEME = Constants.THEME_YELLOW;
    /**
     * 默认屏幕亮度
     */
    private static final int DEFAULT_LIGHT = 60;
    /**
     * 阅读页默认翻页模式
     */
    private static final int DEFAULT_PAGE_TURN_MODE = 1;
    /**
     * 字档
     */
    private static final String SP_SETTING_SIZEPOSITION = "sizeposition";
    /**
     * 风格
     */
    private static final String SP_SETTING_STYLE = "style";
    /**
     *  设置正文页字体大小
     */
    private static final String SP_SETTING_TEXTSIZE = "textsize";
    /**
     *  设置正文页标题字体大小
     */
    private static final String SP_SETTING_TITLE_TEXTSIZE = "texttitlesize";
    /**
     *  标题与正文间距
     */
    private static final String SP_SETTING_TITLE_SPACE = "texttitlespace";

    // ============
    /**
     *  设置行间距
     */
    private static final String SP_SETTING_LINESPACE = "linespace";
    /**
     *  设置段间距
     */
    private static final String SP_SETTING_PARAGRAPHSPACE = "paragraphspace";
    /*
     *  点击左侧，向后翻页.
     */
    private static final String SP_LEFT_CLICK_TO_NEXT = "left_click_to_next";
    /**
     *
     */
    private static final String SP_SETTING_LINE_SPACE_STYLE = "line_space_style";
    private static SharedPreferenceUtil mSharedPreferenceUtil = null;
    /**
     * 默认是否是夜间模式
     */
    private static boolean DEFAULT_IS_NIGHTMODE = false;
    private static SharedPreferences mSharedPreferences;

    private SharedPreferenceUtil() {

    }

    public static SharedPreferenceUtil getInstanceSharedPreferenceUtil() {
        if (mSharedPreferenceUtil == null) {
            synchronized (SharedPreferenceUtil.class) {
                if (mSharedPreferenceUtil == null) {
                    mSharedPreferenceUtil = new SharedPreferenceUtil();
                }
            }
        }
        return mSharedPreferenceUtil;
    }

    /**
     * 获取阅读页设置SharedPreferences对象
     *
     * @param context
     * @return
     */
    public static SharedPreferences getSettingSP(Context context) {
        return context.getSharedPreferences(SP_SP_NAME_SETTING, Context.MODE_PRIVATE);
    }

    // =========================================================================================

    /**
     * 获取阅读页设置SP Editor对象
     *
     * @param sp
     * @return
     */
    public static SharedPreferences.Editor getSettingEditor(SharedPreferences sp) {
        return sp.edit();
    }

    /**
     * 设置int类型属性
     *
     * @param editor
     * @param key
     * @param value
     * @remark 字体大小、行间距
     */
    public static void setIntAttribute(SharedPreferences.Editor editor, String key, int value) {
        editor.putInt(key, value).commit();
    }

    /**
     * 设置boolean类型属性
     *
     * @param editor
     * @param key
     * @param value
     * @remark 是否全屏
     */
    public static void setBooleanAttribute(SharedPreferences.Editor editor, String key, boolean value) {
        editor.putBoolean(key, value).commit();
    }

    /**
     * 获取int类型属性
     *
     * @param sp
     * @param key
     * @param defValue
     * @return
     */
    public static int getIntAttribute(SharedPreferences sp, String key, int defValue) {
        return sp.getInt(key, defValue);
    }

    /**
     * 获取boolean类型属性
     *
     * @param sp
     * @param key
     * @param defValue
     * @return
     * @remark 是否全屏
     */
    public static boolean getBooleanAttribute(SharedPreferences sp, String key, boolean defValue) {
        return sp.getBoolean(key, defValue);
    }

    /**
     * 获取SharedPreferences对象
     *
     * @param context
     * @return
     */
    public SharedPreferences getShaPreferencesInstance(Context context) {
        if (mSharedPreferences == null) {
            mSharedPreferences =
                    context.getSharedPreferences(SP_SP_NAME_SETTING, Context.MODE_PRIVATE);
        }
        return mSharedPreferences;
    }

    /**
     * 获取字符串
     */
    public String getString(String key) {
        if (mSharedPreferences != null) {
            return mSharedPreferences.getString(key, "");
        } else {
            return "";
        }

    }

    /**
     * 保存字符串
     */
    public void saveString(String key, String value) {
        if (mSharedPreferences != null) {
            mSharedPreferences.edit().putString(key, value).commit();
        }
    }

    /**
     * 获取字档
     */
    public int getSizePosition() {
        return mSharedPreferences.getInt(SP_SETTING_SIZEPOSITION, BookContentSettings.DEFAULT_SIZE_POSITION);
    }

    /**
     * 保存字档
     */
    public void saveSizePosition(int position) {
        saveIntAttribute(SP_SETTING_SIZEPOSITION, position);
    }

    /**
     * 正文页字体大小
     */
    public void saveBookContentFontSize(int size) {
        saveIntAttribute(SP_SETTING_TEXTSIZE, size);
    }

    /**
     * 获取正文页字体大小
     */
    public int getBookContentFontSize() {
        return mSharedPreferences.getInt(SP_SETTING_TEXTSIZE,
                BookContentSettings.BOOKCONTENT_DEFAULT_TEXT_SIZE);
    }

    /**
     * 保存正文页标题字体大小
     */
    public void saveBookContentTitleFontSize(int size) {
        saveIntAttribute(SP_SETTING_TITLE_TEXTSIZE, size);
    }

    /**
     * 获取正文页标题字体大小
     */
    public int getBookContentTitleFontSize() {
        return mSharedPreferences.getInt(SP_SETTING_TITLE_TEXTSIZE,
                BookContentSettings.TITLE_DEFAULT_TEXT_SIZE);
    }


    // ====================================================================================

    /**
     * 获取标题与正文间距
     */
    public int getTitleSpace() {
        return mSharedPreferences.getInt(SP_SETTING_TITLE_SPACE,
                BookContentSettings.TITLE_DEFAULT_SPACE);
    }

    /**
     * 保存标题与正文间距
     */
    public void saveTitleSpace(int size) {
        saveIntAttribute(SP_SETTING_TITLE_SPACE, size);
    }

    /**
     * 保存行间距
     */
    public void saveBookContentLineSpace(int size) {
        saveIntAttribute(SP_SETTING_LINESPACE, size);
    }

    /**
     * 获取行间距
     */
    public int getBookContentLineSpace() {
        return mSharedPreferences.getInt(SP_SETTING_LINESPACE,
                BookContentSettings.LINE_DEFAULT_SPACE);
    }

    /**
     * 获取段间距
     */
    public int getBookContentParagraphSpace() {
        return mSharedPreferences.getInt(SP_SETTING_PARAGRAPHSPACE,
                BookContentSettings.PARAGRAPH_DEFAULT_SPACE);
    }

    /**
     * 保存段间距
     */
    public void saveBookContentParagraphSpace(int size) {
        saveIntAttribute(SP_SETTING_PARAGRAPHSPACE, size);
    }

    /**
     * 保存阅读页翻页模式
     *
     * @param pageTurnMode
     */
    public void saveBookContentPageTurnMode(int pageTurnMode) {
        saveIntAttribute(SP_SETTING_PAGE_TURN_MODE, pageTurnMode);
    }

    /**
     * 获取阅读页翻页模式
     *
     * @return
     */
    public int getBookContentPageTurnMode() {
        return mSharedPreferences.getInt(SP_SETTING_PAGE_TURN_MODE, DEFAULT_PAGE_TURN_MODE);
    }

    /**
     * 保存主题模式
     *
     * @param theme
     */
    public void saveBookContentTheme(int theme) {
        saveIntAttribute(SP_SETTING_THEME, theme);
    }

    /**
     * 获取主题模式
     *
     * @return
     */
    public int getBookContentTheme() {
        return mSharedPreferences.getInt(SP_SETTING_THEME, DEFAULT_THEME);
    }

    /**
     * 保存上次主题模式
     *
     * @param theme
     */
    public void saveBookContentLastTheme(int theme) {
        saveIntAttribute(SP_SETTING_LAST_THEME, theme);
    }

    /**
     * 获取上次主题模式
     *
     * @return
     */
    public int getBookContentLastTheme() {
        return mSharedPreferences.getInt(SP_SETTING_LAST_THEME, DEFAULT_THEME);
    }

    /**
     * 保存系统按钮点击状态
     *
     * @param isClickSystemButton
     */
    public void saveBookContentSystemButton(boolean isClickSystemButton) {
        saveBooleanAttribute(SP_SETTING_SCREEN_SYSTEM_BUTTON, isClickSystemButton);
    }

    /**
     * 获取系统按钮点击状态
     *
     * @return
     */
    public boolean getBookContentSystemButton() {
        return mSharedPreferences.getBoolean(SP_SETTING_SCREEN_SYSTEM_BUTTON, true);
    }

    /**
     * 系统亮度
     *
     * @param light
     */
    public void saveBookContentScreenLight(int light) {
        saveIntAttribute(SP_SETTING_SCREEN_LIGHT, light);
    }

    /**
     * 获取屏幕亮度
     *
     * @return
     */
    public int getBookContentLight() {
        return mSharedPreferences.getInt(SP_SETTING_SCREEN_LIGHT, DEFAULT_LIGHT);
    }

    /**
     * 阅读页帮助引导图
     *
     * @param isShow
     */
    public void saveBookContentHelpImageState(boolean isShow) {
        saveBooleanAttribute(SP_SETTING_HELP_SHOW, isShow);
    }

    /**
     * 获取阅读页帮助引导图状态
     *
     * @return
     */
    public boolean getBookContentHelpImageState() {
        return mSharedPreferences.getBoolean(SP_SETTING_HELP_SHOW, false);
    }

    /**
     * 保存是否是全屏属性
     *
     * @param isFullScreen
     */
    public void saveBookContentFullScreen(boolean isFullScreen) {
        saveBooleanAttribute(SP_SETTING_IS_FULL_SCREEN, isFullScreen);
    }

    /**
     * 获取是否全屏
     *
     * @return
     */
    public boolean getBookContentFullScreen() {
        return mSharedPreferences.getBoolean(SP_SETTING_IS_FULL_SCREEN, DEFAULT_FULLSCREEN);
    }

    /**
     * 保存屏幕亮度值 0-255
     *
     * @param brigh
     */
    public void saveScreenBrightness(int brigh) {

        saveIntAttribute(SP_SCREEN_BRIGHTNESS, brigh);

    }

    /**
     * 获取屏幕亮度值 0-255
     *
     * @return
     */
    public int getScreenBrightness() {
        return mSharedPreferences.getInt(SP_SCREEN_BRIGHTNESS, SCREEN_BRIGHTNESS);
    }

    /**
     * 保存夜间模式状态
     *
     * @param isNightMode
     */
    public void saveBookContentNightMode(boolean isNightMode) {
        // Log4an.i("yjd", "保存夜间模式："+isNightMode);
        saveBooleanAttribute(SP_SETTING_IS_NIGHT_MODE, isNightMode);
    }

    /**
     * 获取夜间模式状态
     *
     * @return
     */
    public boolean getBookContentNightMode() {
        // Log4an.i("yjd", "获取夜间模式："+mSharedPreferences.getBoolean(SP_SETTING_IS_NIGHT_MODE,
        // DEFAULT_IS_NIGHTMODE));
        return mSharedPreferences.getBoolean(SP_SETTING_IS_NIGHT_MODE, DEFAULT_IS_NIGHTMODE);
    }

    /**
     * 保存两侧翻页状态
     *
     * @param isTurnPageMode
     */
    public void saveBookContentTurnpagemode(boolean isTurnPageMode) {
        saveBooleanAttribute(SP_SETTING_IS_CLICKSIDE_TURNPAGEMODE, isTurnPageMode);
    }

    /**
     * 获取两侧翻页状态
     *
     * @return
     */
    public boolean getBookContentTurnPageMode() {
        return mSharedPreferences.getBoolean(SP_SETTING_IS_CLICKSIDE_TURNPAGEMODE,
                DEFAULT_SIDE_ENABLED);
    }

    /**
     * 保存夜间模式对话框不再提示状态
     *
     * @param isNightModeNowarn
     */
    public void saveBookContentNightModeNowarn(boolean isNightModeNowarn) {
        saveBooleanAttribute(SP_SETTING_NIGHT_MODE_NOWARN, isNightModeNowarn);
    }

    /**
     * 获取夜间模式对话框不再提示状态
     *
     * @return
     */
    public boolean getBookContentNightModeNowarn() {
        return mSharedPreferences.getBoolean(SP_SETTING_NIGHT_MODE_NOWARN,
                DEFAULT_NIGHT_MODE_NOWARN);
    }

    /**
     * 保存屏幕亮度时间
     *
     * @param keepScreenTime
     */
    public void saveBookContentKeepScreenTime(int keepScreenTime) {
        saveIntAttribute(SP_SETTING_KEEP_SCREEN_TIME, keepScreenTime);
    }

    /**
     * 获取屏幕亮度时间
     *
     * @return
     */
    public int getBookContentKeepScreenTime() {
        return mSharedPreferences.getInt(SP_SETTING_KEEP_SCREEN_TIME, DEFAULT_KEEP_SCREEN_TIME);
    }

    public void saveLeftToTurnNext(boolean clickNext) {
        saveBooleanAttribute(SP_LEFT_CLICK_TO_NEXT, clickNext);
    }

    public boolean isLeftClicked() {
        return getBooleanAttribute(mSharedPreferences, SP_LEFT_CLICK_TO_NEXT, true);
    }

    public void saveLineSpaceStyle(int style) {
        saveIntAttribute(SP_SETTING_LINE_SPACE_STYLE, style);
    }

    public int getLineSpaceStyle() {
        return getIntAttribute(mSharedPreferences, SP_SETTING_LINE_SPACE_STYLE, 1);
    }

    /**
     * 设置int类型属性
     *
     * @param key
     * @param value
     */
    private void saveIntAttribute(String key, int value) {
        mSharedPreferences.edit().putInt(key, value).commit();
    }

    /**
     * 设置float类型属性
     *
     * @param key
     * @param value
     */
    private void saveFloatAttribute(String key, float value) {
        mSharedPreferences.edit().putFloat(key, value).commit();
    }

    /**
     * 设置boolean类型属性
     *
     * @param key
     * @param value
     */
    private void saveBooleanAttribute(String key, boolean value) {
        mSharedPreferences.edit().putBoolean(key, value).commit();
    }


}
