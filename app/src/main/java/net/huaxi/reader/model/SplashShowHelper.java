package net.huaxi.reader.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;
import com.tools.commonlibs.tools.ImageUtils;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.MD5Utils;
import com.tools.commonlibs.tools.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.huaxi.reader.R;
import net.huaxi.reader.bean.SplashBean;
import net.huaxi.reader.common.Constants;
import net.huaxi.reader.common.SharePrefHelper;
import net.huaxi.reader.https.ResponseHelper;

/**
 * Created by ZMW on 2016/4/7.
 */
public class SplashShowHelper {
    public static final int DISPLAYTYPE_BY_TIME = 0;

    public static final int EFFECT_DEFAULT = 0;
    public static final int EFFECT_ALPHA = 1;
    Context activity;
    ImageView imageview;
    private int effect;//显示效果

    public SplashShowHelper(Context context, ImageView imageview) {
        this.activity = context;
        this.imageview = imageview;
    }

    public JSONObject getSplashResponse() {
        String sr = SharePrefHelper.getSplashResponse();
        if (sr == null) {
            return null;
        }
        try {
            LogUtils.debug("SplashResponse==" + sr);
            return new JSONObject(sr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Bitmap loadPicture() {
        Bitmap bitmap = null;
        // TODO: 2016/4/12 可以添加缓存，存需要显示的bean
        JSONObject response = getSplashResponse();
        if (response != null) {
            LogUtils.debug("splash=getSplashResponse===" + response.toString());
            bitmap = getBitmapByResponse(response);
        }
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(activity.getResources(), R.mipmap.splash);
        }

        setImageViewBitmap(bitmap);
        return bitmap;
    }

    private Bitmap getBitmapByResponse(JSONObject response) {
        Bitmap bitmap = null;
        if (response == null) {
            bitmap = BitmapFactory.decodeResource(activity.getResources(), R.mipmap.splash);
            return bitmap;
        }
        int displaytype = getDisplayType(response);
        if (displaytype == DISPLAYTYPE_BY_TIME) {
            List<SplashBean> sbs = getSplashBeansByDisplaytype(response, displaytype);
            if (sbs != null && sbs.size() >= 0) {
                Bitmap b = getBitmapByTime(sbs);
                if (b != null) {
                    bitmap = b;
                }
            }
        }
        return bitmap;
    }

    private void setImageViewBitmap(Bitmap bitmap) {
        if (imageview == null) {
            return;
        }
        if (effect == EFFECT_DEFAULT) {
            imageview.setImageBitmap(bitmap);
            imageview.setVisibility(View.VISIBLE);
        } else if (effect == EFFECT_ALPHA) {
            imageview.setImageBitmap(bitmap);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(imageview, "alpha", 0f, 1f).setDuration(2000);
            objectAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    imageview.setVisibility(View.VISIBLE);
                }
            });
            objectAnimator.start();
        }
    }

    //获取需要显示的url的地址
    public void getBitmapUrl() {
        Bitmap bitmap = null;
        JSONObject response = getSplashResponse();
        bitmap = getBitmapByResponse(response);
        if (bitmap != null) {
            bitmap.recycle();
        }
    }

    private Bitmap getBitmapByTime(List<SplashBean> sbs) {
        Bitmap bitmap = null;
        Collections.sort(sbs, new Comparator<SplashBean>() {
            @Override
            public int compare(SplashBean lhs, SplashBean rhs) {
                return (int) (rhs.getStarttime() - lhs.getStarttime());
            }
        });
        for (int i = 0; i < sbs.size(); i++) {
            SplashBean b = sbs.get(i);
            String path = Constants.XSREADER_SPLASH_IMGCACHE + MD5Utils.MD5(b.getUrl()) + "." + StringUtils.getImgUrlExt(b.getUrl());
            bitmap = getBitmapByPath(path);
            if (bitmap != null) {
                effect = b.getEffect();
                LogUtils.debug("splsh===Bean====" + b);
                break;
            }
        }
        return bitmap;
    }

    private Bitmap getBitmapByPath(String path) {
        return ImageUtils.getBitmapWithPath(path);
    }

    //返回符合要求的splashbean
    private List<SplashBean> getSplashBeansByDisplaytype(JSONObject response, int displaytype) {
        List<SplashBean> sbs = getSplashBeans(response);
        if (response == null) {
            return null;
        }
        if (displaytype == DISPLAYTYPE_BY_TIME) {
            return getSplashBeansByTime(sbs);
        }
        return null;
    }

    private List<SplashBean> getSplashBeansByTime(List<SplashBean> sbs) {
        List<SplashBean> response = new ArrayList<SplashBean>();
        for (SplashBean sb : sbs) {
            if (sb.getType() == DISPLAYTYPE_BY_TIME) {
                if (System.currentTimeMillis() > sb.getStarttime() * 1000 && (sb.getEndtime() == 0 || System.currentTimeMillis() < sb
                        .getEndtime() * 1000)) {
                    response.add(sb);
                }
            }
        }
        return response;
    }


    private int getDisplayType(JSONObject response) {
        JSONObject jsonObject = ResponseHelper.getVdata(response).optJSONObject("info");
        if (jsonObject != null) {
            return jsonObject.optInt("displaytype", 0);
        }
        return 0;
    }

    public List<SplashBean> getSplashBeans(JSONObject json) {
        if (ResponseHelper.isSuccess(json)) {
            JSONObject info = ResponseHelper.getVdata(json).optJSONObject("info");
            JSONArray list = info.optJSONArray("list");
            if (list != null) {
                SharePrefHelper.setSplashResponse(json.toString());
                Type type = new TypeToken<List<SplashBean>>() {
                }.getType();
                return new Gson().fromJson(list.toString(), type);
            }
        }
        return null;
    }
}
