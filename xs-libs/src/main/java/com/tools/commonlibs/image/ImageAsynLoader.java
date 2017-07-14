package com.tools.commonlibs.image;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
//import com.tools.commonlibs.R;
import com.tools.commonlibs.common.CommonApp;
import com.tools.commonlibs.tools.MD5Utils;

/**
 * 图片异步加载工具
 * 
 * 源码与示例：http://blog.csdn.net/xu5603/article/details/38822843
 * 
 * ImageLoaderConfiguration是针对图片缓存的全局配置，主要有线程类、缓存大小、磁盘大小、图片下载与解析、日志方面的配置。
 * ImageLoader是具体下载图片，缓存图片，显示图片的具体执行类，它有两个具体的方法displayImage(...)、loadImage(...)，
 * 但是其实最终他们的实现都是displayImage(...)。
 * DisplayImageOptions用于指导每一个Imageloader根据网络图片的状态
 * （空白、下载错误、正在下载）显示对应的图片，是否将缓存加载到磁盘上，下载完后对图片进行怎么样的处理。
 */
public class ImageAsynLoader {
	private static final int DISK_CACHE_COUNT = 1000;
	private static final int DELAY_TIME = 0;
	private static final int DISK_CACHE_SIZE = 100 * 1024 * 1024;
	// private static final int FADE_IN_DISPLAYER = 500;

	private static volatile ImageAsynLoader instance;

	private ImageAsynLoader(Context ctx, String cacheDirName) {

		File cacheDir = new File(cacheDirName);

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(ctx)
		//
				.memoryCacheExtraOptions(480, 800)// 每个缓存文件的最大长宽
				.threadPoolSize(3)// default线程池缓存数
				.threadPriority(Thread.NORM_PRIORITY - 1)// default线程优先级
				.denyCacheImageMultipleSizesInMemory()//
				// .memoryCache(new UsingFreqLimitedMemoryCache(5 * 1024 *
				// 1024))//设置内存缓存 默认为一个当前应用可用内存的1/8大小的LruMemoryCache
				// .memoryCacheSize(5 * 1024 * 1024) //设置内存缓存的最大大小
				// 默认为一个当前应用可用内存的1/8
				// .memoryCacheSizePercentage(13)//设置内存缓存最大大小占当前应用可用内存的百分比
				// 默认为一个当前应用可用内存的1/8
				.diskCacheSize(DISK_CACHE_SIZE) // default
				.diskCacheFileNameGenerator(new MyMd5FileNameGenerator())// 将保存的时候的URI名称用MD5
																			// 加密
				.tasksProcessingOrder(QueueProcessingType.FIFO)// 先进先出
				.diskCacheFileCount(DISK_CACHE_COUNT)// 设置图片下载和显示的工作队列排序
				.diskCache(new UnlimitedDiscCache(cacheDir, null, new MyMd5FileNameGenerator()))// default
																								// 磁盘自定义缓存路径
				.imageDownloader(new BaseImageDownloader(ctx))// default 图片下载
				.imageDecoder(new BaseImageDecoder(true))// default 图片解码
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple())// default
																				// 图片显示
				// .writeDebugLogs()// Remove for release app

				.build();

		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);

	}

	/**
	 * 
	 * @param picPath
	 *            缓存相对路径。例：String READNOVEL_IMGCACHE = "/root/imgCache/"
	 * @return
	 */
	public static ImageAsynLoader getInstance(String picPath) {
		if (instance == null) {
			synchronized (ImageAsynLoader.class) {
				if (instance == null) {
					instance = new ImageAsynLoader(CommonApp.getInstance(), picPath);
				}
			}
		}
		return instance;
	}

	/**
	 * 图片加载
	 * 
	 * @param imageUrl
	 *            图片url
	 * @param imageView
	 *            图片组件
	 */
	public void show(String imageUrl, ImageView imageView) {
//		show(imageUrl, imageView, R.drawable.ic_launcher, DisplayType.def);

	}

	/**
	 * 图片加载
	 * 
	 * @param imageUrl
	 *            图片url
	 * @param imageView
	 *            图片组件
	 * @param displayType
	 *            显示类型
	 */
	public void show(String imageUrl, ImageView imageView, DisplayType displayType) {

//		show(imageUrl, imageView, R.drawable.ic_launcher, displayType);

	}

	/**
	 * 图片加载
	 * 
	 * @param imageUrl
	 *            图片url
	 * @param imageView
	 *            图片组件
	 * @param defImageRes
	 *            默认图
	 */
	public void show(String imageUrl, ImageView imageView, int defImageRes) {

		show(imageUrl, imageView, defImageRes, DisplayType.def);

	}

	/**
	 * 图片加载
	 * 
	 * @param imageUrl
	 *            图片url
	 * @param imageView
	 *            图片组件
	 * @param defImageRes
	 *            默认图
	 * @param displayType
	 *            显示类型
	 */
	public void show(String imageUrl, ImageView imageView, int defImageRes, DisplayType displayType) {
		ImageLoader.getInstance().displayImage(imageUrl, imageView, createOptions(defImageRes, displayType));
	}

	/**
	 * 生成自定义DisplayImageOptions
	 * 
	 * @param defImageRes
	 * @return
	 */
	private static DisplayImageOptions createOptions(int defImageRes, DisplayType displayType) {

		DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder().showImageOnLoading(defImageRes) // resource
																												// or
																												// drawable
				.showImageOnLoading(defImageRes)// 设置图片在下载期间显示的图片
				.showImageForEmptyUri(defImageRes) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(defImageRes) // 设置图片加载/解码过程中错误时候显示的图片
				.resetViewBeforeLoading(true) // 设置图片在下载前是否重置，复位
				.delayBeforeLoading(DELAY_TIME)// int delayInMillis为你设置的下载前的延迟时间
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
				.considerExifParams(false) // 是否考虑JPEG图像EXIF参数（旋转，翻转）
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)// 设置图片以如何的编码方式显示
				.bitmapConfig(Bitmap.Config.RGB_565) // 设置图片的解码类型,默认值——Bitmap.Config.ARGB_8888
				.handler(new Handler());

		if (DisplayType.circle.equals(displayType)) {

			builder.displayer(new CircleBitmapDisplayer());

		} else if (DisplayType.circleWhiteLoop.equals(displayType)) {

			builder.displayer(new CircleWhiteLoopBitmapDisplayer());

		} else {

			builder.displayer(new SimpleBitmapDisplayer()); // default

		}

		// .displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少
		// .displayer(new
		// FadeInBitmapDisplayer(FADE_IN_DISPLAYER))//是否图片加载好后渐入的动画时间

		return builder.build();
	}

	private static final class MyMd5FileNameGenerator implements FileNameGenerator {
		@Override
		public String generate(String imgUri) {
			return MD5Utils.MD5(imgUri);
		}

	}

	/**
	 * 显示类型
	 *
	 */
	public enum DisplayType {
		def, // 默认
		circle, // 圆形
		circleWhiteLoop, // 圆形带圆白环
	}

}
