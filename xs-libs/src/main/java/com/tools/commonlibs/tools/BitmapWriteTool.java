package com.tools.commonlibs.tools;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Environment;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapWriteTool {

	public static String ROOTPATH = Environment.getExternalStorageDirectory() + File.separator +
			"commonlibs";

	public static String ROOTPATH_TEMP = ROOTPATH + File.separator + "tmp";

	public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}

		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * @param bitmap
	 * @return
	 */
	public static String writeToSDcard(Bitmap bitmap, String dir, CompressFormat format) {
		if (bitmap == null || bitmap.isRecycled())
			return null;

		String path = createFilePath(dir);
		File file = new File(path);

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		if (path != null) {

			FileOutputStream m_fileOutPutStream = null;
			try {
				m_fileOutPutStream = new FileOutputStream(path);// 写入的文件路径
				bitmap.compress(format == null ? CompressFormat.JPEG : format, 75,
						m_fileOutPutStream);
				m_fileOutPutStream.flush();
				m_fileOutPutStream.close();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			bitmap.recycle();
			bitmap = null;
			return path;
		}
		return null;
	}

	public static File writeToSDcard(Bitmap bitmap, File file, CompressFormat format) {
		if (bitmap == null || bitmap.isRecycled())
			return null;

		if (file.isDirectory()) {
			FileUtils.deleteChildDir(file, null);
			file.delete();
		}
		if (!file.getParentFile().isDirectory()) {
			file.getParentFile().delete();
		}
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}


		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		if (file != null) {
			FileOutputStream m_fileOutPutStream = null;
			try {
				m_fileOutPutStream = new FileOutputStream(file);// 写入的文件路径
				bitmap.compress(format == null ? CompressFormat.JPEG : format, 100,
						m_fileOutPutStream);
				m_fileOutPutStream.flush();
				m_fileOutPutStream.close();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			bitmap.recycle();
			bitmap = null;
			return file;
		}
		return null;
	}

	public static String createFilePath(String dir) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			if (TextUtils.isEmpty(dir) || !dir.startsWith(ROOTPATH)) {
				return null;
			}
			StringBuffer sb = new StringBuffer();
//			sb.append(Environment.getExternalStorageDirectory().getAbsolutePath());
			sb.append(dir);
			File file = new File(sb.toString());
			if (!file.exists()) {
				file.mkdirs();
			}
			sb.append(File.separator + System.currentTimeMillis() + ".jpg");
			return sb.toString();
		} else {
			return null;
		}

	}

	/**
	 * 该方法 废弃了
	 *
	 * @param originalImage
	 * @param text
	 * @return
	 */
	public static Bitmap wirteText(Bitmap originalImage, String text) {
		int width = originalImage.getWidth();
		int height = originalImage.getHeight();
		Paint textPaint = new Paint();
		textPaint.setColor(Color.BLACK);
		textPaint.setTextSize(16);
		textPaint.setAntiAlias(true);
		Rect bounds = new Rect();
		textPaint.getTextBounds(text, 0, text.length(), bounds);

		float hangFloat = bounds.width() / (width * 1.0f);
		int hangInt = bounds.width() / width;

		int realhangNum = 0;
		if (hangFloat - hangInt > 0) {
			realhangNum = hangInt + 1;
		} else {
			realhangNum = hangInt;
		}

		Bitmap mBmp = Bitmap.createBitmap(width, height + bounds.height() * realhangNum + 20,
				Config.ARGB_8888);

		Canvas canvas = new Canvas(mBmp);
		canvas.drawColor(Color.WHITE);
		canvas.drawBitmap(originalImage, 0, 0, textPaint);
		int start = 0;
		int end = 0;
		int length = (int) (text.length() / hangFloat);
		for (int i = 0; i < realhangNum; i++) {
			start = length * i;
			if (i + 1 != realhangNum) {
				end = start + length;
			} else {
				end = text.length();
			}
			System.out.println(start + " " + end + " " + text.length());
			canvas.drawText(text, start, end, 0, height + bounds.height() * (i + 1) + 10,
					textPaint);

		}
		return mBmp;
	}
}
