package com.spriteapp.booklibrary.widget;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.callback.ReaderMoreSettingCallback;
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.enumeration.AutoSubEnum;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.ui.activity.DownloadFontsActivity;
import com.spriteapp.booklibrary.util.Constants;
import com.spriteapp.booklibrary.util.FileUtils;
import com.spriteapp.booklibrary.util.GlideUtils;
import com.spriteapp.booklibrary.util.ScreenUtil;
import com.spriteapp.booklibrary.util.SharedPreferencesUtil;
import com.spriteapp.booklibrary.util.ToastUtil;
import com.spriteapp.booklibrary.widget.readview.Config;

/**
 * 阅读器更多设置
 * Created by kuangxiaoguo on 2017/7/14.
 */

public class ReadMoreSettingLayout extends LinearLayout {
    public static Typeface t1 = null;
    public static Typeface t2 = null;
    public static Typeface t3 = null;
    public static Typeface t4 = null;
    public static Typeface t5 = null;

    private ReaderMoreSettingCallback moreSettingCallback;
    private Activity mContext;
    private boolean isBrightness = false;
    private View empty_view;
    private RelativeLayout relative_center_view;
    private LinearLayout linear_right, linear_bottom_view;
    private ImageView book_cover;
    private TextView book_title;
    private TextView author_name;
    private ImageView author_avater;
    private Switch switch_button;
    private SeekBar seekBar;
    private TextView tv_reduce;
    private TextView tv_add;
    private TextView tv_text_size;
    private RadioGroup radio_font, radio_format, radio_bg_color,radio_flip,radio_progress;
    private RadioButton font0, font1, font2, font3, font4, font5;
    private CheckBox btn_system_brightness;
    private float font_size = 16;
    private View mView;

    public ReaderMoreSettingCallback getMoreSettingCallback() {
        return moreSettingCallback;
    }

    public void setMoreSettingCallback(ReaderMoreSettingCallback moreSettingCallback) {
        this.moreSettingCallback = moreSettingCallback;
    }

    public void setActivity(Activity a) {
        mContext = a;
    }

    public ReadMoreSettingLayout(Context context) {
        this(context, null);
    }

    public ReadMoreSettingLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReadMoreSettingLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context c) {
        mView = LayoutInflater.from(c).inflate(R.layout.book_reader_more_setting_layout, null);
        addView(mView);
        empty_view = mView.findViewById(R.id.empty_view);
        relative_center_view = (RelativeLayout) mView.findViewById(R.id.relative_center_view);
        linear_right = (LinearLayout) mView.findViewById(R.id.linear_right);
        linear_bottom_view = (LinearLayout) mView.findViewById(R.id.linear_bottom_view);
        book_cover = (ImageView) mView.findViewById(R.id.book_cover);
        author_avater = (ImageView) mView.findViewById(R.id.author_avatar);
        book_title = (TextView) mView.findViewById(R.id.book_title);
        author_name = (TextView) mView.findViewById(R.id.author_name);
        switch_button = (Switch) mView.findViewById(R.id.switch_button);
        seekBar = (SeekBar) mView.findViewById(R.id.seekBar);
        btn_system_brightness = (CheckBox) mView.findViewById(R.id.btn_system_brightness);
        tv_reduce = (TextView) mView.findViewById(R.id.tv_reduce);
        tv_add = (TextView) mView.findViewById(R.id.tv_add);
        tv_text_size = (TextView) mView.findViewById(R.id.tv_text_size);
        radio_font = (RadioGroup) mView.findViewById(R.id.radio_font);
        radio_format = (RadioGroup) mView.findViewById(R.id.radio_format);
        radio_bg_color = (RadioGroup) mView.findViewById(R.id.radio_bg_color);
        radio_flip = (RadioGroup) mView.findViewById(R.id.radio_flip);
        radio_progress = (RadioGroup) mView.findViewById(R.id.radio_progress);
        font0 = (RadioButton) mView.findViewById(R.id.font0);
        font1 = (RadioButton) mView.findViewById(R.id.font1);
        font2 = (RadioButton) mView.findViewById(R.id.font2);
        font3 = (RadioButton) mView.findViewById(R.id.font3);
        font4 = (RadioButton) mView.findViewById(R.id.font4);
        font5 = (RadioButton) mView.findViewById(R.id.font5);


        setListener();
        initTypeFace();

    }


    /**
     * 初始化阅读设置信息
     * @param bookDetail
     */
    public void initRaderSetting(BookDetailResponse bookDetail) {
        if (bookDetail != null) {
            book_title.setText(bookDetail.getBook_name());
            author_name.setText(bookDetail.getAuthor_name());
            GlideUtils.loadImage(book_cover, bookDetail.getBook_image(), mContext);
            GlideUtils.loadImage(author_avater, bookDetail.getAuthor_avatar(), mContext);
        }


        //是否订阅
        boolean isAutoSub = SharedPreferencesUtil.getInstance()
                .getInt(Constant.IS_BOOK_AUTO_SUB) == AutoSubEnum.AUTO_SUB.getValue();
        switch_button.setChecked(isAutoSub);

        //亮度
        float brightness = Config.getInstance().getBrightness();
        btn_system_brightness.setChecked(Config.getInstance().getSystemBrightness());
        if (brightness >= 0) {
            seekBar.setProgress((int) (brightness*255));
            setBrightness(mContext, brightness);
        }else{
            btn_system_brightness.setChecked(true);
            seekBar.setProgress(getScreenBrightness2(mContext));
        }


        //字号
        font_size = ScreenUtil.pxToDp(Config.getInstance().getFontSize());
        tv_text_size.setTextSize(font_size);

        //字体
        refreshFontSelect(true);


        //阅读行间距
        int format = Config.getInstance().getFontFormat();
        RadioButton radioButton;
        switch (format) {
            case 1:
                radioButton = (RadioButton) radio_format.findViewById(R.id.format1);
                radioButton.setChecked(true);
                break;
            case 2:
                radioButton = (RadioButton) radio_format.findViewById(R.id.format2);
                radioButton.setChecked(true);
                break;
            case 3:
                radioButton = (RadioButton) radio_format.findViewById(R.id.format3);
                radioButton.setChecked(true);
                break;
        }

        //背景颜色
        int bg_color = Config.getInstance().getBookBgType();
        RadioButton radioButtonColor;
        switch (bg_color) {
            case 1:
                radioButtonColor = (RadioButton) radio_bg_color.findViewById(R.id.reader_bg_color1);
                radioButtonColor.setChecked(true);
                break;
            case 2:
                radioButtonColor = (RadioButton) radio_bg_color.findViewById(R.id.reader_bg_color2);
                radioButtonColor.setChecked(true);
                break;
            case 3:
                radioButtonColor = (RadioButton) radio_bg_color.findViewById(R.id.reader_bg_color3);
                radioButtonColor.setChecked(true);
                break;
            case 4:
                radioButtonColor = (RadioButton) radio_bg_color.findViewById(R.id.reader_bg_color4);
                radioButtonColor.setChecked(true);
                break;
            case 5:
                radioButtonColor = (RadioButton) radio_bg_color.findViewById(R.id.reader_bg_color5);
                radioButtonColor.setChecked(true);
                break;
            case 6:
                radioButtonColor = (RadioButton) radio_bg_color.findViewById(R.id.reader_bg_color6);
                radioButtonColor.setChecked(true);
                break;

        }

        //翻页
        int page_mode = Config.getInstance().getPageMode();
        RadioButton radioButtonPageMode;
        switch (page_mode) {
            case 0:
                radioButtonPageMode = (RadioButton) radio_flip.findViewById(R.id.flip_style1);
                radioButtonPageMode.setChecked(true);
                break;
            case 1:
                radioButtonPageMode = (RadioButton) radio_flip.findViewById(R.id.flip_style2);
                radioButtonPageMode.setChecked(true);
                break;
            case 2:
                radioButtonPageMode = (RadioButton) radio_flip.findViewById(R.id.flip_style3);
                radioButtonPageMode.setChecked(true);
                break;
        }

        //显示进度
        int progress_format = Config.getInstance().getReaderProgressFormat();
        RadioButton radioButtonProgressFormat;
        switch (progress_format) {
            case 0:
                radioButtonProgressFormat = (RadioButton) radio_progress.findViewById(R.id.progress_format1);
                radioButtonProgressFormat.setChecked(true);
                break;
            case 1:
                radioButtonProgressFormat = (RadioButton) radio_progress.findViewById(R.id.progress_format2);
                radioButtonProgressFormat.setChecked(true);
                break;
        }

    }

    public void refreshFontSelect(boolean resetPageFont) {
        int check_font = Config.getInstance().getFontStyle();
        Typeface tf = null;
        if (check_font == 0) {
            font0.setChecked(true);
        } else if (check_font == 1) {
            font1.setChecked(true);
            tf = t1;
        } else if (check_font == 2) {
            font2.setChecked(true);
            tf = t2;
        } else if (check_font == 3) {
            font3.setChecked(true);
            tf = t3;
        } else if (check_font == 4) {
            font4.setChecked(true);
            tf = t4;
        } else if (check_font == 5) {
            font5.setChecked(true);
            tf = t5;
        }
        if (resetPageFont) {//修改页面字体
            if (moreSettingCallback != null)
                moreSettingCallback.sendFontStyle(tf);
        }
    }


    //夜间模式
    public void changeMode(boolean isNight) {
        linear_bottom_view.setBackgroundResource(isNight ? R.color.book_reader_read_title_night_color
                : R.color.book_reader_white);
        linear_right.setBackgroundResource(isNight ? R.color.book_reader_read_title_night_color
                : R.color.book_reader_white);
    }

    private void setListener() {
        empty_view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibility(GONE);
            }
        });
        relative_center_view.setOnClickListener(null);
        linear_bottom_view.setOnClickListener(null);

        //设置字体大小
        tv_reduce.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (font_size <= 10) {
                    ToastUtil.showSingleToast("已是最小字体");
                    return;
                }
                tv_text_size.setTextSize(font_size -= 2);
                Config.getInstance().setFontSize(font_size);
                if (moreSettingCallback != null)
                    moreSettingCallback.sendTextSize(font_size);
            }
        });
        tv_add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (font_size >= 30) {
                    ToastUtil.showSingleToast("已是最大字体");
                    return;
                }
                tv_text_size.setTextSize(font_size += 2);
                Config.getInstance().setFontSize(font_size);
                if (moreSettingCallback != null)
                    moreSettingCallback.sendTextSize(font_size);
            }
        });

        //设置字体格式
        radio_font.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (!group.findViewById(checkedId).isPressed()) return;
                if (checkedId == R.id.font0) {
                    gotoDownLoadFont(null, 0);
                } else if (checkedId == R.id.font1) {
                    gotoDownLoadFont(t1, 1);
                } else if (checkedId == R.id.font2) {
                    gotoDownLoadFont(t2, 2);
                } else if (checkedId == R.id.font3) {
                    gotoDownLoadFont(t3, 3);
                } else if (checkedId == R.id.font4) {
                    gotoDownLoadFont(t4, 4);
                } else if (checkedId == R.id.font5) {
                    gotoDownLoadFont(t5, 5);
                }
            }
        });

        //阅读页面排版
        radio_format.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (!group.findViewById(checkedId).isPressed()) return;
                int format = 0;
                if (checkedId == R.id.format1) {
                    format = 1;
                } else if (checkedId == R.id.format2) {
                    format = 2;
                } else if (checkedId == R.id.format3) {
                    format = 3;
                }
                Config.getInstance().setFontFormat(format);
                if (moreSettingCallback != null)
                    moreSettingCallback.sendFontFormat(format);
            }
        });

        //阅读背景颜色
        radio_bg_color.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (!group.findViewById(checkedId).isPressed()) return;
                int color = 0;
                if (checkedId == R.id.reader_bg_color1) {
                    color = 1;
                } else if (checkedId == R.id.reader_bg_color2) {
                    color = 2;
                } else if (checkedId == R.id.reader_bg_color3) {
                    color = 3;
                } else if (checkedId == R.id.reader_bg_color4) {
                    color = 4;
                } else if (checkedId == R.id.reader_bg_color5) {
                    color = 5;
                } else if (checkedId == R.id.reader_bg_color6) {
                    color = 6;
                }
                Config.getInstance().setBookBgType(color);
                if (moreSettingCallback != null)
                    moreSettingCallback.sendReaderBgColor(color);
            }
        });

        //翻页
        radio_flip.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (!group.findViewById(checkedId).isPressed()) return;
                int page_mode = 0 ;
                if (checkedId == R.id.flip_style1) {
                    page_mode = 0;
                } else if (checkedId == R.id.flip_style2) {
                    page_mode = 1;
                } else if (checkedId == R.id.flip_style3) {
                    page_mode = 2;
                }
                Config.getInstance().setPageMode(page_mode);
                if (moreSettingCallback != null)
                    moreSettingCallback.sendPageMode(page_mode);
            }
        });

        //显示进度
        radio_progress.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (!group.findViewById(checkedId).isPressed()) return;
                int progress_format = 0 ;
                if (checkedId == R.id.progress_format1) {
                    progress_format = 0;
                } else if (checkedId == R.id.progress_format2) {
                    progress_format = 1;
                }
                Config.getInstance().setReaderProgressFormat(progress_format);
                if (moreSettingCallback != null)
                    moreSettingCallback.sendProgressFormat(progress_format);
            }
        });
        //自动订阅按钮
        switch_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtil.getInstance().putInt(Constant.IS_BOOK_AUTO_SUB,
                        isChecked ? AutoSubEnum.AUTO_SUB.getValue()
                                : AutoSubEnum.NOT_AUTO_SUB.getValue());
            }
        });

        //亮度调节
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mContext != null && isBrightness) {
                    setBrightness(mContext, Float.valueOf(progress) * (1f / 255f));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isBrightness = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                btn_system_brightness.setChecked(false);

            }
        });

        btn_system_brightness.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Config.getInstance().setSystemBrightness(isChecked);
                if(isChecked){
                    float system_brightness = getScreenBrightness(mContext);
                    setBrightness(mContext,-1);
                    seekBar.setProgress((int) (system_brightness*255));
                }else{
                    float brightness = Config.getInstance().getBrightness();
                    if(brightness <0){
                        brightness = Float.valueOf(getScreenBrightness2(mContext) ) * (1f / 255f);
                    }
                    setBrightness(mContext,brightness);
                    seekBar.setProgress((int) (brightness*255));
                }
            }
        });
    }

    /**
     * 获取屏幕的亮度
     */
    public static int getScreenBrightness2(Context context) {
        int nowBrightnessValue = 0;
        ContentResolver resolver = context.getContentResolver();
        try {
            nowBrightnessValue = android.provider.Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return nowBrightnessValue;
    }

    public static float getScreenBrightness(Activity activity) {
        Window localWindow = activity.getWindow();
        WindowManager.LayoutParams params = localWindow.getAttributes();
        return params.screenBrightness;
    }

    /**
     * 设置当前Activity显示时的亮度
     * 屏幕亮度最大数值一般为255，各款手机有所不同
     * screenBrightness 的取值范围在[0,1]之间
     */
    public static void setBrightness(Activity activity, float brightness) {
        Config.getInstance().setBrightness(brightness);
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.screenBrightness = brightness;//Float.valueOf(brightness) * (1f / 255f)
        activity.getWindow().setAttributes(lp);

    }

    public static void initTypeFace() {
        try {
            if (t1 == null && FileUtils.isExists(getFontPath(DownloadFontsActivity.hei))) {
                t1 = Typeface.createFromFile(getFontPath(DownloadFontsActivity.hei));
            }
            if (t2 == null && FileUtils.isExists(getFontPath(DownloadFontsActivity.siyuan))) {
                t2 = Typeface.createFromFile(getFontPath(DownloadFontsActivity.siyuan));
            }
            if (t3 == null && FileUtils.isExists(getFontPath(DownloadFontsActivity.shu))) {
                t3 = Typeface.createFromFile(getFontPath(DownloadFontsActivity.shu));
            }
            if (t4 == null && FileUtils.isExists(getFontPath(DownloadFontsActivity.kai))) {
                t4 = Typeface.createFromFile(getFontPath(DownloadFontsActivity.kai));
            }
            if (t5 == null && FileUtils.isExists(getFontPath(DownloadFontsActivity.fangsong))) {
                t5 = Typeface.createFromFile(getFontPath(DownloadFontsActivity.fangsong));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getFontPath(String font) {
        return Constants.DOWNLOAD_FONTS + "/" + font;
    }


    public void gotoDownLoadFont(Typeface typeface, int font_num) {
        if (font_num != 0 && typeface == null) {
            mContext.startActivityForResult(new Intent(mContext, DownloadFontsActivity.class), 99);
        } else {
            //修改页面字体
            Config.getInstance().setFontStyle(font_num);
            if (moreSettingCallback != null)
                moreSettingCallback.sendFontStyle(typeface);
        }
    }
}
