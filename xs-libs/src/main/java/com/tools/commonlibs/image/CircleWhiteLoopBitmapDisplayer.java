package com.tools.commonlibs.image;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class CircleWhiteLoopBitmapDisplayer implements BitmapDisplayer {
	protected final int margin;

	public CircleWhiteLoopBitmapDisplayer() {

		this(0);

	}

	public CircleWhiteLoopBitmapDisplayer(int margin) {

		this.margin = margin;

	}

	@Override
	public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {

		if (!(imageAware instanceof ImageViewAware)) {

			throw new IllegalArgumentException("ImageAware should wrap ImageView. ImageViewAware is expected.");

		}

		imageAware.setImageDrawable(new CircleWhiteLoopDrawable(bitmap, margin));

	}
}
