package net.huaxi.reader.test;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.huawei.hms.api.ConnectionResult;
import com.huawei.hms.support.api.client.ResultCallback;
import com.huawei.hms.support.api.client.Status;
import com.huawei.hms.support.api.entity.pay.PayStatusCodes;
import com.huawei.hms.support.api.pay.HuaweiPay;
import com.huawei.hms.support.api.pay.PayResult;
import com.huawei.hms.support.api.pay.PayResultInfo;
import com.tools.commonlibs.activity.BaseActivity;
import com.tools.commonlibs.tools.LogUtils;

import net.huaxi.reader.R;
import net.huaxi.reader.book.BookContentSettings;
import net.huaxi.reader.common.EnterBookContent;
import net.huaxi.reader.db.dao.BookDao;
import net.huaxi.reader.db.dao.ChapterDao;
import net.huaxi.reader.db.model.BookTable;
import net.huaxi.reader.db.model.ChapterTable;
import net.huaxi.reader.thread.HuaWeiPayTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.huawei.hms.activity.BridgeActivity.EXTRA_RESULT;
import static net.huaxi.reader.thread.HuaWeiPayTask.REQUEST_HMS_RESOLVE_ERROR;

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




    /**
     * 支付接口调用的回调处理----------------------------------------------------------------------------
     * 只有当处理结果中的返回码为 PayStatusCodes.PAY_STATE_SUCCESS的时候，CP需要继续调用支付
     * 否则就需要处理支付失败结果
     */
    public  class PayResultCallback implements ResultCallback<PayResult> {
        private int requestCode;
        public PayResultCallback(int requestCode) {
            this.requestCode = requestCode;
        }
        @Override
        public void onResult(PayResult result) {
            //支付鉴权结果，处理result.getStatus()
            Status status = null ;
            status =  result.getStatus();
            if (PayStatusCodes.PAY_STATE_SUCCESS == status.getStatusCode()) {
                //当支付回调 返回码为0的时候，表明支付流程正确，CP需要调用startResolutionForResult接口来进来后续处理
                //支付会先判断华为帐号是否登录，如果未登录，会先提示用户登录帐号。之后才会进行支付流程
                try {
                    status.startResolutionForResult(TestActivity.this, requestCode);
                } catch (IntentSender.SendIntentException e) {
                    Log.e("huawei", "启动支付失败"+e.getMessage());
                }
            } else {
                Log.i("huawei", "支付失败，原因 :" + status.getStatusCode());
            }
        }
    }



    /**
     * 当用户未登录或者未授权，调用signin接口拉起对应的页面处理完毕后会将结果返回给当前activity处理
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //连接失败处理
        if(requestCode == REQUEST_HMS_RESOLVE_ERROR) {
            if(resultCode == Activity.RESULT_OK) {

                int result = data.getIntExtra(EXTRA_RESULT, 0);

                if(result == ConnectionResult.SUCCESS) {
                    Log.i(HuaWeiPayTask.TAG, "错误成功解决");
//                    if (!client.isConnecting() && !client.isConnected()) {
//                        client.connect();
//                    }
                } else if(result == ConnectionResult.CANCELED) {
                    Log.i(HuaWeiPayTask.TAG, "解决错误过程被用户取消");
                } else if(result == ConnectionResult.INTERNAL_ERROR) {
                    Log.i(HuaWeiPayTask.TAG, "发生内部错误，重试可以解决");
                    //CP可以在此处重试连接华为移动服务等操作，导致失败的原因可能是网络原因等
                } else {
                    Log.i(HuaWeiPayTask.TAG, "未知返回码");
                }
            } else {
                Log.i(HuaWeiPayTask.TAG, "调用解决方案发生错误");
            }
        } else if (requestCode == HuaWeiPayTask.REQ_CODE_PAY) {//连接成功
            //当返回值是-1的时候表明用户支付调用调用成功
            if (resultCode == Activity.RESULT_OK) {
                //获取支付完成信息
                PayResultInfo payResultInfo = HuaweiPay.HuaweiPayApi.getPayResultInfoFromIntent(data);
                if (payResultInfo != null) {
                    Map<String, Object> paramsa = new HashMap<>();
                    if (PayStatusCodes.PAY_STATE_SUCCESS == payResultInfo.getReturnCode()) {

                        paramsa.put("returnCode", payResultInfo.getReturnCode());
                        paramsa.put("userName", payResultInfo.getUserName());
                        paramsa.put("requestId", payResultInfo.getRequestId());
                        paramsa.put("amount", payResultInfo.getAmount());
                        paramsa.put("time", payResultInfo.getTime());

                        String orderId = payResultInfo.getOrderID();
                        if (!TextUtils.isEmpty(orderId)) {
                            paramsa.put("orderID", orderId);
                        }
                        String withholdID = payResultInfo.getWithholdID();
                        if (!TextUtils.isEmpty(withholdID)) {
                            paramsa.put("withholdID", withholdID);
                        }
                        String errMsg = payResultInfo.getErrMsg();
                        if (!TextUtils.isEmpty(errMsg)) {
                            paramsa.put("errMsg", errMsg);
                        }

                        String noSigna = HuaWeiPayTask.getNoSign(paramsa);
                        boolean success = HuaWeiPayTask.doCheck(noSigna, payResultInfo.getSign());

                        if (success) {
                            Log.i(HuaWeiPayTask.TAG, "支付/订阅成功");
                        } else {
                            //支付成功，但是签名校验失败。CP需要到服务器上查询该次支付的情况，然后再进行处理。
                            Log.i(HuaWeiPayTask.TAG, "支付/订阅成功，但是签名验证失败");
                        }

                        Log.i(HuaWeiPayTask.TAG, "商户名称: " + payResultInfo.getUserName());
                        if (!TextUtils.isEmpty(orderId)) {
                            Log.i(HuaWeiPayTask.TAG, "订单编号: " + orderId);
                        }
                        Log.i(HuaWeiPayTask.TAG, "支付金额: " + payResultInfo.getAmount());
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                        String time = payResultInfo.getTime();
                        if (time != null) {
                            try {
                                Date curDate = new Date(Long.valueOf(time));
                                String str = formatter.format(curDate);
                                Log.i(HuaWeiPayTask.TAG, "交易时间: " + str);
                            } catch (NumberFormatException e) {
                                Log.i(HuaWeiPayTask.TAG, "交易时间解析出错 time: " + time);
                            }
                        }
                        Log.i(HuaWeiPayTask.TAG, "商户订单号: " + payResultInfo.getRequestId());
                    } else if (PayStatusCodes.PAY_STATE_CANCEL == payResultInfo.getReturnCode()) {
                        //支付失败，原因是用户取消了支付，可能是用户取消登录，或者取消支付
                        Log.i(HuaWeiPayTask.TAG, "支付失败：用户取消" + payResultInfo.getErrMsg());
                    } else {
                        //支付失败，其他一些原因
                        Log.i(HuaWeiPayTask.TAG, "支付失败：" + payResultInfo.getErrMsg());
                    }
                } else {
                    //支付失败
                }
            } else {
                //当resultCode 为0的时候表明用户未登录，则CP可以处理用户不登录事件
                Log.i(HuaWeiPayTask.TAG, "resultCode为0, 用户未登录 CP可以处理用户不登录事件");
            }
        }
    }
}
