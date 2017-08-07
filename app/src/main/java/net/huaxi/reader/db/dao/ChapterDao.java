package net.huaxi.reader.db.dao;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.StringUtils;

import net.huaxi.reader.common.AppContext;
import net.huaxi.reader.db.OrmDataBaseHelper;
import net.huaxi.reader.db.model.ChapterTable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChapterDao {
    private static ChapterDao chapterDao;
    private static Dao<ChapterTable, Integer> mDao;

    public static ChapterDao getInstance() {
        if (null == chapterDao) {
            chapterDao = new ChapterDao();
        }
        mDao = OrmDataBaseHelper.getDataBaseHelper(AppContext.getInstance()).getDao(ChapterTable.class);
        return chapterDao;
    }

    public static ChapterDao getInstance(String uid) {
        if (null == chapterDao) {
            chapterDao = new ChapterDao();
        }
        mDao = OrmDataBaseHelper.getDataBaseHelper(AppContext.getInstance(), uid).getDao(ChapterTable.class);
        return chapterDao;
    }

    /**
     * //根据章节id查询对应章节
     *
     * @param cid 章节ID
     * @param bid 书籍ID
     * @return
     */
    public ChapterTable findChapterByChapterId(String cid, String bid) {
        if (cid == null) return null;
        ChapterTable chapter = null;
        try {
            List<ChapterTable> tables = mDao.queryBuilder().where().eq("c_sid", cid).and().eq("b_sid", bid).query();
            if (tables != null && tables.size() > 0) {
                for (Iterator<ChapterTable> iterator = tables.iterator(); iterator.hasNext(); ) {
                    ChapterTable chapterTable = iterator.next();
                    if (chapterTable != null && !StringUtils.isBlank(chapterTable.getChapterId())) {
                        chapter = chapterTable;
                        break;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chapter;
    }

    public List<ChapterTable> findChapterListByBid(String bid) {
        if (bid == null) return new ArrayList<ChapterTable>();
        List<ChapterTable> list = new ArrayList<ChapterTable>();
        try {
            QueryBuilder<ChapterTable, Integer> queryBuilder = mDao.queryBuilder();
            queryBuilder.where().eq("b_sid", bid);
            queryBuilder.orderBy("c_chapter_no", true);
            list = queryBuilder.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 本地目录总数。
     * @param bid
     * @return
     */
    public int chapterCount(String bid) {
        List<ChapterTable> chapterTableList = findChapterListByBid(bid);
        if(chapterTableList != null && chapterTableList.size()> 0){
            return chapterTableList.size();
        }else{
            return 0;
        }
    }

    private List<ChapterTable> findChapterListByMarch(ChapterTable table) {
        if (table == null) return new ArrayList<ChapterTable>();
        List<ChapterTable> list = new ArrayList<ChapterTable>();

        try {
            list = mDao.queryForMatching(table);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public int updateChapter(ChapterTable table) {
        if (table == null || table.getChapterId() == null || table.getBookId() == null) return -1;
        int result = -1;
        try {
            if (table.getId() == 0) {
                ChapterTable ct = findChapterByChapterId(table.getChapterId(), table.getBookId());
                if (ct == null) return -1;
                int id = ct.getId();
                table.setId(id);
            }
            result = mDao.update(table);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    //根据章节删除记录
    private int deleteChapterByCid(String cid, String bid) {
        if (cid == null) return -1;
        int result = -1;
        try {
            DeleteBuilder<ChapterTable, Integer> deleteBuilder = mDao.deleteBuilder();
            deleteBuilder.where().eq("b_sid", bid).eq("c_sid", cid);
            result = deleteBuilder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    //根据书籍删除记录
    public int deleteChapterByBid(String bid) {
        if (bid == null) return -1;
        int result = -1;
        try {
            DeleteBuilder<ChapterTable, Integer> deleteBuilder = mDao.deleteBuilder();
            deleteBuilder.where().eq("b_sid", bid);
            result = deleteBuilder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    //删除所有章节信息内容
    public int clearCache() {
        int result = -1;

        DeleteBuilder<ChapterTable, Integer> deleteBuilder = mDao.deleteBuilder();
        try {
            result = deleteBuilder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    //添加章节信息,若是同一章则修改
    public int addChapter(ChapterTable table) {
        if (table == null || table.getChapterId() == null || table.getBookId() == null) return -1;
        int result = -1;
        try {
            ChapterTable ct = findChapterByChapterId(table.getChapterId(), table.getBookId());
            if (ct == null) {
                result = mDao.create(table);
            } else {
                result = updateChapter(table);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    //批量插入章节语句
    public int addChapterList(List<ChapterTable> tableList, String bid) {
        if (tableList == null || tableList.size() == 0) {
            return -1;
        }
        int result = -1;
        long t1 = System.currentTimeMillis();
        int temp = deleteChapterByBid(bid);
        long t2 = System.currentTimeMillis();
        long t3 = 0;
        System.err.println(" 删除 " + temp + " 条记录");
        try {
            result = mDao.create(tableList);
            t3 = System.currentTimeMillis();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        LogUtils.debug("t2-t1==" + (t2 - t1));
        LogUtils.debug("t3-t2==" + (t3 - t2));
        LogUtils.debug("t3-t1==" + (t3 - t1));
        return result;
    }

}
