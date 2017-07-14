package net.huaxi.reader.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import net.huaxi.reader.activity.MainActivity;
import net.huaxi.reader.common.MainTabFragEnum;

public class AdapterMainFragmentPager extends FragmentStatePagerAdapter {

//	private MainActivity MyOnPageChangeAdapter;
	private MainTabFragEnum[] mainTabFrags;
	private Fragment[] fragments;

	public AdapterMainFragmentPager(MainActivity mainActivity,
			FragmentManager fm, MainTabFragEnum[] mainTabFrags) {
		super(fm);
//		MyOnPageChangeAdapter = mainActivity;
		this.mainTabFrags = mainTabFrags;
		this.fragments = new Fragment[mainTabFrags.length];
	}

	public Fragment getFragment(int index) {
		return fragments[index];
	}

	@Override
	public int getCount() {
		return mainTabFrags.length;
	}

	@Override
	public Fragment getItem(int index) {
		fragments[index] = mainTabFrags[index].createFrag();
		return fragments[index];
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		super.destroyItem(container, position, object);
	}
}
