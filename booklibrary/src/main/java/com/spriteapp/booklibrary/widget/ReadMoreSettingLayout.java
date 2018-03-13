package com.spriteapp.booklibrary.widget;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;

/**
 * Created by kuangxiaoguo on 2017/7/14.
 */

public class ReadMoreSettingLayout extends LinearLayout {

    private Activity mContext;
    private View empty_view;
    private RelativeLayout relative_center_view;
    private LinearLayout linear_bottom_view;
    private ImageView book_cover;
    private TextView book_title;
    private TextView author_name;
    private ImageView author_avater;
    private Switch switch_button;
    private SeekBar seekBar;
    private TextView tv_reduce;
    private TextView tv_add;
    private TextView tv_text_size;
    private RadioGroup radio_font, radio_format, radio_bg_color;
    private View mView;

    public void setActivity(Activity a){
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
        linear_bottom_view = (LinearLayout) mView.findViewById(R.id.linear_bottom_view);
        book_cover = (ImageView) mView.findViewById(R.id.book_cover);
        author_avater = (ImageView) mView.findViewById(R.id.author_avatar);
        book_title = (TextView) mView.findViewById(R.id.book_title);
        author_name = (TextView) mView.findViewById(R.id.author_name);
        switch_button = (Switch) mView.findViewById(R.id.switch_button);
        seekBar = (SeekBar) mView.findViewById(R.id.seekBar);
        tv_reduce = (TextView) mView.findViewById(R.id.tv_reduce);
        tv_add = (TextView) mView.findViewById(R.id.tv_add);
        tv_text_size = (TextView) mView.findViewById(R.id.tv_text_size);
        radio_font = (RadioGroup) mView.findViewById(R.id.radio_font);
        radio_format = (RadioGroup) mView.findViewById(R.id.radio_format);
        radio_bg_color = (RadioGroup) mView.findViewById(R.id.radio_bg_color);
        setListener();
    }

    public void changeMode(boolean isNight) {
        mView.setBackgroundResource(isNight ? R.color.book_reader_read_bottom_night_background
                : R.color.book_reader_white);
    }

    private void setListener() {
        empty_view.setOnClickListener(null);
        relative_center_view.setOnClickListener(null);
        linear_bottom_view.setOnClickListener(null);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setBrightness(mContext,progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /**
     * 获取屏幕的亮度
     */
    public static int getScreenBrightness(Context context) {
        int nowBrightnessValue = 0;
        ContentResolver resolver = context.getContentResolver();
        try {
            nowBrightnessValue = android.provider.Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nowBrightnessValue;
    }

    /**
     * 设置当前Activity显示时的亮度
     * 屏幕亮度最大数值一般为255，各款手机有所不同
     * screenBrightness 的取值范围在[0,1]之间
     */
    public static void setBrightness(Activity activity, int brightness) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
        activity.getWindow().setAttributes(lp);
    }
}
