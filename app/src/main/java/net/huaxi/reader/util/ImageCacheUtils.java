package net.huaxi.reader.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.tools.commonlibs.cache.ImageLreCache;
import com.tools.commonlibs.cache.RequestQueueManager;
import com.tools.commonlibs.common.CommonApp;
import net.huaxi.reader.common.Constants;

/**
 * 异步加载图片
 * Created by taoyingfeng
 * 2015/12/10.
 */
public class ImageCacheUtils {

    // 取运行内存阈值的1/8作为图片缓存
    private static final int MEM_CACHE_SIZE = 1024 * 1024 * ((ActivityManager) CommonApp.getInstance().getSystemService(Context
            .ACTIVITY_SERVICE)).getMemoryClass() / 5;
    private static ImageLoader.ImageCache mImageLreCache = new ImageLreCache(MEM_CACHE_SIZE,
            Constants.XSREADER_IMGCACHE, 500*1024*1024);

    public static ImageLoader mImageLoder = new ImageLoader(RequestQueueManager.getInstance(), mImageLreCache);

    public static ImageLreCache getmImageLoderCache(){
        ImageLreCache result=(ImageLreCache)mImageLreCache;
        return  result;
    }

//    static boolean thumbnails = false;   //返回缩略图Bitmap(结构需要优化);

    private static ImageLoader.ImageListener getImageLinseter(final ImageView view, final Bitmap defaultImageBitmap, final Bitmap
            errorImageBitmap) {

        return new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                if (imageContainer.getBitmap() != null) {
                    Bitmap bitmap = imageContainer.getBitmap();
//                    if (thumbnails) {
//                        int thumbnailWidth = (int) CommonApp.getInstance().getResources().getDimension(R.dimen.thumbnails_width);
//                        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
//                        bitmap = ThumbnailUtils.extractThumbnail(bitmap, thumbnailWidth, thumbnailWidth, ThumbnailUtils
// .OPTIONS_RECYCLE_INPUT);
//                    }
                    view.setImageBitmap(bitmap);
                } else if (defaultImageBitmap != null) {
                    view.setImageBitmap(defaultImageBitmap);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                if (errorImageBitmap != null) {
                    view.setImageBitmap(errorImageBitmap);
                }
            }
        };
    }

    public static ImageLoader.ImageContainer loadImage(String requestUrl, ImageLoader.ImageListener imageListener) {
        return loadImage(requestUrl, imageListener, 0, 0);
    }

    public static ImageLoader.ImageContainer loadImage(String requestUrl,final ImageView view,
                                                       final int defaultImageResId, final int errorImageResId){
        return loadImage(requestUrl,ImageLoader.getImageListener(view,defaultImageResId,errorImageResId));
    }

    private static ImageLoader.ImageContainer loadImage(String url, ImageLoader.ImageListener listener, int maxWidth, int maxHeight) {
        return mImageLoder.get(url, listener, maxWidth, maxHeight);
    }

    /**
     * 外部调用次方法即可完成将url处图片现在view上，并自动实现内存和硬盘双缓存。
     *
     * @param url                远程url地址
     * @param view               待现实图片的view
     * @param defaultImageBitmap 默认显示的图片
     * @param errorImageBitmap   网络出错时显示的图片
     */
    public static ImageLoader.ImageContainer loadImage(final String url, final ImageView view, final Bitmap defaultImageBitmap, final
    Bitmap errorImageBitmap) {
        return loadImage(url, getImageLinseter(view, defaultImageBitmap, errorImageBitmap));
    }

    public static ImageLoader.ImageContainer loadImage(final String url, final View view, final Bitmap defaultImageBitmap, final Bitmap
            errorImageBitmap) {
        return loadImage(url, getImageLinseter(view, defaultImageBitmap, errorImageBitmap));
    }


    private static ImageLoader.ImageListener getImageLinseter(final View view, final Bitmap defaultImageBitmap, final Bitmap
            errorImageBitmap) {
        return new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                if (imageContainer.getBitmap() != null) {
                    Bitmap bitmap = imageContainer.getBitmap();
                    //FIXME:未解决View设置背景问题。
                    view.setBackgroundDrawable(new BitmapDrawable(bitmap));
                } else if (defaultImageBitmap != null) {
                    view.setBackgroundDrawable(new BitmapDrawable(defaultImageBitmap));
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                if (errorImageBitmap != null) {
                    view.setBackgroundDrawable(new BitmapDrawable(errorImageBitmap));
                }
            }
        };
    }

    /**
     * 外部调用次方法即可完成将url处图片现在view上，并自动实现内存和硬盘双缓存。
     *
     * @param url                远程url地址
     * @param view               待现实图片的view
     * @param defaultImageBitmap 默认显示的图片
     * @param errorImageBitmap   网络出错时显示的图片
     * @param maxWidtn
     * @param maxHeight
     */
    public static ImageLoader.ImageContainer loadImage(final String url, final ImageView view, final Bitmap defaultImageBitmap, final
    Bitmap errorImageBitmap, int maxWidtn, int maxHeight) {
        return loadImage(url, getImageLinseter(view, defaultImageBitmap, errorImageBitmap), maxWidtn, maxHeight);
    }

    /**
     * 从资源中取出Bitmap
     *
     * @param act
     * @param resId
     * @return
     */
    private static Bitmap getBitmapFromResources(int resId) {
        Resources res = CommonApp.getInstance().getResources();
        return BitmapFactory.decodeResource(res, resId);
    }

    /**
     * 异步加载图片
     *
     * @author taoyf
     * @time 2015年6月5日
     * @param url
     *            图片网络地址
     * @param view
     * @return
     */
//    public static ImageLoader.ImageContainer loadImage(final String url, final ImageView view,final int defaultResId,final int
// errorResId) {
//        return loadImage(url, view, getBitmapFromResources(defaultResId), getBitmapFromResources(errorResId));
//    }
//
//    public static ImageLoader.ImageContainer loadImage(final String url, final ImageView view, int maxWidth, int maxHeight) {
//        return loadImage(url, view, getBitmapFromResources(R.drawable.ic_default), getBitmapFromResources(R.drawable.ic_error),
// maxWidth, maxHeight);
//    }
}
