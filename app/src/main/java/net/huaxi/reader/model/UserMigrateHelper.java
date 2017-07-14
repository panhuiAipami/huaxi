package net.huaxi.reader.model;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tools.commonlibs.activity.BaseActivity;
import com.tools.commonlibs.cache.RequestQueueManager;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.ViewUtils;
import net.huaxi.reader.adapter.XSAdapter;
import net.huaxi.reader.bean.MigrateBean;
import net.huaxi.reader.bean.MigrateUserBean;
import net.huaxi.reader.bean.User;
import net.huaxi.reader.common.URLConstants;
import net.huaxi.reader.common.XSNetEnum;
import net.huaxi.reader.https.ResponseHelper;
import net.huaxi.reader.statistic.ReportUtils;
import net.huaxi.reader.util.EncodeUtils;
import net.huaxi.reader.view.ListViewForScrollView;

import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.huaxi.reader.R;
import net.huaxi.reader.activity.LoginActivity;
import net.huaxi.reader.adapter.XSViewHolder;
import net.huaxi.reader.aidl.XSMigrateStore;
import net.huaxi.reader.common.CommonUtils;
import net.huaxi.reader.common.UserHelper;
import net.huaxi.reader.https.GetRequest;

/**
 * @Description: [ 账号迁移的逻辑处理 ]
 * @Author: [ Saud ]
 * @CreateDate: [ 16/6/13 13:36 ]
 * @UpDate: [ 16/6/13 13:36 ]
 * @Version: [ v1.0 ]
 */
public class UserMigrateHelper {

    private OnMigrateSuccessListener successListener;
    private Activity activity;
    private List<XSMigrateStore> mXSStoreList;
    private Set<XSMigrateStore> tempXSStoreSet = new HashSet<>();
    private LinkedList<String> tempCode = new LinkedList<>();
    private List<MigrateUserBean> migrateUsers = new ArrayList<>();
    private List<MigrateBean> resultList = new ArrayList<>();
    private String tabSp = "\n        ";
    private String enter = "\n";
    private String mUser = "";
    public static final int MIGRATE_REQUEST = 1;
    private boolean isMigrateSuccese = false;
    private boolean isMigrateError = false;
    private int indexCallBack = 0;
    private Thread mThread;
    private String[] migrateInfo = new String[]{"正在同步收藏作品...", "正在同步阅币...", "正在同步订购记录...", "正在同步打赏记录..."};
    private String errorMsg = "对不起，账号同步失败";

    public UserMigrateHelper(Activity activity, List<XSMigrateStore> _xsMigrateStoreList, OnMigrateSuccessListener successListener) {
        this.mXSStoreList = _xsMigrateStoreList;
        this.successListener = successListener;
        this.activity = activity;
    }

    /**
     * 显示需要迁移的对话框
     */
    public void startMigrate() {

        String count = "";
        String btn = "";
        boolean isLogin = UserHelper.getInstance().isLogin();
        if (isLogin) {//已登录
            User user = UserHelper.getInstance().getUser();
            if (mUser.equals(user.getNickname())) {
                return;
            }
            mUser = user.getNickname();
            count = String.format(activity.getString(R.string.migrate_login_count), mUser, tabSp, mXSStoreList.size());
            btn = "开始同步";
        } else {
            count = String.format(activity.getString(R.string.migrate_logout_count), tabSp, mXSStoreList.size());
            btn = "需要登录/注册新账号";
        }
        showDialog(count, btn, isLogin);
    }

    /**
     * 在登录的情况下，显示迁移对话框
     *
     * @param count
     * @param btn
     * @param isLogin
     */
    private void showDialog(final String count, final String btn, final boolean isLogin) {
        final MaterialDialog dialog = new MaterialDialog.Builder(activity)
                .customView(R.layout.dialog_migrate_user, true)
                .canceledOnTouchOutside(false)
                .build();
        //设置自定义布局的数据
        TextView tvCount = (TextView) dialog.getCustomView().findViewById(R.id.tv_dialog_count);
        ListViewForScrollView lvUserName = (ListViewForScrollView) dialog.getCustomView().findViewById(R.id.lv_user_name);
        final Button btnStartMigrate = (Button) dialog.getCustomView().findViewById(R.id.btn_start_migrate);
        tvCount.setText(count);
        btnStartMigrate.setText(btn);
        //设置对话框的大小
        dialog.getWindow().setLayout((int) (activity.getWindowManager().getDefaultDisplay().getWidth() * 0.94), ActionBar.LayoutParams.WRAP_CONTENT);
        btnStartMigrate.setClickable(false);
        btnStartMigrate.setAlpha(0.5f);
        lvUserName.setAdapter(new XSAdapter<XSMigrateStore>(activity, R.layout.item_migrate, mXSStoreList) {
            @Override
            public void convert(final XSViewHolder holder, final XSMigrateStore xsMigrateStore) {
                holder.setText(R.id.tv_name, xsMigrateStore.getUserName());
                final View rlView = holder.getView(R.id.rl_migrate_user);
                final CheckBox checkBox = (CheckBox) holder.getView(R.id.cb_migrate);
                if (holder.getPosition() == 0 && xsMigrateStore != null) {
                    if (xsMigrateStore.getExpireTime() > System.currentTimeMillis()) {
                        checkBox.setChecked(true);
                        btnStartMigrate.setClickable(true);
                        btnStartMigrate.setAlpha(1f);
                        tempXSStoreSet.add(xsMigrateStore);
                        rlView.setBackgroundResource(R.drawable.shape_migrate_select);
                    }
                }
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            tempXSStoreSet.add(xsMigrateStore);
                            rlView.setBackgroundResource(R.drawable.shape_migrate_select);
                        } else {
                            tempXSStoreSet.remove(xsMigrateStore);
                            rlView.setBackgroundResource(R.drawable.shape_migrate_normal);
                        }
                        if (tempXSStoreSet.size() == 0) {
                            btnStartMigrate.setClickable(false);
                            btnStartMigrate.setAlpha(0.5f);
                        } else {
                            btnStartMigrate.setClickable(true);
                            btnStartMigrate.setAlpha(1);
                        }
                    }
                });

                holder.setOnClickListener(R.id.rl_migrate_user, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //判断迁移码是否已经失效，
                        if (xsMigrateStore.getExpireTime() > System.currentTimeMillis()) {
                            boolean isSelect = checkBox.isChecked();
                            holder.setChecked(R.id.cb_migrate, !isSelect);
                        } else {
                            dialog.dismiss();
                            if (successListener != null) {//告诉老系统删除这个id的同步文件
                                successListener.onDelete(xsMigrateStore.getUserId());
                            }
                            showFailDialog(activity.getString(R.string.migrate_time_out));
                        }
                    }
                });
            }
        });
        btnStartMigrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLogin) {
                    if (tempXSStoreSet != null && tempXSStoreSet.size() > 0) {
                        ForeachMigrate();
                        showMigratingDialog();
                        dialog.dismiss();
                        System.out.println("UserMigrateHelper.onClick");
                    } else {
                        ViewUtils.toastShort("请选择一个有效的迁移账号");
                    }
                } else {
                    Intent intent = new Intent(activity, LoginActivity.class);
                    activity.startActivity(intent);
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    /**
     * 开始同步
     */
    private void ForeachMigrate() {
        //get请求发送迁移码 接口http://nirvana.xs.cn/api/nirvana/start
        int time = 0;
        indexCallBack = 0;
        resultList.clear();
        for (final XSMigrateStore store : tempXSStoreSet) {
            ((BaseActivity) activity).getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    sycMigrate(store.getMigrateCode());//开始同步
                }
            }, time);
            time = time + 2000;
        }
    }

    /**
     * 服务器请求同步账号
     *
     * @param migrateCode
     */
    private void sycMigrate(String migrateCode) {
        if (!tempCode.contains(migrateCode)) {//缓存同步的code
            tempCode.addFirst(migrateCode);
        }
        String url = String.format(URLConstants.MIGRATE_TODO, EncodeUtils.encodeString_UTF8(migrateCode) + CommonUtils.getPublicGetArgs());
        final GetRequest request = new GetRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtils.debug("评论的数据==" + response.toString());

                int errorid = ResponseHelper.getErrorId(response);
                if (errorid == XSNetEnum._VDATAERROR_MIGRATE_MIGRATE_RESULT_CODE.getCode()) {//正在迁移
                    System.out.println(response.toString());
                    errorMsg = XSNetEnum._VDATAERROR_MIGRATE_MIGRATE_RESULT_CODE.getMsg();
                    if (tempCode.size() > 0) {//延迟一秒重新请求一次
                        ((BaseActivity) activity).getHandler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                sycMigrate(tempCode.getLast());//循环去请求网络得到同步结果
                            }
                        }, 1000);
                    }
                } else if (errorid == XSNetEnum._VDATAERROR_MIGRATE_FINSH_RESULT_CODE.getCode()) {//迁移已经完成
                    errorMsg = XSNetEnum._VDATAERROR_MIGRATE_FINSH_RESULT_CODE.getMsg();
                    indexCallBack++;
                    Type type = new TypeToken<MigrateBean>() {
                    }.getType();
                    MigrateBean commentBeans = new Gson().fromJson(response.toString(), type);
                    if (tempCode.contains(commentBeans.getVdata().getNirvanaCode())) {
                        tempCode.remove(commentBeans.getVdata().getNirvanaCode());
                    }
                    resultList.add(commentBeans);
                } else if (errorid == XSNetEnum._VDATAERROR_MIGRATE_OUTTIME_RESULT_CODE.getCode()) {//迁移码已经过期
                    indexCallBack++;
                    errorMsg = XSNetEnum._VDATAERROR_MIGRATE_OUTTIME_RESULT_CODE.getMsg();
                } else {
                    indexCallBack++;
                    if (errorid == XSNetEnum._VDATAERROR_MIGRATE_UNFINISHED_RESULT_CODE.getCode()) {//该被迁移用户存在未完成的迁移任务
                        errorMsg = XSNetEnum._VDATAERROR_MIGRATE_UNFINISHED_RESULT_CODE.getMsg();
                    } else if (errorid == XSNetEnum._VDATAERROR_MIGRATE_MIGRATECODE_RESULT_CODE.getCode()) {//错误迁移码
                        errorMsg = XSNetEnum._VDATAERROR_MIGRATE_MIGRATECODE_RESULT_CODE.getMsg();
                    } else if (errorid == XSNetEnum._VDATAERROR_MIGRATE_UMID_RESULT_CODE.getCode()) {//当前用户umid与迁移码正在迁移用户umid不符合
                        errorMsg = XSNetEnum._VDATAERROR_MIGRATE_UMID_RESULT_CODE.getMsg();
                    }
                }
//                ViewUtils.toastLong(errorMsg);
                LogUtils.debug("UserMigrateHelper.onResponse" + errorMsg);
                if (tempXSStoreSet.size() == indexCallBack) {//全部账号同步全部结束
                    tempCode.clear();
                    isMigrateSuccese = true;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ReportUtils.reportError(new Throwable(error.getMessage()));
                LogUtils.debug("网络错误");
            }
        }

        );
        RequestQueueManager.addRequest(request);
    }

    /**
     * 显示正在同步的进度条
     */
    private void showMigratingDialog() {
        //显示同步进度对话框
        //设置自定义布局的数据
        MaterialDialog migratingDialog = new MaterialDialog.Builder(activity)
                .canceledOnTouchOutside(false)
                .customView(R.layout.dialog_migrate_progress, true)
                .cancelable(false)
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (mThread != null) {
                            mThread.interrupt();
                        }
                        showFinishDialog();
                    }
                })
                .showListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        final MaterialDialog dialog = (MaterialDialog) dialogInterface;
                        //设置自定义布局的数据

                        final TextView tvMigrating = (TextView) dialog.getCustomView().findViewById(R.id.tv_progress);
                        final ProgressBar pbMigrate = (ProgressBar) dialog.getCustomView().findViewById(R.id.pb_migrate);
                        startThread(new Runnable() {
                            @Override
                            public void run() {
                                int pro = 0;
                                while (pbMigrate.getProgress() != pbMigrate.getMax() && !Thread.currentThread().isInterrupted()) {
                                    if (dialog.isCancelled()) {
                                        break;
                                    }
                                    if (isMigrateError) {
                                        dialog.cancel();
                                        break;
                                    }
                                    long stime = 300;
                                    if (isMigrateSuccese) {
                                        stime = 50;
                                    }
                                    try {
                                        Thread.sleep(stime);
                                    } catch (InterruptedException e) {
                                        break;
                                    }
                                    pro++;
                                    if (pro > 100) {
                                        pro = 100;
                                    }
                                    final int finalPro = pro;
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            pbMigrate.setProgress(finalPro);
                                            tvMigrating.setText(getProgressInfo(finalPro));
                                        }
                                    });
                                }

                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mThread = null;
                                        dialog.cancel();
                                        ViewUtils.toastLong(errorMsg);
                                    }
                                });

                            }
                        });
                    }
                })
                .build();

        migratingDialog.show();
        System.out.println("UserMigrateHelper.showMigratingDialog");

    }


    /**
     * 更具服务器返回内容状态，提示不同的对话框
     */
    private void showFinishDialog() {
        if (resultList.size() == tempXSStoreSet.size() && resultList.size() != 0) {//全部成功
            showSuccessDialog();
        } else if (resultList.size() == 0) {//没有一个成功的
            showFailDialog(activity.getString(R.string.migrate_fail));
        } else {//部分账号同步成功
            showQuasiSuccess();
        }
    }


    /**
     * 显示迁移成功的对户口
     */
    private void showSuccessDialog() {
        SuccessMigrateInfo successMigrateInfo = new SuccessMigrateInfo().invoke();
        int favorCounts = successMigrateInfo.getFavorCounts();
        int remainCoin = successMigrateInfo.getRemainCoin();
        int xsbCoin = successMigrateInfo.getXsbCoin();
        int subChapterCount = successMigrateInfo.getSubChapterCount();
        int rewarCounts = successMigrateInfo.getRewarCounts();
        final String count = "共为您当前账号添加：" + tabSp + favorCounts + "本收藏作品" + tabSp + remainCoin + "阅币+" + xsbCoin + "阅币奖励" + tabSp + subChapterCount + "条订购记录" + tabSp + rewarCounts + "条打赏记录";
        final MaterialDialog dialog = new MaterialDialog.Builder(activity)
                .customView(R.layout.dialog_migrate_succes, true)
                .build();
        //设置自定义布局的数据
        TextView tvCount = (TextView) dialog.getCustomView().findViewById(R.id.tv_dialog_count);
        Button btnRead = (Button) dialog.getCustomView().findViewById(R.id.btn_start_read);
        tvCount.setText(count);
        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    /**
     * 一部分成功
     */
    private void showQuasiSuccess() {
        migrateUsers.clear();
        SuccessMigrateInfo successMigrateInfo = new SuccessMigrateInfo().invoke();
        int favorCounts = successMigrateInfo.getFavorCounts();
        int remainCoin = successMigrateInfo.getRemainCoin();
        int xsbCoin = successMigrateInfo.getXsbCoin();
        int subChapterCount = successMigrateInfo.getSubChapterCount();
        int rewarCounts = successMigrateInfo.getRewarCounts();
        //处理成可以显示的数据
        for (XSMigrateStore store : tempXSStoreSet) {
            MigrateUserBean mUB = new MigrateUserBean();
            String reqUser = store.getUserName().trim();
            mUB.setUserName(reqUser);
            mUB.setSuccess(false);
            for (MigrateBean result : resultList) {
                String tempUser = result.getVdata().getUserName().trim();
                if (tempUser.equals(reqUser)) {
                    mUB.setSuccess(true);
                    break;
                }
            }
            migrateUsers.add(mUB);
        }

        final String count = "本次共为您添加：" + enter + favorCounts + "本收藏作品" + enter + remainCoin + "阅币+" + xsbCoin + "阅币奖励" + enter + subChapterCount + "条订购记录" + enter + rewarCounts + "条打赏记录";
        final MaterialDialog dialog = new MaterialDialog.Builder(activity)
                .customView(R.layout.dialog_migrate_quasi, true)
                .build();
        //设置自定义布局的数据
        TextView tvCount = (TextView) dialog.getCustomView().findViewById(R.id.tv_dialog_count);
        Button btnRead = (Button) dialog.getCustomView().findViewById(R.id.btn_start_read);
        ListViewForScrollView lvFailName = (ListViewForScrollView) dialog.getCustomView().findViewById(R.id.lv_user_name);
        if (tempXSStoreSet.size() > resultList.size()) {
            lvFailName.setVisibility(View.VISIBLE);
        } else {
            lvFailName.setVisibility(View.GONE);
        }
        tvCount.setText(count);
        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        lvFailName.setAdapter(new XSAdapter<MigrateUserBean>(activity, R.layout.item_migrate_fail, migrateUsers) {
            @Override
            public void convert(XSViewHolder holder, MigrateUserBean bean) {
                holder.setText(R.id.tv_name, bean.getUserName());
                if (bean.isSuccess()) {
                    holder.setImageResource(R.id.iv_is_fail, R.mipmap.migrate_quasi_success);
                    holder.setText(R.id.tv_is_fail, "已同步");
                } else {
                    holder.setImageResource(R.id.iv_is_fail, R.mipmap.migrate_quasi_fail);
                    holder.setTextColorRes(R.id.tv_is_fail, R.color.c01_themes_color);
                    holder.setTextColorRes(R.id.tv_name, R.color.c01_themes_color);
                    holder.setText(R.id.tv_is_fail, "同步失败");
                }
            }
        });
        dialog.show();
    }

    /**
     * 同步失败的提示信息
     */
    private void showFailDialog(String count) {
        final MaterialDialog dialog = new MaterialDialog.Builder(activity)
                .customView(R.layout.dialog_migrate_fail, true)
                .build();
        //设置自定义布局的数据
        Button btnRestart = (Button) dialog.getCustomView().findViewById(R.id.btn_restart);
        TextView textView = (TextView) dialog.getCustomView().findViewById(R.id.tv_migrate_count);
        textView.setText(count);
        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                openOtherClient();
            }
        });
        dialog.show();
    }


    //假数据
    private String getProgressInfo(int progress) {
        String progressInfo = "";
        if (progress > 0 && progress <= 25) {
            progressInfo = migrateInfo[0];
        } else if (progress > 25 && progress <= 50) {
            progressInfo = migrateInfo[1];
        } else if (progress > 50 && progress <= 75) {
            progressInfo = migrateInfo[2];
        } else if (progress >= 75) {
            progressInfo = migrateInfo[3];
        }
        return progressInfo + "（" + progress + "%）";
    }

    private void startThread(Runnable run) {
        if (mThread != null) {
            mThread.interrupt();
        }
        mThread = new Thread(run);
        mThread.start();
    }


    private void openOtherClient() {
        PackageManager manager = activity.getPackageManager();
        Intent i = manager.getLaunchIntentForPackage("com.xs.cn");
        if (i != null) {
            i.addCategory(Intent.CATEGORY_LAUNCHER);
        }
        activity.startActivity(i);
    }

    public interface OnMigrateSuccessListener {

        void onDelete(String uid);

    }

    private class SuccessMigrateInfo implements Serializable {
        private static final long serialVersionUID = 5434403212437933865L;
        private int mFavorCounts;
        private int mRemainCoin;
        private int mXsbCoin;
        private int mSubChapterCount;
        private int mRewarCounts;

        public int getFavorCounts() {
            return mFavorCounts;
        }

        public int getRemainCoin() {
            return mRemainCoin;
        }

        public int getXsbCoin() {
            return mXsbCoin;
        }

        public int getSubChapterCount() {
            return mSubChapterCount;
        }

        public int getRewarCounts() {
            return mRewarCounts;
        }

        public SuccessMigrateInfo invoke() {
            mFavorCounts = 0;
            mRemainCoin = 0;
            mXsbCoin = 0;
            mSubChapterCount = 0;
            mRewarCounts = 0;
            for (int i = 0; i < resultList.size(); i++) {
                MigrateBean migrateBean = resultList.get(i);
                String userid = migrateBean.getVdata().getUserid();
                if (successListener != null) {
                    successListener.onDelete(userid);
                }
                try {
                    mFavorCounts += Integer.parseInt(migrateBean.getVdata().getFavorCounts());
                    mRemainCoin += Integer.parseInt(migrateBean.getVdata().getRemainCoin());
                    mXsbCoin += Integer.parseInt(migrateBean.getVdata().getXsbCoin());
                    mSubChapterCount += Integer.parseInt(migrateBean.getVdata().getSubChapterCount());
                    mRewarCounts += Integer.parseInt(migrateBean.getVdata().getRewardCounts());

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            return this;
        }
    }
}
