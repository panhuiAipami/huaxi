package com.spriteapp.booklibrary.util;

import android.util.Log;

import com.spriteapp.booklibrary.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by userfirst on 2018/2/26.
 * 搜索文本与背景颜色
 */

public class ColorUtils {
    public static List<Integer> textColors = new ArrayList<>();
    public static List<Integer> bgColors = new ArrayList<>();

    public static void setColor() {
        if (textColors.size() == 0) {
            textColors.add(R.color.text1);
            textColors.add(R.color.text2);
            textColors.add(R.color.text3);
            textColors.add(R.color.text4);
        }
        if (bgColors.size() == 0) {
            bgColors.add(R.color.bg1);
            bgColors.add(R.color.bg2);
            bgColors.add(R.color.bg3);
            bgColors.add(R.color.bg4);
        }

    }

    /**
     * 生成0-4之间的随机数，包括0，不包括4。
     */
    public static int getRandom() {
        Random rand = new Random();
        return rand.nextInt(4);
    }
}
