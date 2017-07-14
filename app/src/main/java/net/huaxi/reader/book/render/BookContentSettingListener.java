package net.huaxi.reader.book.render;

/**
 * Created by Saud on 16/1/4.
 */
public interface BookContentSettingListener {


    /**
     * 上一章
     */
    void onPreChapter();

    /**
     * 下一章
     */
    void onNextChapter();


    /**
     * 跳转到指定章节
     * 滑动进度0-100
     *
     * @param progress
     */
    void onChapterChanged(int progress);


    /**
     * 主题模式变化
     *
     * @param theme (0:白色;1:粉红;2：绿色;3:黄褐色;4:夜晚模式;)
     */
    void onThemeChanged(int theme);

    /**
     * 评论
     */
    void onComment();


    /**
     * 返回
     */
    void onBack();

    /**
     * 下载
     */
    void onDownload();


    /**
     * 听书
     */
    void onListenBook();

    /**
     * 书的详情
     */
    void onOpenDetail();

    /**
     * 大字体
     */
    void onTextSizeBig();

    /**
     * 小字体
     */
    void onTextSizeSmall();


    /**
     * “更多”的设置
     */
    void onMoreSetting();


}
