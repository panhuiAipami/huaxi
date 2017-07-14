package com.tools.commonlibs.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;

import com.tools.commonlibs.common.CommonApp;

public class ImageUtils {
	
	/**
	 * 通过路径获取Bitmap;
	 * @param path
	 * @return
	 */
	public static Bitmap getBitmapWithPath(String path) {
		final File coverFile = new File(path);
		if (null == coverFile || !coverFile.exists()) {
			return null;
		}
		Bitmap bmp = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(coverFile);
			bmp = BitmapFactory.decodeStream(fis);
		} catch (IOException e) {
		} finally {
			try {
				if (null != fis) {
					fis.close();
					fis = null;
				}
			} catch (Exception e) {
				LogUtils.debug("decodeWithOptions error : " + e.toString());
			}

		}

		return bmp;
	}
	
	/**
	 * 根据百分比显示图片.
	 * @param targetBm
	 * @param progress
	 * @return
	 */
	public static Bitmap getClipBitmap(Bitmap targetBm, int progress) {
		if (targetBm == null) {
			return null;
		}
		int w = targetBm.getWidth();
		int h = targetBm.getHeight();// (targetBm.getHeight() * progress) / 100;

		Bitmap newb = Bitmap.createBitmap(w, h,
				android.graphics.Bitmap.Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图

		Canvas cv = new Canvas(newb);
		cv.save();// 保存
		cv.clipRect(0, 0, w, ((100 - progress) * h) / 100);

		cv.drawBitmap(targetBm, 0, 0, null);// 在 0，0坐标开始画入src

		cv.restore();// 存储
		return newb;
	}

	/**
	 * 图片透明度处理
	 * 
	 * @param sourceImg
	 *            原始图片
	 * @param number
	 *            透明度  设置的透明度数值，取值范围是从0-100
	 * @return
	 */
	public static Bitmap setAlpha(Bitmap sourceImg, int number) {
		int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];
		sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0, sourceImg.getWidth(), sourceImg.getHeight());// 获得图片的ARGB值
		number = number * 255 / 100;
		for (int i = 0; i < argb.length; i++) {
			argb[i] = (number << 24) | (argb[i] & 0x00FFFFFF);// 修改最高2位的值
		}
		sourceImg = Bitmap
				.createBitmap(argb, sourceImg.getWidth(),
						sourceImg.getHeight(),
						android.graphics.Bitmap.Config.ARGB_8888);

		return sourceImg;
	}
	
	/**
	 * 融合两张图片(覆盖式)
	 * 
	 * @author taoyf
	 * @time 2015年1月12日
	 * @param context
	 * @param resid
	 *            底部资源
	 * @param resid2
	 *            上部资源
	 * @param left
	 * @param top
	 * @return
	 */
	@SuppressWarnings("finally")
	public static Bitmap MergeOfOverlap(final Context context, final int resid, final int resid2, int left, int top) {
		Bitmap newBitmap = null;
		try {
			// 防止出现Immutable bitmap passed to Canvas constructor错误
			Bitmap bitmap1 = BitmapFactory.decodeResource(context.getResources(), resid).copy(Bitmap.Config.ARGB_8888, true);
			Bitmap bitmap2 = ((BitmapDrawable) context.getResources().getDrawable(resid2)).getBitmap();
			newBitmap = Bitmap.createBitmap(bitmap1);
			Canvas canvas = new Canvas(newBitmap);
			Paint paint = new Paint();
			paint.setColor(Color.GRAY);
			paint.setAlpha(125);
			canvas.drawRect(0, 0, bitmap1.getWidth(), bitmap1.getHeight(), paint);
			paint = new Paint();
			canvas.drawBitmap(bitmap2, left, top, paint);
			canvas.save(Canvas.ALL_SAVE_FLAG);
			// 存储新合成的图片
			canvas.restore();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return newBitmap;
		}

	}

	/**
	 * 根据指定的图像路径和大小来获取缩略图 此方法有两点好处： 1.
	 * 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
	 * 第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。 2.
	 * 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使 用这个工具生成的图像不会被拉伸。
	 * 
	 * @param imagePath
	 *            图像的路径
	 * @param width
	 *            指定输出图像的宽度
	 * @param height
	 *            指定输出图像的高度
	 * @return 生成的缩略图
	 */
	public static Bitmap getImageThumbnail(String imagePath, int width, int height) {
		Bitmap bitmap = null;
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			// 获取这个图片的宽和高，注意此处的bitmap为null
			bitmap = BitmapFactory.decodeFile(imagePath, options);
			options.inJustDecodeBounds = false; // 设为 false
			// 计算缩放比
			int h = options.outHeight;
			int w = options.outWidth;
			int beWidth = w / width;
			int beHeight = h / height;
			int be = 1;
			if (beWidth < beHeight) {
				be = beWidth;
			} else {
				be = beHeight;
			}
			if (be <= 0) {
				be = 1;
			}
			options.inSampleSize = be;
			// 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
			bitmap = BitmapFactory.decodeStream(new FileInputStream(imagePath),null, options);
			// 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
			bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * 获取压缩后的图片
	 * 
	 * @param oldPath
	 * @param bitmapMaxWidth
	 * @return
	 * @throws Exception
	 */
	public static String getThumbUploadPath(String oldPath, int bitmapMaxWidth) throws Exception {

		String thumbPath = null;
		//从path读取Bitmap;
		Bitmap bitmap = readBitmap(oldPath, bitmapMaxWidth);
		
		int degree = readPictureDegree(oldPath);
		if (degree == 0) {
			thumbPath = BitmapWriteTool.writeToSDcard(bitmap, BitmapWriteTool.ROOTPATH_TEMP, CompressFormat.JPEG);
		} else {
			Matrix matrix = new Matrix();
			matrix.postRotate(degree);
			Bitmap realizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			thumbPath = BitmapWriteTool.writeToSDcard(realizeBmp, BitmapWriteTool.ROOTPATH_TEMP, CompressFormat.JPEG);

		}

		return thumbPath;
	}
	
	/**
	 * 从InputStream读取Bitmap
	 * @param is
	 * @return
	 */
	public static Bitmap readBitMap(InputStream is) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;

		return BitmapFactory.decodeStream(is, null, opt);
	}
	
	/**
	 * 从本地读取图片
	 * @param oldpath
	 * @param bitmapMaxWidth
	 * @return
	 */
	public static Bitmap readBitmap(String oldpath,int bitmapMaxWidth){
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(oldpath, options);
			int height = options.outHeight;
			int width = options.outWidth;

			int reqWidth = Math.min(720, bitmapMaxWidth);
			int reqHeight = (reqWidth * height) / width;

			// 在内存中创建bitmap对象，这个对象按照缩放大小创建的
			options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
			options.inJustDecodeBounds = false;
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			options.inPurgeable = true;
			options.inInputShareable = true;
			LogUtils.debug("options.inSampleSize:" + options.inSampleSize+" reqWidth:" + reqWidth+" reqHeight:" + reqHeight);
			InputStream is = new FileInputStream(oldpath);
			Bitmap bitmap =  BitmapFactory.decodeStream(is, null, options);
//			bitmap = compressImage(temp);
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			inSampleSize = height / reqHeight + 1;
		}
		return inSampleSize;
	}

	@SuppressWarnings("unused")
	private static Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int quality = 100;
		LogUtils.info("-----k:" + baos.toByteArray().length / 1024);
		LogUtils.info("----quality:" + 75);
		while (baos.toByteArray().length / 1024 > 100 && quality >= 70) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩

			LogUtils.info("-----k:" + baos.toByteArray().length / 1024);
			LogUtils.info("----quality:" + quality);

			quality -= 10;// 每次都减少10
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, quality, baos);// 这里压缩options%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		image.recycle();
		image = null;
		return bitmap;
	}

	/**
	 * 读取图片的旋转度数
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static int readPictureDegree(String path) throws IOException {
		int degree = 0;
		ExifInterface exifInterface = new ExifInterface(path);
		int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
		switch (orientation) {
		case ExifInterface.ORIENTATION_ROTATE_90:
			degree = 90;
			break;
		case ExifInterface.ORIENTATION_ROTATE_180:
			degree = 180;
			break;
		case ExifInterface.ORIENTATION_ROTATE_270:
			degree = 270;
			break;
		}
		return degree;
	}

	public static void recycleBitmap(Bitmap bitmap) {
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
			bitmap = null;
		}
	}

	/**
	 * 根据原图和变长绘制圆形图片
	 * 
	 * @param source
	 * @param min
	 * @return
	 */
	public static Bitmap createCircleImage(Bitmap source, int min) {

		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);

		Bitmap target = Bitmap.createBitmap(min, min, Config.ARGB_4444);
		/**
		 * 1.产生一个同样大小的画布
		 */
		Canvas canvas = new Canvas(target);
		/**
		 * 2.首先绘制圆形
		 */
		paint.setColor(Color.WHITE);
		canvas.drawCircle(min / 2, min / 2, min / 2, paint);

		/**
		 * 3.绘制图片
		 */
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(source, 0, 0, paint);

		/**
		 * 4.绘制白色圆环
		 */
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(2.5f);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setColor(Color.WHITE);
		canvas.drawCircle(min / 2, min / 2, min / 2 - 1.0f, paint);

		return target;
	}

	/**
	 * 根据原图添加圆角
	 * 
	 * @param source
	 * @return
	 */
	public static Bitmap createRoundConerImage(Bitmap source) {
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		Bitmap target = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Config.ARGB_4444);
		Canvas canvas = new Canvas(target);
		RectF rect = new RectF(0, 0, source.getWidth(), source.getHeight());
		canvas.drawRoundRect(rect, 50f, 50f, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(source, 0, 0, paint);
		return target;
	}

	/**
	 * 放缩图片
	 * @param bm 源图片
	 * @param newWidth    目标宽度
	 * @param newHeight    目标高度
	 * @return
	 */
	public static Bitmap scaleImage(Bitmap bm, int newWidth, int newHeight) {
		if (bm == null) {
			return null;
		}
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
		if (bm != null & !bm.isRecycled()) {
			bm.recycle();
			bm = null;
		}
		return newbm;
	}

	/**
	 * 以最省内存的方式读取本地资源的图片
	 * 
	 * @param context
	 * @param resId
	 * @return
	 */
	public static Bitmap readBitMap(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

	/** 水平方向模糊度 */
	private static float hRadius = 10;
	/** 竖直方向模糊度 */
	private static float vRadius = 10;
	/** 模糊迭代度 */
	private static int iterations = 7;

	/**
	 * 高斯模糊图片处理
	 * 
	 * @param bmp
	 * @return
	 */
	public static Bitmap BoxBlurFilter(Bitmap bmp) {
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		int[] inPixels = new int[width * height];
		int[] outPixels = new int[width * height];
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bmp.getPixels(inPixels, 0, width, 0, 0, width, height);
		for (int i = 0; i < iterations; i++) {
			blur(inPixels, outPixels, width, height, hRadius);
			blur(outPixels, inPixels, height, width, vRadius);
		}
		blurFractional(inPixels, outPixels, width, height, hRadius);
		blurFractional(outPixels, inPixels, height, width, vRadius);
		bitmap.setPixels(inPixels, 0, width, 0, 0, width, height);

		return bitmap;
	}

	public static void blur(int[] in, int[] out, int width, int height, float radius) {
		int widthMinus1 = width - 1;
		int r = (int) radius;
		int tableSize = 2 * r + 1;
		int divide[] = new int[256 * tableSize];
		for (int i = 0; i < 256 * tableSize; i++)
			divide[i] = i / tableSize;
		int inIndex = 0;
		for (int y = 0; y < height; y++) {
			int outIndex = y;
			int ta = 0, tr = 0, tg = 0, tb = 0;
			for (int i = -r; i <= r; i++) {
				int rgb = in[inIndex + clamp(i, 0, width - 1)];
				ta += (rgb >> 24) & 0xff;
				tr += (rgb >> 16) & 0xff;
				tg += (rgb >> 8) & 0xff;
				tb += rgb & 0xff;
			}

			for (int x = 0; x < width; x++) {
				out[outIndex] = (divide[ta] << 24) | (divide[tr] << 16) | (divide[tg] << 8) | divide[tb];
				int i1 = x + r + 1;
				if (i1 > widthMinus1)
					i1 = widthMinus1;
				int i2 = x - r;
				if (i2 < 0)
					i2 = 0;
				int rgb1 = in[inIndex + i1];
				int rgb2 = in[inIndex + i2];

				ta += ((rgb1 >> 24) & 0xff) - ((rgb2 >> 24) & 0xff);
				tr += ((rgb1 & 0xff0000) - (rgb2 & 0xff0000)) >> 16;
				tg += ((rgb1 & 0xff00) - (rgb2 & 0xff00)) >> 8;
				tb += (rgb1 & 0xff) - (rgb2 & 0xff);
				outIndex += height;
			}
			inIndex += width;
		}
	}

	public static void blurFractional(int[] in, int[] out, int width, int height, float radius) {
		radius -= (int) radius;
		float f = 1.0f / (1 + 2 * radius);
		int inIndex = 0;
		for (int y = 0; y < height; y++) {
			int outIndex = y;
			out[outIndex] = in[0];
			outIndex += height;
			for (int x = 1; x < width - 1; x++) {
				int i = inIndex + x;
				int rgb1 = in[i - 1];
				int rgb2 = in[i];
				int rgb3 = in[i + 1];
				int a1 = (rgb1 >> 24) & 0xff;
				int r1 = (rgb1 >> 16) & 0xff;
				int g1 = (rgb1 >> 8) & 0xff;
				int b1 = rgb1 & 0xff;
				int a2 = (rgb2 >> 24) & 0xff;
				int r2 = (rgb2 >> 16) & 0xff;
				int g2 = (rgb2 >> 8) & 0xff;
				int b2 = rgb2 & 0xff;
				int a3 = (rgb3 >> 24) & 0xff;
				int r3 = (rgb3 >> 16) & 0xff;
				int g3 = (rgb3 >> 8) & 0xff;
				int b3 = rgb3 & 0xff;

				a1 = a2 + (int) ((a1 + a3) * radius);
				r1 = r2 + (int) ((r1 + r3) * radius);
				g1 = g2 + (int) ((g1 + g3) * radius);
				b1 = b2 + (int) ((b1 + b3) * radius);
				a1 *= f;
				r1 *= f;
				g1 *= f;
				b1 *= f;
				out[outIndex] = (a1 << 24) | (r1 << 16) | (g1 << 8) | b1;
				outIndex += height;
			}
			out[outIndex] = in[width - 1];
			inIndex += width;
		}
	}

	public static int clamp(int x, int a, int b) {
		return (x < a) ? a : (x > b) ? b : x;
	}
}
