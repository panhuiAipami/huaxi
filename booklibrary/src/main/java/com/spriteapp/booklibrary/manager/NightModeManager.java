package com.spriteapp.booklibrary.manager;

import android.content.res.ColorStateList;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.util.AppUtil;

/**
 * 原生书城日夜间切换
 * Created by kuangxiaoguo on 2017/8/14.
 */

public class NightModeManager {

    private static final String TAG = "NightModeManager";
    private static StoreColorManager manager;
    private static boolean isNight;

    public static void setStoreTheme(boolean isNight) {
        NightModeManager.isNight = isNight;
        manager = new StoreColorManager.Builder()
                .setContainerBackground(getColor(R.color.book_reader_white, R.color.book_reader_store_container_night_background))
                .setPayTextColor(getColor(R.color.book_reader_pay_text_day_color, R.color.book_reader_pay_text_night_color))
                .setRechargeTextColor(getColorStateList(R.color.book_reader_bottom_text_selector, R.color.book_reader_bottom_text_night_selector))
                .setDivideViewBackground(getColor(R.color.book_reader_container_color, R.color.book_reader_divide_view_night_background))
                .setDivideLineColor(getColor(R.color.book_reader_divide_line_color, R.color.book_reader_divide_line_night_color))
                .setVerticalMarkColor(getColor(R.color.book_reader_main_color, R.color.book_reader_main_night_color))
                .setBookTitleColor(getColor(R.color.book_reader_common_text_color, R.color.book_reader_book_title_night_color))
                .setBookAuthorColor(getColor(R.color.book_reader_book_author_day_color, R.color.book_reader_book_author_night_color))
                .setAllBookColor(getColor(R.color.book_reader_all_book_day_color, R.color.book_reader_all_book_night_color))
                .setBookLineBackground(getResource(R.drawable.book_reader_store_item_shape, R.drawable.book_reader_store_item_night_shape))
                .setRechargeTextSelector(getResource(R.drawable.book_reader_recharge_text_selector, R.drawable.book_reader_recharge_text_night_selector))
                .setAllBookImageResource(getResource(R.drawable.book_reader_see_all_image, R.drawable.book_reader_see_all_image_night))
                .build();
    }

    public static StoreColorManager getManager() {
        return manager;
    }

    private static int getColor(int dayColor, int nightColor) {
        return AppUtil.getAppContext().getResources().getColor(isNight ? nightColor : dayColor);
    }

    private static ColorStateList getColorStateList(int daySelector, int nightSelector) {
        return AppUtil.getAppContext().getResources().getColorStateList(isNight ? nightSelector : daySelector);
    }

    private static int getResource(int dayResource, int nightResource) {
        return isNight ? nightResource : dayResource;
    }

}
