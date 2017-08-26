package net.huaxi.reader.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.android.volley.toolbox.RequestFuture;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tools.commonlibs.activity.BaseActivity;
import com.tools.commonlibs.cache.RequestQueueManager;
import com.tools.commonlibs.tools.FileUtils;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.PhoneUtils;
import com.tools.commonlibs.tools.StringUtils;

import net.huaxi.reader.R;
import net.huaxi.reader.common.CommonUtils;
import net.huaxi.reader.common.Constants;
import net.huaxi.reader.common.SharePrefHelper;
import net.huaxi.reader.common.URLConstants;
import net.huaxi.reader.common.UmengHelper;
import net.huaxi.reader.common.Utility;
import net.huaxi.reader.db.dao.BookDao;
import net.huaxi.reader.db.model.BookTable;
import net.huaxi.reader.https.GetRequest;
import net.huaxi.reader.https.ResponseHelper;
import net.huaxi.reader.https.XSKEY;
import net.huaxi.reader.model.SplashShowHelper;
import net.huaxi.reader.statistic.ReportUtils;
import net.huaxi.reader.thread.DownLoadPictureThread;
import net.huaxi.reader.util.UMEventAnalyze;
import net.huaxi.reader.util.listener.ScreenUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * taoyingfeng
 * 2016/1/14.
 */
public class SplashActivity extends BaseActivity {

    private ImageView splashIv;
    private Bitmap bitmap;
    private long DELAYTIMES = 2200;  //3秒钟.
    private volatile boolean waitFinished = false;
    private volatile boolean loadFinished = false;
    private volatile boolean isFirstStart = false;
    private volatile boolean isInitSexClassify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        a();
        ScreenUtils.fullScreen(this);
        tintManager.setStatusBarTintEnabled(false);
        setContentView(R.layout.activity_splash);
        splashIv = (ImageView) findViewById(R.id.splash);
        isFirstStart = SharePrefHelper.getIsFirstStart();
        isInitSexClassify = SharePrefHelper.isInitSexClassify();
        UMEventAnalyze.countEvent(this, UMEventAnalyze.SPLASH_PAGE);

    }

    public void a() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Log.e("uncaughtException", e.toString());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        new DownLoadPictureThread().start();
        myHandler.sendEmptyMessageDelayed(0, DELAYTIMES);
        new Thread() {
            @Override
            public void run() {
                UmengHelper.getInstance().addAlias();
                ReportUtils.setUserSceneTag(Constants.BUGLY_SCENE_TAG_START);
                clearChapterCache();       //clear book chapter online cache;
                loadRecommendBookFormNet();
                loadFinished = true;
                gotoMainActivity();
            }
        }.start();
    }

    @Override
    protected void onResume() {
        UMEventAnalyze.countEvent(this, UMEventAnalyze.SPLASH_SHOW);
        super.onResume();
        try { //打印内存分配
            ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
            System.err.println("app-memory :" + activityManager.getMemoryClass());
            System.err.println("app-max-memory :" + activityManager.getLargeMemoryClass());
        } catch (Exception err) {
            System.out.println(err.getMessage());
        }
        loadPicture();
    }

    private Handler myHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0) {
                waitFinished = true;
                gotoMainActivity();
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        System.out.println(keyCode + ">>>>>>>>>>>>");
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            myHandler.removeMessages(0);
            onKeyBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean allDone() {
        return loadFinished && waitFinished;
    }

    /**
     * 添加默认推荐书籍(整个流程必须是同步操作)
     */
    private void loadRecommendBookFormNet() {
        if (isFirstStart) {
            try {
                PhoneUtils.addShortcutToDesktop(SplashActivity.this, R.mipmap.ic_launch, R.string.app_name);
            } catch (Exception e) {
                e.printStackTrace();
            }

            boolean hasInitBookList = false;
            RequestFuture<JSONObject> future = RequestFuture.newFuture();
            String url = String.format(URLConstants.RECOMMEND_BOOK_URL, "bookshelvesrcm") + CommonUtils.getPublicGetArgs();   //书架推荐内容
            GetRequest request = new GetRequest(url, future, future);
            RequestQueueManager.addRequest(request, SplashActivity.class.getName());
            try {
                JSONObject response = future.get(5, TimeUnit.SECONDS);
                LogUtils.debug("默认书架加载返回的数据====" + response.toString());
                if (ResponseHelper.isSuccess(response)) {
                    JSONObject object = ResponseHelper.getVdata(response);
                    if (object != null) {
                        try {
                            JSONArray array = object.getJSONArray(XSKEY.KEY_LIST);
                            if (array != null) {
                                Type type = new TypeToken<ArrayList<BookTable>>() {
                                }.getType();
                                List<BookTable> bookTables = new Gson().fromJson(array.toString(), type);
                                if (bookTables != null && !bookTables.isEmpty()) {
                                    //默认用户没有添加推荐书籍
                                    for (BookTable bookTable : bookTables) {
                                        if (bookTable != null && StringUtils.isNotEmpty(bookTable.getBookId())) {
                                            bookTable.setIsOnShelf(1);
                                            bookTable.setAddToShelfTime(System.currentTimeMillis());
                                            bookTable.setHasNewChapter(1);
                                            bookTable.setCatalogUpdateTime(0);
                                            BookDao.getInstance(Constants.DEFAULT_USERID).addBook(bookTable);
                                        }
                                    }
                                    hasInitBookList = true;
                                    //记录添加的默认书籍
                                    SharePrefHelper.setDefaultShelfBook(bookTables);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (!hasInitBookList) {
                    //// FIXME: 2016/3/2 目前数据库书籍ID还没有确定，暂时去掉内置书。
                    addLocalBookData();
                }
            }
        }
    }


    private void addLocalBookData() {
//        String s = "{\n" +
//                "        \"bk_title\": \"诡罪档案\",\n" +
//                "        \"bk_keywords\": \"悬疑,墨绿青苔\",\n" +
//                "        \"bk_description\": " +
//                "\"茶城市著名企业家梁仕超被人谋杀，警方在调查中很快锁定目标，但没想到的是，这件案子远没有他们想象中那么简单…… 《诡罪档案》为知名悬疑作家墨绿青苔全新著作，由九域文学独家首发。\\r\\n " +
//                "《诡罪档案》为知名悬疑作家墨绿青苔全新著作，由九域文学独家首发.\",\n" +
//                "        \"bk_seo_title\": \"\",\n" +
//                "        \"bk_seo_keywords\": \"\",\n" +
//                "        \"bk_seo_description\": \"\",\n" +
//                "        \"bk_mid\": \"60044\",\n" +
//                "        \"bp_au_pname\": \"墨绿青苔\",\n" +
//                "        \"au_mid\": \"51\",\n" +
//                "        \"bk_cover_imgid\": \"http://www.jiuyuu.com/files/article/image/60/60044/60044s.jpg\",\n" +
//                "        \"bp_hire_flag\": \"0\",\n" +
//                "        \"bk_finish_flag\": \"0\",\n" +
//                "        \"bp_price\": \"5\",\n" +
//                "        \"bk_total_score\": \"7\",\n" +
//                "        \"bk_total_reads\": \"0\",\n" +
//                "        \"bk_total_words\": \"60万\",\n" +
//                "        \"bk_total_sub\": \"0\",\n" +
//                "        \"bk_total_cpts\": \"0\",\n" +
//                "        \"cat_name\": \"罪案推理\",\n" +
//                "        \"cat_mid\": \"3\",\n" +
//                "        \"bk_share_url\": \"http://www.jiuyuu.com/info/60044.html\"\n" +
//                "      }";
//        BookTable bookTable = new Gson().fromJson(s, BookTable.class);
//        bookTable.setIsOnShelf(1);
//        bookTable.setAddToShelfTime(System.currentTimeMillis());
//        bookTable.setHasNewChapter(1);
//        bookTable.setCatalogUpdateTime(0);
//        BookDao.getInstance().addBook(bookTable);
//
//        s = "{\n" +
//                "        \"bk_title\": \"夺命弯道\",\n" +
//                "        \"bk_keywords\": \"推理,刑侦,罪案\",\n" +
//                "        \"bk_description\": " +
//                "\"深冬雨夜，东风市发生一起车祸，一名女司机当场殒命。刑警江枫赶到现场，发现死者在车祸前六小时就已死亡。死人怎么会开车？江枫发誓要侦破此案，调查步步深入，当真相洞穿，他却像碰见了瘟疫，拼命想要逃避。他逃得了吗？　　人生总是很难以预料，你明明很努力想干一件事，结果却发生了另外一些事情。不可思议的犯罪动机，超乎想象的作案手法，拨开人性的迷雾，真相令人不敢触碰！\",\n" +
//                "        \"bk_seo_title\": \"\",\n" +
//                "        \"bk_seo_keywords\": \"\",\n" +
//                "        \"bk_seo_description\": \"\",\n" +
//                "        \"bk_mid\": \"60093\",\n" +
//                "        \"bp_au_pname\": \"姜钦峰\",\n" +
//                "        \"au_mid\": \"523\",\n" +
//                "        \"bk_cover_imgid\": \"http://www.jiuyuu.com/files/article/image/60/60093/60093s.jpg\",\n" +
//                "        \"bp_hire_flag\": \"0\",\n" +
//                "        \"bk_finish_flag\": \"0\",\n" +
//                "        \"bp_price\": \"5\",\n" +
//                "        \"bk_total_score\": \"7\",\n" +
//                "        \"bk_total_reads\": \"0\",\n" +
//                "        \"bk_total_words\": \"169万\",\n" +
//                "        \"bk_total_sub\": \"0\",\n" +
//                "        \"bk_total_cpts\": \"0\",\n" +
//                "        \"cat_name\": \"悬疑小说\",\n" +
//                "        \"cat_mid\": \"1\",\n" +
//                "        \"bk_share_url\": \"http://www.jiuyuu.com/info/60093.html\"\n" +
//                "      }";
//        bookTable = new Gson().fromJson(s, BookTable.class);
//        bookTable.setIsOnShelf(1);
//        bookTable.setAddToShelfTime(System.currentTimeMillis());
//        bookTable.setHasNewChapter(1);
//        bookTable.setCatalogUpdateTime(0);
//        BookDao.getInstance().addBook(bookTable);

//        s = "{\n" +
//                "        \"bk_title\": \"灵鼎\",\n" +
//                "        \"bk_keywords\": \"【小迁2013仙侠巨献】、【修仙】、【热血】、【情殇】\",\n" +
//                "        \"bk_description\": \"ps1：【推荐小迁自己的另一本东方玄幻类文《灵武》！】\\r\\nps2：灵鼎普通群号： " +
//                "169210236（接待新读者专用，帝仙阁老读者勿加！不想支持正版的读者勿加！）终极vip粉丝群（帝仙阁）：在普通群认证方可加入！\\r\\nps3：微博名：RN心碎梦思迁 微信号：dy14968 " +
//                "\\r\\n修仙一途，感天聚气，练气筑液，固液为丹，碎丹凝婴，通婴化神……\\r\\n泡妞界：每天一拜楚神人，泡妞给力长精神！\\r\\n修仙界：今日跟从楚妖孽，明日逆袭仙二代！\\r\\n综合评价：凶残，妖孽，奇葩，神人！\\r\\n" +
//                "这里有无穷无尽的新奇法宝，这里有数不胜数的太古灵兽，这里有奇幻玄妙的法诀灵术！人族，妖族，鬼族，族族纷争！道修，佛修，儒修，百家争鸣！\\r\\n光怪陆离，神秘无限，热血沸腾，战意滔天！\\r\\n" +
//                "且看一名落魄的少年，如何凭借一尊残破小鼎重踏修行之路，最终破解万古谜团，问鼎无上仙道！\\r\\n这不是一个草根强势崛起的故事，这是一个天才绝境逆袭的故事！\\r\\n修炼等级：练气——筑基——金丹——元婴——化神——炼虚？\",\n" +
//                "        \"bk_seo_title\": \"\",\n" +
//                "        \"bk_seo_keywords\": \"\",\n" +
//                "        \"bk_seo_description\": \"\",\n" +
//                "        \"bk_mid\": \"1011011456776104409700003854\",\n" +
//                "        \"bp_au_pname\": \"心碎梦思迁\",\n" +
//                "        \"au_mid\": \"1011731457015755657758007659\",\n" +
//                "        \"bk_cover_imgid\": \"http://cdn-image.xs.cn/90eb630af41a14c3e62d661b21838bb4.jpg@200w_267h_20q.jpg\",\n" +
//                "        \"bp_hire_flag\": \"0\",\n" +
//                "        \"bk_finish_flag\": \"0\",\n" +
//                "        \"bp_price\": \"5\",\n" +
//                "        \"bk_total_score\": \"8\",\n" +
//                "        \"bk_total_reads\": \"0\",\n" +
//                "        \"bk_total_words\": \"863万\",\n" +
//                "        \"bk_total_sub\": \"0\",\n" +
//                "        \"bk_total_cpts\": \"0\",\n" +
//                "        \"cat_name\": \"仙侠\",\n" +
//                "        \"cat_mid\": \"1011011016111809484641717642\",\n" +
//                "        \"bk_share_url\": \"http://rd.xs.cn/1006?bookid=1011011456776104409700003854\"\n" +
//                "      }";
//        bookTable = new Gson().fromJson(s, BookTable.class);
//        bookTable.setIsOnShelf(1);
//        bookTable.setAddToShelfTime(System.currentTimeMillis());
//        bookTable.setHasNewChapter(1);
//        bookTable.setCatalogUpdateTime(0);
//        BookDao.getInstance().addBook(bookTable);

    }

    private void loadPicture() {
        SplashShowHelper splashShowHelper = new SplashShowHelper(SplashActivity.this, splashIv);
        bitmap = splashShowHelper.loadPicture();
    }

    private void clearChapterCache() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FileUtils.deleteDir(new File(Utility.getCacheRootPath()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private synchronized void gotoMainActivity() {
        if (allDone() && !isFinishing() && !isFinishing()) {
            SharePrefHelper.setIsFirstStart(false);
//            if (isInitSexClassify) {
//                Intent it = new Intent(SplashActivity.this, MainActivity.class);
//                startActivity(it);
//            } else {
//                Intent it = new Intent(SplashActivity.this, SexClassifyActivity.class);
//                startActivity(it);
//            }
             Intent it = new Intent(SplashActivity.this, MainActivity.class);
             startActivity(it);

            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }
}
