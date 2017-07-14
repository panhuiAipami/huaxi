package net.huaxi.reader.test;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.tools.commonlibs.activity.BaseActivity;
import com.tools.commonlibs.tools.LogUtils;
import net.huaxi.reader.book.BookContentSettings;
import net.huaxi.reader.common.EnterBookContent;
import net.huaxi.reader.db.model.BookTable;

import net.huaxi.reader.R;

import net.huaxi.reader.db.dao.BookDao;
import net.huaxi.reader.db.dao.ChapterDao;
import net.huaxi.reader.db.model.ChapterTable;

public class TestActivity extends BaseActivity {
    Button readTest = null;
    Button recover = null;
    Button catalog = null;

    public static final String BOOKID = "1011011016111707110138543971";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        recover = (Button) findViewById(R.id.recover_data);
        recover.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        insertBook();
                    }
                });
                t.start();
            }
        });
        readTest = (Button) findViewById(R.id.readpage_test);
        readTest.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EnterBookContent.openBookContent(getActivity(), TestActivity.BOOKID);
            }
        });

        catalog = (Button) findViewById(R.id.insert_catalog);
        catalog.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        insertCatalog();
                        insertBook();
                    }
                });
                t.start();
            }
        });

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        System.out.println(" density = " + dm.density + " widthPixels=" + dm.widthPixels + " heightPixels= " + dm.heightPixels);
    }

    public void insertBook() {
        LogUtils.debug("================写入测试书籍信息==========================");
        BookTable bookTable = new BookTable();
        bookTable.setBookId(BOOKID);
        bookTable.setName("特种兵在都市");
        bookTable.setAuthorName("夜十三");
        bookTable.setLastReadChapter("1011011016111707110285641485");
        bookTable.setLastReadLocation(0);
        bookTable.setIsMonthly(1);
        BookDao.getInstance().addBook(bookTable);
//
//        PagingManager.getSingleton().resetPageMap();

//        BookCatalogThreadLoader help = new BookCatalogThreadLoader(this, new onCatalogLoadFinished() {
//            @Override
//            public void onFinished(List<ChapterTable> chapterTables) {
//
//            }
//        });
//        help.getData("1011011016111707022803192859");
//        help.getData("123");
    }

    private void insertCatalog() {
        long startTime = System.currentTimeMillis();

        ChapterTable chapterTable = new ChapterTable();
        chapterTable.setBookId(BOOKID);
        chapterTable.setChapterId("1011011016111707110285641485");
        chapterTable.setChapterNo(1);
        chapterTable.setTotalWords(1293);
        chapterTable.setName("001章 引子");
        //// FIXME: 2016/1/4 测试数据没有章节大小数据，在判断第一张和最后一张的时候可能会用到
        ChapterDao.getInstance().addChapter(chapterTable);

        chapterTable = new ChapterTable();
        chapterTable.setBookId(BOOKID);
        chapterTable.setChapterId("1011011016111707110294932190");
        chapterTable.setChapterNo(2);
        chapterTable.setTotalWords(1530);
        chapterTable.setName("002章 韩国美女");
        //// FIXME: 2016/1/4 测试数据没有章节大小数据，在判断第一张和最后一张的时候可能会用到
        ChapterDao.getInstance().addChapter(chapterTable);

        chapterTable = new ChapterTable();
        chapterTable.setBookId(BOOKID);
        chapterTable.setChapterId("1011011016111707110306661330");
        chapterTable.setChapterNo(3);
        chapterTable.setTotalWords(1083);
        chapterTable.setName("003章 机场遇美");
        //// FIXME: 2016/1/4 测试数据没有章节大小数据，在判断第一张和最后一张的时候可能会用到
        ChapterDao.getInstance().addChapter(chapterTable);

        chapterTable = new ChapterTable();
        chapterTable.setBookId(BOOKID);
        chapterTable.setChapterId("1011011016111707110310109444");
        chapterTable.setChapterNo(4);
        chapterTable.setTotalWords(1087);
        chapterTable.setName("004章 上海四大美女");
        //// FIXME: 2016/1/4 测试数据没有章节大小数据，在判断第一张和最后一张的时候可能会用到
        ChapterDao.getInstance().addChapter(chapterTable);

        chapterTable = new ChapterTable();
        chapterTable.setBookId(BOOKID);
        chapterTable.setChapterId("1011011016111707110313402705");
        chapterTable.setChapterNo(5);
        chapterTable.setTotalWords(1068);
        chapterTable.setName("005章 劫匪");
        //// FIXME: 2016/1/4 测试数据没有章节大小数据，在判断第一张和最后一张的时候可能会用到
        ChapterDao.getInstance().addChapter(chapterTable);

        LogUtils.debug("写入目录耗时 :" + (System.currentTimeMillis() - startTime) + " 毫秒");
    }

    @Override
    protected void onStart() {
        super.onStart();
        BookContentSettings settings = BookContentSettings.getInstance();
        LogUtils.debug("阅读界面的参数");
        LogUtils.debug(" screenWidth =" + settings.getScreenWidth() + " screenHeight=" + settings.getScreenHeight());
        LogUtils.debug(" marginTop = " + settings.getMarginTop() + " marginBotton =" + settings.getMarginBottom());
        LogUtils.debug(" marginLeft = " + settings.getMarginLeft() + " marginRight =" + settings.getMarginRight());
        LogUtils.debug(" marginToTitle = " + settings.getTopToTitle() + " TextSize= " + settings.getTextSize() + " LineSpace=" + settings
                .getLineSpace());

    }


}
