//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.spriteapp.booklibrary.widget.photoview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.luck.picture.lib.PictureBaseActivity;
import com.luck.picture.lib.R.anim;
import com.luck.picture.lib.R.id;
import com.luck.picture.lib.R.layout;
import com.luck.picture.lib.R.string;
import com.luck.picture.lib.R.style;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.dialog.CustomDialog;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.permissions.RxPermissions;
import com.luck.picture.lib.photoview.OnViewTapListener;
import com.luck.picture.lib.photoview.PhotoView;
import com.luck.picture.lib.tools.DebugUtil;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.luck.picture.lib.tools.ScreenUtils;
import com.luck.picture.lib.widget.PreviewViewPager;
import com.luck.picture.lib.widget.longimage.ImageSource;
import com.luck.picture.lib.widget.longimage.ImageViewState;
import com.luck.picture.lib.widget.longimage.SubsamplingScaleImageView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class PictureExternalPreviewActivity extends PictureBaseActivity implements OnClickListener {
    private ImageButton left_back;
    private TextView tv_title;
    private PreviewViewPager viewPager;
    private List<LocalMedia> images = new ArrayList();
    private int position = 0;
    private String directory_path;
    private PictureExternalPreviewActivity.SimpleFragmentAdapter adapter;
    private LayoutInflater inflater;
    private RxPermissions rxPermissions;
    private PictureExternalPreviewActivity.loadDataThread loadDataThread;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what) {
                case 200:
                    String path = (String)msg.obj;
                    PictureExternalPreviewActivity.this.showToast(PictureExternalPreviewActivity.this.getString(string.picture_save_success) + "\n" + path);
                    PictureExternalPreviewActivity.this.dismissDialog();


                    //发广播告诉相册有图片需要更新，这样可以在图册下看到保存的图片了
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri uri = Uri.fromFile(new File(path));
                    intent.setData(uri);
                    sendBroadcast(intent);
                default:
            }
        }
    };

    public PictureExternalPreviewActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(layout.picture_activity_external_preview);
        this.inflater = LayoutInflater.from(this);
        this.tv_title = (TextView)this.findViewById(id.picture_title);
        this.left_back = (ImageButton)this.findViewById(id.left_back);
        this.viewPager = (PreviewViewPager)this.findViewById(id.preview_pager);
        this.position = this.getIntent().getIntExtra("position", 0);
        this.directory_path = this.getIntent().getStringExtra("directory_path");
        this.images = (List)this.getIntent().getSerializableExtra("previewSelectList");
        this.left_back.setOnClickListener(this);
        this.initViewPageAdapterData();
    }

    private void initViewPageAdapterData() {
        this.tv_title.setText(this.position + 1 + "/" + this.images.size());
        this.adapter = new PictureExternalPreviewActivity.SimpleFragmentAdapter();
        this.viewPager.setAdapter(this.adapter);
        this.viewPager.setCurrentItem(this.position);
        this.viewPager.addOnPageChangeListener(new OnPageChangeListener() {
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                PictureExternalPreviewActivity.this.tv_title.setText(position + 1 + "/" + PictureExternalPreviewActivity.this.images.size());
            }

            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public void onClick(View v) {
        this.finish();
        this.overridePendingTransition(0, anim.a3);
    }

    private void displayLongPic(Bitmap bmp, SubsamplingScaleImageView longImg) {
        longImg.setQuickScaleEnabled(true);
        longImg.setZoomEnabled(true);
        longImg.setPanEnabled(true);
        longImg.setDoubleTapZoomDuration(100);
        longImg.setMinimumScaleType(2);
        longImg.setDoubleTapZoomDpi(2);
        longImg.setImage(ImageSource.cachedBitmap(bmp), new ImageViewState(0.0F, new PointF(0.0F, 0.0F), 0));
    }

    private void showDownLoadDialog(final String path) {
        final CustomDialog dialog = new CustomDialog(this, ScreenUtils.getScreenWidth(this) * 3 / 4, ScreenUtils.getScreenHeight(this) / 4, layout.picture_wind_base_dialog_xml, style.Theme_dialog);
        Button btn_cancel = (Button)dialog.findViewById(id.btn_cancel);
        Button btn_commit = (Button)dialog.findViewById(id.btn_commit);
        TextView tv_title = (TextView)dialog.findViewById(id.tv_title);
        TextView tv_content = (TextView)dialog.findViewById(id.tv_content);
        tv_title.setText(this.getString(string.picture_prompt));
        tv_content.setText(this.getString(string.picture_prompt_content));
        btn_cancel.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btn_commit.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                PictureExternalPreviewActivity.this.showPleaseDialog();
                boolean isHttp = PictureMimeType.isHttp(path);
                if(isHttp) {
                    PictureExternalPreviewActivity.this.loadDataThread = PictureExternalPreviewActivity.this.new loadDataThread(path);
                    PictureExternalPreviewActivity.this.loadDataThread.start();
                } else {
                    try {
                        String e = PictureFileUtils.createDir(PictureExternalPreviewActivity.this, path.substring(path.lastIndexOf("/") + 1, path.length()), PictureExternalPreviewActivity.this.directory_path);
                        PictureFileUtils.copyFile(path, e);
                        PictureExternalPreviewActivity.this.showToast(PictureExternalPreviewActivity.this.getString(string.picture_save_success) + "\n" + e);
                        PictureExternalPreviewActivity.this.dismissDialog();

                    } catch (IOException var4) {
                        PictureExternalPreviewActivity.this.showToast(PictureExternalPreviewActivity.this.getString(string.picture_save_error) + "\n" + var4.getMessage());
                        PictureExternalPreviewActivity.this.dismissDialog();
                        var4.printStackTrace();
                    }
                }

                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void showLoadingImage(String urlPath) {
        try {
            URL e = new URL(urlPath);
            String path = PictureFileUtils.createDir(this, urlPath.substring(urlPath.lastIndexOf("/") + 1, urlPath.length()),this.directory_path);
            byte[] buffer = new byte[8192];
            int ava = 0;
            long start = System.currentTimeMillis();
            BufferedInputStream bin = new BufferedInputStream(e.openStream());
            BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(path));

            int read;
            while((read = bin.read(buffer)) > -1) {
                bout.write(buffer, 0, read);
                ava += read;
                long message = (long)ava / (System.currentTimeMillis() - start);
                DebugUtil.i("Download: " + ava + " byte(s)    avg speed: " + message + "  (kb/s)");
            }

            bout.flush();
            bout.close();
            Message message1 = this.handler.obtainMessage();
            message1.what = 200;
            message1.obj = path;
            this.handler.sendMessage(message1);
        } catch (IOException var13) {
            this.showToast(this.getString(string.picture_save_error) + "\n" + var13.getMessage());
            var13.printStackTrace();
        }

    }

    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        this.overridePendingTransition(0, anim.a3);
    }

    protected void onDestroy() {
        super.onDestroy();
        if(this.loadDataThread != null) {
            this.handler.removeCallbacks(this.loadDataThread);
            this.loadDataThread = null;
        }

    }

    public class loadDataThread extends Thread {
        private String path;


        public loadDataThread(String path) {
            this.path = path;

        }

        public void run() {
            try {
                PictureExternalPreviewActivity.this.showLoadingImage(this.path);
            } catch (Exception var2) {
                var2.printStackTrace();
            }

        }
    }

    public class SimpleFragmentAdapter extends PagerAdapter {
        public SimpleFragmentAdapter() {
        }

        public int getCount() {
            return PictureExternalPreviewActivity.this.images.size();
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }

        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public Object instantiateItem(ViewGroup container, final int position) {
            View contentView = PictureExternalPreviewActivity.this.inflater.inflate(layout.picture_image_preview, container, false);
            final PhotoView imageView = (PhotoView)contentView.findViewById(id.preview_image);
            final SubsamplingScaleImageView longImg = (SubsamplingScaleImageView)contentView.findViewById(id.longImg);
            LocalMedia media = (LocalMedia)PictureExternalPreviewActivity.this.images.get(position);
            if(media != null) {
                String pictureType = media.getPictureType();
                final String path;
                if(media.isCut() && !media.isCompressed()) {
                    path = media.getCutPath();
                } else if(!media.isCompressed() && (!media.isCut() || !media.isCompressed())) {
                    path = media.getPath();
                } else {
                    path = media.getCompressPath();
                }

                boolean isHttp = PictureMimeType.isHttp(path);
                if(isHttp) {
                    PictureExternalPreviewActivity.this.showPleaseDialog();
                }

                boolean isGif = PictureMimeType.isGif(pictureType);
                final boolean eqLongImg = PictureMimeType.isLongImg(media);
                imageView.setVisibility(eqLongImg && !isGif?8:0);
                longImg.setVisibility(eqLongImg && !isGif?0:8);
                RequestOptions options;
                if(isGif && !media.isCompressed()) {
                    options = (new RequestOptions()).override(480, 800).priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.NONE);
                    Glide.with(PictureExternalPreviewActivity.this).asGif().apply(options).load(path).listener(new RequestListener() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                            PictureExternalPreviewActivity.this.dismissDialog();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                            PictureExternalPreviewActivity.this.dismissDialog();
                            return false;
                        }

                    }).into(imageView);
                } else {
                    options = (new RequestOptions()).diskCacheStrategy(DiskCacheStrategy.ALL);
                    Glide.with(PictureExternalPreviewActivity.this).asBitmap().load(path).apply(options).into(new SimpleTarget(480, 800) {
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                            PictureExternalPreviewActivity.this.dismissDialog();
                        }

                        @Override
                        public void onResourceReady(Object resource, Transition transition) {
                            PictureExternalPreviewActivity.this.dismissDialog();
                            if(eqLongImg) {
                                PictureExternalPreviewActivity.this.displayLongPic((Bitmap) resource, longImg);
                            } else {

                                imageView.setImageBitmap((Bitmap) resource);
                            }
                        }

                    });
                }

                imageView.setOnViewTapListener(new OnViewTapListener() {
                    public void onViewTap(View view, float x, float y) {
                        PictureExternalPreviewActivity.this.finish();
                        PictureExternalPreviewActivity.this.overridePendingTransition(0, anim.a3);
                    }
                });
                longImg.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        PictureExternalPreviewActivity.this.finish();
                        PictureExternalPreviewActivity.this.overridePendingTransition(0, anim.a3);
                    }
                });
                imageView.setOnLongClickListener(new OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        if(PictureExternalPreviewActivity.this.rxPermissions == null) {
                            PictureExternalPreviewActivity.this.rxPermissions = new RxPermissions(PictureExternalPreviewActivity.this);
                        }

                        PictureExternalPreviewActivity.this.rxPermissions.request(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}).subscribe(new Observer() {
                            public void onSubscribe(Disposable d) {
                            }

                            @Override
                            public void onNext(@NonNull Object o) {
                                PictureExternalPreviewActivity.this.showDownLoadDialog(path);
                            }

                            public void onError(Throwable e) {
                            }

                            public void onComplete() {
                            }
                        });
                        return true;
                    }
                });
            }

            container.addView(contentView, 0);
            return contentView;
        }
    }
}
