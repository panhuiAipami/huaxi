package com.spriteapp.booklibrary.util;

/**
 * Created by kuangxiaoguo on 2017/8/3.
 */

public class RecyclerViewUtil {

    private static final int LEFT_MARGIN = 15;
    private static final int RIGHT_MARGIN = 15;

    /**
     * @param spaceCount 分割线个数
     * @param spaceWidth 分割线宽度
     * @return imageWidth(px)
     */
    public static int getImageWidth(int spaceCount, int spaceWidth) {
        int imageWidth;
        int screenWidth = ScreenUtil.getScreenWidth();
        int containerWidth = screenWidth - ScreenUtil.dpToPxInt(LEFT_MARGIN)
                - ScreenUtil.dpToPxInt(RIGHT_MARGIN);
        int totalSpaceWidth = ScreenUtil.dpToPxInt(spaceCount * spaceWidth);
        imageWidth = (containerWidth - totalSpaceWidth) / (spaceCount + 1);
        return imageWidth;
    }
}
