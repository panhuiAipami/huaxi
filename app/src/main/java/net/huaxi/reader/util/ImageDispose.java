package net.huaxi.reader.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.os.StatFs;

import com.tools.commonlibs.tools.BitmapWriteTool;
import com.tools.commonlibs.tools.LogUtils;

/**
 * 处理图片的旋转和压缩问题
 */
public class ImageDispose {
	
	
	/**
	 * 获取压缩后的图片
	 * @param oldPath
	 * @param bitmapMaxWidth
	 * @return
	 * @throws Exception
	 */
	public static String getThumbUploadPath(String oldPath, int bitmapMaxWidth) throws Exception {
		
		String thumbPath = null;
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(oldPath, options);
		int height = options.outHeight;
		int width = options.outWidth;
		
		int reqWidth  = Math.min(400, bitmapMaxWidth);
		int reqHeight = (reqWidth * height) / width;

		// 在内存中创建bitmap对象，这个对象按照缩放大小创建的
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		options.inJustDecodeBounds = false;
		LogUtils.info("options.inSampleSize:" + options.inSampleSize);
		LogUtils.info("reqWidth:"+reqWidth);
		LogUtils.info("reqHeight:"+reqHeight);
		Bitmap bitmap = BitmapFactory.decodeFile(oldPath, options);
		LogUtils.info("---reqWidth:"+bitmap.getWidth());
		LogUtils.info("---reqHeight:"+bitmap.getHeight());
		Bitmap bbb = compressImage(Bitmap.createScaledBitmap(bitmap, reqWidth, reqHeight, false));
		bitmap.recycle();
		int degree = readPictureDegree(oldPath);
		if (degree == 0) {
			thumbPath= BitmapWriteTool.writeToSDcard(bbb, BitmapWriteTool.ROOTPATH_TEMP,
					CompressFormat.JPEG);
		} else {
			Matrix matrix = new Matrix();
			matrix.postRotate(degree);
			Bitmap realizeBmp = Bitmap.createBitmap(bbb, 0, 0, bbb.getWidth(), bbb.getHeight(), matrix, true);
			thumbPath = BitmapWriteTool.writeToSDcard(realizeBmp,BitmapWriteTool.ROOTPATH_TEMP,CompressFormat.JPEG);
			
		}
		
		return thumbPath;
	}

	private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight||width>reqWidth) {
				inSampleSize = height /reqHeight+1;
		}
		return inSampleSize;
	}

	private static Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int quality = 100;
		LogUtils.info("-----k:"+baos.toByteArray().length / 1024);
		LogUtils.info("----quality:"+75);
		while (baos.toByteArray().length / 1024 > 10&&quality>=70) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			
			LogUtils.info("-----k:"+baos.toByteArray().length / 1024);
			LogUtils.info("----quality:"+quality);
			
			quality -= 10;// 每次都减少10
			baos.reset();// 重置baos即清空baos
			image.compress(CompressFormat.JPEG, quality, baos);// 这里压缩options%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		image.recycle();
		image = null;
		return bitmap;
	}
	
	/**
	 * 读取图片的旋转度数
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

	public static long getAvailableStore(String filePath) {

		StatFs statFs = new StatFs(filePath);

		// 获取block的SIZE

		long blocSize = statFs.getBlockSize();

		// 可使用的Block的数量

		long availaBlock = statFs.getAvailableBlocks();

		long availableSpare = availaBlock * blocSize;

		return availableSpare;

	}

	public static boolean enoughCapacity() {
		boolean isEnough = false;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

			long size = getAvailableStore(Environment.getExternalStorageDirectory().getAbsolutePath());
			if (size / M > 50) {
				isEnough = true;

			} else {
				isEnough = false;
			}
		}
		return isEnough;
	}

	public static long M = 1024 * 1024;
}
