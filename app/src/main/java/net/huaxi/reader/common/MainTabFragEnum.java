package net.huaxi.reader.common;

import android.support.v4.app.Fragment;
import android.widget.RadioGroup;

import net.huaxi.reader.fragment.FmBookShelf;
import net.huaxi.reader.fragment.FmBookStore;
import net.huaxi.reader.fragment.FmCategory;
import net.huaxi.reader.fragment.FmPersonCenter;

public enum MainTabFragEnum {


	bookshelf(0) {
		@Override
		public Fragment createFrag() {
			Fragment frag = new FmBookShelf();
			setFrag(frag);
			return frag;
		}
	}, // 书架

	bookcity(1) {
		@Override
		public Fragment createFrag() {
//			Fragment frag = new FmBookCity();
			Fragment frag = new FmBookStore();
			setFrag(frag);
			return frag;
		}
	}, // 书城

	paihang(2) {
		@Override
		public Fragment createFrag() {
//			Fragment frag = new FmGambitPaihang();
			Fragment frag = new FmCategory();
			setFrag(frag);
			return frag;
		}

	},//排行

	person(3) {
		@Override
		public Fragment createFrag() {
			Fragment frag = new FmPersonCenter();
			setFrag(frag);
			return frag;
		}

	}, // 个人中心

	;

	/* 排序 */
	private int index;
	private Fragment frag;

	private MainTabFragEnum(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public Fragment getFrag() {
		return frag;
	}

	public void setFrag(Fragment frag) {
		this.frag = frag;
	}

	/**
	 * 得到当前片段实例
	 * 
	 * @return
	 */
	public abstract Fragment createFrag();

	/**
	 * 片段跳转
	 * 
	 * @return
	 */
	public void goTo(RadioGroup tabs) {
		tabs.getChildAt(getIndex()).performClick();
	}

	/**
	 * 通过索引得到实例
	 * 
	 * @param index
	 * @return
	 */
	public static MainTabFragEnum getByIndex(int index) {
		return MainTabFragEnum.values()[index];
	}

}
