package net.huaxi.reader.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tools.commonlibs.tools.LogUtils;
import net.huaxi.reader.common.MainTabFragEnum;

import net.huaxi.reader.R;

public class UITabBottom extends LinearLayout implements OnClickListener {

    public interface OnUITabChangeListener {
        public void onTabChange(int index);
    }

    // 按钮
    private UITabItem tab0;
    private UITabItem tab1;
    private UITabItem tab2;
    private UITabItem tab3;

    private ViewPager mViewPager;
    private OnUITabChangeListener changeListener;

    // 点击后的颜色和不点击的颜色
    private int colorClick;
    private int colorUnclick;

    private int R1;// 未选中的Red值
    private int G1;// 未选中的Green值
    private int B1;// 未选中的Blue值
    private int R2;// 选中的Red值
    private int G2;// 选中的Green值
    private int B2;// 选中的Blue值
    private int Rm = R2 - R1;// Red的差值
    private int Gm = G2 - G1;// Green的差值
    private int Bm = B2 - B1;// Blue的差值

    // 当前点击的位置
    private int mLocation = 0;

    public UITabBottom(Context context) {
        super(context);
        init();
    }

    public UITabBottom(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ViewPager getmViewPager() {
        return mViewPager;
    }

    public void setmViewPager(ViewPager mViewPager) {
        this.mViewPager = mViewPager;
    }

    private UITabItem newChildItem(int i) {
        UITabItem tabItem = new UITabItem();
        tabItem.parent = LayoutInflater.from(getContext()).inflate(
                R.layout.item_main_tab, null);
        tabItem.iconView = (TabIconView) tabItem.parent
                .findViewById(R.id.mTabIcon);
        tabItem.labelView = (TextView) tabItem.parent
                .findViewById(R.id.mTabText);
        tabItem.parent.setTag(i);
        tabItem.parent.setOnClickListener(this);
        return tabItem;
    }

    private void init() {
        colorClick = Color.parseColor("#F94A4E");
        colorUnclick = Color.parseColor("#666666");

        int tabBottomHeight = ViewGroup.LayoutParams.MATCH_PARENT;
        setOrientation(LinearLayout.HORIZONTAL);

        // tab0(书架)
        tab0 = newChildItem(MainTabFragEnum.bookshelf.getIndex());
        LayoutParams layoutParams = new LayoutParams(
                0, tabBottomHeight);
        layoutParams.weight = 1;
        tab0.labelView.setText(getContext().getString(R.string.book_shelf));
        tab0.labelView.setTextColor(colorClick);
        tab0.iconView.init(R.mipmap.main_navigation_bookshelf_pressed,
                R.mipmap.main_navigation_bookshelf_normal);
        addView(tab0.parent, layoutParams);

        // tab1(书城)
        tab1 = newChildItem(MainTabFragEnum.bookcity.getIndex());
        layoutParams = new LayoutParams(0, tabBottomHeight);
        layoutParams.weight = 1;
        tab1.labelView.setText(getContext().getString(R.string.book_city));
        tab1.labelView.setTextColor(colorUnclick);
        tab1.iconView.init(R.mipmap.main_navigation_bookcity_pressed,
                R.mipmap.main_navigation_bookcity_normal);
        addView(tab1.parent, layoutParams);

         // tab2(排行)
        tab2 = newChildItem(MainTabFragEnum.paihang.getIndex());
        layoutParams = new LayoutParams(0, tabBottomHeight);
        layoutParams.weight = 1;
        tab2.labelView.setText(getContext().getString(R.string.book_catalog));
        tab2.labelView.setTextColor(colorUnclick);
        tab2.iconView.init(R.mipmap.main_navigation_paihang_pressed,
        R.mipmap.main_navigation_paihang_normal);
        addView(tab2.parent, layoutParams);
        tab2.parent.setVisibility(GONE);


        // tab3(个人中心)
        tab3 = newChildItem(MainTabFragEnum.person.getIndex());
        layoutParams = new LayoutParams(0, tabBottomHeight);
        layoutParams.weight = 1;
        tab3.labelView.setText(getContext().getString(R.string.book_mine));
        tab3.labelView.setTextColor(colorUnclick);
        tab3.iconView.init(R.mipmap.main_navigation_usercenter_pressed,
                R.mipmap.main_navigation_usercenter_normal);
        addView(tab3.parent, layoutParams);

        R1 = (colorClick & 0xff0000) >> 16;
        G1 = (colorClick & 0xff00) >> 8;
        B1 = (colorClick & 0xff);
        R2 = (colorUnclick & 0xff0000) >> 16;
        G2 = (colorUnclick & 0xff00) >> 8;
        B2 = (colorUnclick & 0xff);
        Rm = R1 - R2;
        Gm = G1 - G2;
        Bm = B1 - B2;

        mLocation = 0;
    }

    public void setChangeListener(OnUITabChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    @Override
    public void onClick(View v) {
        Integer i = (Integer) v.getTag();
        mViewPager.setCurrentItem(i, false);
        selectTab(i);
    }

    // 点击tab执行
    public void selectTab(int index) {
        LogUtils.debug(" selectTab index = " + index);
        if (mLocation == index) {
            return;
        }

        mLocation = index;
        if (changeListener != null) {
            changeListener.onTabChange(mLocation);
        }

        tab0.iconView.setmAlpha(0);
        tab1.iconView.setmAlpha(0);
        tab2.iconView.setmAlpha(0);
        tab3.iconView.setmAlpha(0);
        tab0.labelView.setTextColor(colorUnclick);
        tab1.labelView.setTextColor(colorUnclick);
        tab2.labelView.setTextColor(colorUnclick);
        tab3.labelView.setTextColor(colorUnclick);
        switch (mLocation) {
            case 0:
                tab0.iconView.setmAlpha(255);
                tab0.labelView.setTextColor(colorClick);
                break;
            case 1:
                tab1.iconView.setmAlpha(255);
                tab1.labelView.setTextColor(colorClick);
                break;
            case 2:
                tab2.iconView.setmAlpha(255);
                tab2.labelView.setTextColor(colorClick);
                break;
            case 3:
                tab3.iconView.setmAlpha(255);
                tab3.labelView.setTextColor(colorClick);
                break;
            default:
                break;
        }

    }

    /**
     * 拼成颜色值
     *
     * @param f
     * @return
     */
    private int getColorInt(float f) {
        int R = (int) (R2 + f * Rm);
        int G = (int) (G2 + f * Gm);
        int B = (int) (B2 + f * Bm);
        return 0xff << 24 | R << 16 | G << 8 | B;

    }

    // 滑动tab执行
    public void scroll(int location, float f) {
        System.out.println("scroll.........................." + location + "precent:" + f);
        int leftAlpha = (int) (255 * (1 - f));
        int rightAlpha = (int) (255 * f);
        int leftColor = getColorInt(1 - f);
        int rightColor = getColorInt(f);
        switch (location) {
            case 0:
                tab0.iconView.setmAlpha(leftAlpha);
                tab1.iconView.setmAlpha(rightAlpha);
                tab0.labelView.setTextColor(leftColor);
                tab1.labelView.setTextColor(rightColor);
                break;
            case 1:
                tab1.iconView.setmAlpha(leftAlpha);
                tab2.iconView.setmAlpha(rightAlpha);
                tab1.labelView.setTextColor(leftColor);
                tab2.labelView.setTextColor(rightColor);
                break;
            case 2:
                tab2.iconView.setmAlpha(leftAlpha);
                tab3.iconView.setmAlpha(rightAlpha);
                tab2.labelView.setTextColor(leftColor);
                tab3.labelView.setTextColor(rightColor);
                break;
            default:
                break;
        }

    }

}
