package com.tools.commonlibs.image;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

public class CircleWhiteLoopDrawable extends Drawable {

	public static final String TAG = "CircleDrawable";

	protected Paint paint;

	protected int margin = 30;

	protected final BitmapShader bitmapShader;

	protected float radius;

	protected Bitmap oBitmap;//原图

	public CircleWhiteLoopDrawable(Bitmap bitmap) {

		this(bitmap, 0);

	}

	public CircleWhiteLoopDrawable(Bitmap bitmap, int margin) {

		this.margin = margin;

		this.oBitmap = bitmap;

		bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

		paint = new Paint();

		paint.setAntiAlias(true);

		paint.setShader(bitmapShader);

	}

	@Override
	protected void onBoundsChange(Rect bounds) {

		super.onBoundsChange(bounds);

		computeBitmapShaderSize();

		computeRadius();

	}

	@Override
	public void draw(Canvas canvas) {

		Rect bounds = getBounds();

		//画一个圆圈
		canvas.drawCircle(bounds.width() / 2F, bounds.height() / 2F, radius, paint);

		//绘制白色圆环
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(2.0f);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setColor(Color.WHITE);
		canvas.drawCircle(bounds.width() / 2F, bounds.height() / 2F, radius - 1.0F, paint);

	}

	@Override
	public int getOpacity() {

		return PixelFormat.TRANSLUCENT;

	}

	@Override
	public void setAlpha(int alpha) {

		paint.setAlpha(alpha);

	}

	@Override
	public void setColorFilter(ColorFilter cf) {

		paint.setColorFilter(cf);

	}

	/**

	 * 策画Bitmap shader 大小

	 */

	public void computeBitmapShaderSize() {

		Rect bounds = getBounds();

		if (bounds == null)
			return;

		//选择缩放斗劲多的缩放,如许就不会有拉伸失衡

		Matrix matrix = new Matrix();

		float scaleX = bounds.width() / (float) oBitmap.getWidth();

		float scaleY = bounds.height() / (float) oBitmap.getHeight();

		float scale = scaleX > scaleY ? scaleX : scaleY;

		matrix.postScale(scale, scale);

		bitmapShader.setLocalMatrix(matrix);

	}

	/**

	 * 策画半径的大小

	 */

	public void computeRadius() {

		Rect bounds = getBounds();

		radius = bounds.width() < bounds.height() ?

		bounds.width() / 2F - margin :

		bounds.height() / 2F - margin;

	}

}
