package net.huaxi.reader.appinterface;

import net.huaxi.reader.db.model.ChapterTable;

import java.util.List;

/**
 * @Description: [获取数据的接口]
 * @Author: [Saud]
 * @CreateDate: [16/1/7 13:21]
 * @UpDate: [16/1/7 13:21]
 * @Version: [v1.0]
 */
public interface onCatalogLoadFinished {
    /**
     * 返回新的数据
     *
     * @param chapterTables
     */
    void onFinished(List<ChapterTable> chapterTables, int resultCode);

}
