package net.huaxi.reader.model;

import com.tools.commonlibs.common.CommonApp;
import net.huaxi.reader.common.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import net.huaxi.reader.R;
import net.huaxi.reader.bean.DownLoadChild;
import net.huaxi.reader.bean.DownLoadGroup;
import net.huaxi.reader.db.model.ChapterTable;

/**
 * Created by Saud on 16/1/22.
 */
public class DownloadDataFactory {


    /**
     * 获取免费章节的个数
     *
     * @param chapterTables
     * @return
     */
    public int getFreeNum(List<ChapterTable> chapterTables) {

        int freeNum = 0;
        for (int i = 0; i < chapterTables.size(); i++) {
            if (chapterTables.get(i).getIsVip() == 0) {
                freeNum++;
            } else {
                return freeNum;
            }
        }
        return freeNum;
    }


    /**
     * 对分组后的数据进行进一步处理，使符合列表显示
     *
     * @param freeNum
     * @return
     */
    public List dataFactory(int freeNum, List<List<ChapterTable>> lists) {
        int preNum = 1;
        int nextNum = 0;
        List<DownLoadGroup> groupList = new Vector<>();
        if (lists == null)
            return groupList;
        for (int i = 0; i < lists.size(); i++) {
            DownLoadGroup downLoadGroup = new DownLoadGroup();
            downLoadGroup.setChecked(false);
            List<ChapterTable> chapterTables = lists.get(i);
            List<DownLoadChild> childList = new ArrayList<DownLoadChild>();


            for (int j = 0; j < chapterTables.size(); j++) {
                DownLoadChild downLoadChild = new DownLoadChild();
                downLoadChild.setChecked(false);
                ChapterTable chapterTable = chapterTables.get(j);
                downLoadChild.setName(chapterTable.getName());
                downLoadChild.setChapterId(chapterTable.getChapterId());
                downLoadChild.setPrice(chapterTable.getPrice());
                downLoadChild.setBookId(chapterTable.getBookId());
                downLoadChild.setIsSubscribe(chapterTable.getIsSubscribed());
                childList.add(downLoadChild);
                downLoadChild.addObserver(downLoadGroup);
                downLoadGroup.addObserver(downLoadChild);
            }
            if (i == 0) {
                preNum = 1;
                nextNum = freeNum;
            } else {
                preNum = freeNum + (i - 1) * Constants.DOWNLOAD_LIST_CHILD_NUM + 1;
                nextNum = freeNum + i * Constants.DOWNLOAD_LIST_CHILD_NUM - (Constants.DOWNLOAD_LIST_CHILD_NUM - childList.size());
            }
            String str = String.format(CommonApp.context().getString(R.string.download_group_tag), preNum, nextNum);
            downLoadGroup.setName(str);
            downLoadGroup.setChildList(childList);
            groupList.add(downLoadGroup);
        }

        return groupList;
    }


    /**
     * 拆分集合
     *
     * @param <T>
     * @param resList 要拆分的集合
     * @param start   设置第一组的个数
     * @param count   每个集合的元素个数
     * @return 返回拆分后的各个集合
     */
    public <T> List<List<T>> split(List<T> resList, int start, int count) {
        if (resList == null || count < 1)
            return null;
        List<List<T>> ret = new ArrayList<List<T>>();
        List<T> items = new ArrayList<T>();
        for (int i = 0; i < start; i++) {
            items.add(resList.get(i));
        }
        ret.add(items);
        for (int i = 0; i < start; i++) {
            resList.remove(0);
        }
        int size = resList.size();
        if (size <= count && size > 0) { //数据量不足count指定的大小
            ret.add(resList);
        } else {
            int pre = size / count;
            int last = size % count;
            //前面pre个集合，每个大小都是count个元素
            for (int i = 0; i < pre; i++) {
                List<T> itemList = new ArrayList<T>();
                for (int j = 0; j < count; j++) {
                    itemList.add(resList.get(i * count + j));
                }
                ret.add(itemList);
            }
            //last的进行处理
            if (last > 0) {
                List<T> itemList = new ArrayList<T>();
                for (int i = 0; i < last; i++) {
                    itemList.add(resList.get(pre * count + i));
                }
                ret.add(itemList);
            }
        }
        return ret;
    }
}
