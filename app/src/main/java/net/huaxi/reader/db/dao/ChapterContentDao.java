package net.huaxi.reader.db.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.huaxi.reader.common.AppContext;
import net.huaxi.reader.db.OrmDataBaseHelper;
import net.huaxi.reader.db.model.ChapterContentTable;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.tools.commonlibs.tools.StringUtils;

public class ChapterContentDao {

    private static ChapterContentDao chaptercontentDao;
    private static Dao<ChapterContentTable, Integer> mDao;

    public static ChapterContentDao getInstance() {
        if (null == chaptercontentDao) {
            chaptercontentDao = new ChapterContentDao();
        }
        mDao = OrmDataBaseHelper.getDataBaseHelper(AppContext.getInstance()).getDao(ChapterContentTable.class);
        return chaptercontentDao;
    }

    public static ChapterContentDao getInstance(String uid) {
        if (null == chaptercontentDao) {
            chaptercontentDao = new ChapterContentDao();
        }
        mDao = OrmDataBaseHelper.getDataBaseHelper(AppContext.getInstance(), uid).getDao(ChapterContentTable.class);
        return chaptercontentDao;
    }


    private ChapterContentTable findChapterByCid(String cid, String bid) {
        if (cid == null) return null;
        ChapterContentTable chapter = null;
        try {
            List<ChapterContentTable> tables = mDao.queryBuilder().where().eq("c_sid", cid).and().eq("b_sid", bid).query();
            if (tables != null && tables.size() > 0) {
                for (Iterator<ChapterContentTable> iterator = tables.iterator(); iterator.hasNext(); ) {
                    ChapterContentTable chaptercontentTable = iterator.next();
                    if (chaptercontentTable != null && !StringUtils.isBlank(chaptercontentTable.getChapterId())) {
                        chapter = chaptercontentTable;
                        break;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chapter;
    }

    public List<ChapterContentTable> findChapterListByBid(String bid) {
        if (bid == null) return new ArrayList<ChapterContentTable>();
        List<ChapterContentTable> list = new ArrayList<ChapterContentTable>();
        try {
            list = mDao.queryForEq("b_sid", bid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private List<ChapterContentTable> findChapterListByMarch(ChapterContentTable table) {
        if (table == null) return new ArrayList<ChapterContentTable>();
        List<ChapterContentTable> list = new ArrayList<ChapterContentTable>();
        try {
            list = mDao.queryForMatching(table);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int updateChapter(ChapterContentTable table) {
        if (table == null || table.getChapterId() == null || table.getBookId() == null) return -1;
        int result = -1;
        try {
            if (table.getId() == 0) {
                ChapterContentTable cct = findChapterByCid(table.getChapterId(), table.getBookId());
                if (cct == null) return -1;
                int id = cct.getId();
                table.setId(id);
            }

            result = mDao.update(table);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 返回章节阅读状态
     * @param bookid
     * @param chapterid
     * @return
     */
    public boolean isReaded(String bookid, String chapterid) {
        ChapterContentTable table = findChapterByCid(chapterid, bookid);
        if (table != null && table.getIsReaded() == 1) {
            return true;
        }
        return false;
    }

    /**
     * 更新章节阅读状态
     * @param bookid
     * @param chapterid
     * @param isReaded  1:已读
     * @return
     */
    public int updateReadState(String bookid,String chapterid,int isReaded) {
        int result = -1;
        try {
            ChapterContentTable table = findChapterByCid(chapterid, bookid);
            if (table != null) {  //允许清空阅读记录
                table.setIsReaded(isReaded);
                result = mDao.update(table);
            }else{
                table = new ChapterContentTable();
                table.setBookId(bookid);
                table.setChapterId(chapterid);
                table.setIsReaded(isReaded);
                result = mDao.create(table);
            }
        } catch (Exception e) {
        }
        return result;
    }

    //根据章节删除记录
    private int deleteChapterByCid(String cid, String bid) {
        if (cid == null) return -1;
        int result = -1;
        try {
            DeleteBuilder<ChapterContentTable, Integer> deleteBuilder = mDao.deleteBuilder();
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
            DeleteBuilder<ChapterContentTable, Integer> deleteBuilder = mDao.deleteBuilder();
            deleteBuilder.where().eq("b_sid", bid);
            result = deleteBuilder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    //添加章节信息
    public int addChapter(ChapterContentTable table) {

        if (table == null || table.getChapterId() == null || table.getBookId() == null) return -1;
        int result = -1;
        try {
            ChapterContentTable ct = findChapterByCid(table.getChapterId(), table.getBookId());
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
    public int addChapterList(List<ChapterContentTable> tableList, String bid) {
        if (tableList == null || tableList.size() == 0) {
            return -1;
        }
        int result = -1;
        deleteChapterByBid(bid);
        try {
            result = mDao.create(tableList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
