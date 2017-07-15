package net.huaxi.reader.db.dao;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.tools.commonlibs.tools.StringUtils;
import net.huaxi.reader.common.AppContext;
import net.huaxi.reader.db.OrmDataBaseHelper;
import net.huaxi.reader.db.model.BookTable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BookDao {
    private static BookDao bookDao;
    private static Dao<BookTable, Integer> mDao;

    public static BookDao getInstance() {
        if (null == bookDao) {
            bookDao = new BookDao();
        }
        mDao = OrmDataBaseHelper.getDataBaseHelper(AppContext.getInstance()).getDao(BookTable.class);
        return bookDao;
    }

    public static BookDao getInstance(String uid) {
        if (null == bookDao) {
            bookDao = new BookDao();
        }
        mDao = OrmDataBaseHelper.getDataBaseHelper(AppContext.getInstance(), uid).getDao(BookTable.class);
        return bookDao;
    }

    public List<BookTable> findAllBook() {
        List<BookTable> list = null;
        try {
            list = mDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<BookTable> findRecentLyBooks() {
        List<BookTable> list = new ArrayList<BookTable>();
        try {
            list = mDao.queryBuilder().orderBy("b_last_read_date", false).limit(50L).where().notIn("b_last_read_date", 0).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<BookTable> findShelfBooks() {
        List<BookTable> list = new ArrayList<BookTable>();
        try {
            list = mDao.queryBuilder().orderBy("b_add_to_shelf_time", false).where().not().eq("b_is_on_shelf", 0).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 寻找最后一次读取的章节.(推荐使用DataSourceManager中的getLastReadRecord())
     *
     * @param bookId
     * @return
     */
//    public String findLastReadChapterId(String bookId) {
//        BookTable result = findBook(bookId);
//        if (result != null && StringUtils.isNotEmpty(result.getLastReadChapter())) {
//            return result.getLastReadChapter();
//        }
//        return "";
//    }
    public BookTable findBook(String bookId) {
        BookTable result = null;
        try {
            List<BookTable> tables = mDao.queryBuilder().where().eq("b_sid", bookId).query();
            if (tables != null && tables.size() > 0) {
                for (Iterator<BookTable> iterator = tables.iterator(); iterator.hasNext(); ) {
                    BookTable bookTable = iterator.next();
                    if (bookTable != null && !StringUtils.isBlank(bookTable.getBookId())) {
                        result = bookTable;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 判断bid是否相同，相同则更新，不同则添加
     *
     * @param book
     * @return
     */
    public int addBook(BookTable book) {
        if (null == book || book.getBookId() == null) {
            return -1;
        }
        int result = -1;
        int i = hasKey(book.getBookId());
        if (i == 1) {
            result = updateBook(book);
        } else if (i == 0) {
            try {
                result = mDao.create(book);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public int hasKey(String bookid) {
        if (bookid == null) {
            return -1;
        }
        int b = 0;
        BookTable book = findBook(bookid);
        if (book != null) {
            b = 1;
        }
        return b;
    }

    public int updateBook(BookTable book) {
        if (book == null || book.getBookId() == null) return -1;
        int result = -1;
        try {
            BookTable bt = findBook(book.getBookId());
            if (bt == null) return -1;
            int id = bt.getId();
            book.setId(id);
            result = mDao.update(book);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public int clearLastReadDate() {
        int result = -1;
        try {
            result = mDao.updateBuilder().updateColumnValue("b_last_read_date", 0).update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 更新阅读记录
     *
     * @param bookId    书籍ＩＤ
     * @param chapterId 章节ID
     * @param location  　阅读章节Offset
     * @param percent   阅读百分比.
     * @return
     */
    public int updateLastReadLocation(String bookId, String chapterId, int location, float percent) {
        int result = -1;
        try {
            BookTable table = findBook(bookId);
            if (table != null) {  //允许清空阅读记录
                table.setLastReadChapter(chapterId);
                table.setLastReadLocation(location);
                table.setReadPercentage(percent);
                table.setLastReadDate(System.currentTimeMillis());
            }
            result = mDao.update(table);
        } catch (Exception e) {
        }
        return result;
    }

    //	根据bookid删除书,只是修改isonshelf字段
    public int deleteBook(List<String> ids) {
        int result = -1;
//        DeleteBuilder<BookTable, Integer> builder = mDao.deleteBuilder();
        UpdateBuilder<BookTable, Integer> builder = null;
        try {
            builder = mDao.updateBuilder().updateColumnValue("b_is_on_shelf", 0).updateColumnValue("b_add_to_shelf_time", 0L);
            builder.where().notIn("b_sid", ids);
            result = builder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public int deleteAllBook() {
        int result = -1;
        DeleteBuilder<BookTable, Integer> builder = mDao.deleteBuilder();
        try {
            result = builder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public float getReadPercent(String bookId) {
        float result = 0;
        try {
            BookTable table = findBook(bookId);
            if (table != null) {
                result = table.getReadPercentage();
            }
        } catch (Exception e) {
        }
        return result;
    }


}
