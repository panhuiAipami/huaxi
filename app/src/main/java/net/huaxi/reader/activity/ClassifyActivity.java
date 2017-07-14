package net.huaxi.reader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.tools.commonlibs.activity.BaseActivity;
import com.tools.commonlibs.tools.CircularAnim;
import com.tools.commonlibs.tools.Utils;
import net.huaxi.reader.appinterface.ClassifyPresenter;
import net.huaxi.reader.util.UMEventAnalyze;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import net.huaxi.reader.R;

import net.huaxi.reader.appinterface.ClassifyViewListener;
import net.huaxi.reader.bean.CatalogBean;
import net.huaxi.reader.bean.ClassifyChildBean;
import net.huaxi.reader.presenter.ClassifyPresenterImpl;
import net.huaxi.reader.util.ImageUtil;
import net.huaxi.reader.view.divider.HorizontalDividerItemDecoration;

/**
 * 下面是分类借口的相关参数
 * <p/>
 * 参数	             参数说明	       是否可以为空	  默认值	       备注
 * cat_mid	          分类mid	         是       	无
 * cat_pmid	          父分类mid	         是	        无
 * vip	              是否收费	         是	        无	       0 免费 1 收费 2 会员
 * offset	          起始偏移值	         是        	0          分页开始书籍下标
 * limit              每页条数	         是	        20	       分页条数
 * orderby	          排序规则	         是		    0          1 收藏排序，2字数排序
 */

/**
 * @Description: [分类子页面的activity]
 * @Author: [Saud]
 * @CreateDate: [16/7/19 16:18]
 * @UpDate: [16/7/19 16:18]
 * @Version: [v1.0]
 */
public class ClassifyActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener,
        ClassifyViewListener, BaseQuickAdapter.RequestLoadMoreListener,
        SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.OnRecyclerViewItemClickListener, View.OnTouchListener {

    private static final int CLASSIFY_LOADING_COUNT = 30;
    private ClassifyAdapter mClassifyAdapter;
    private boolean isLoadingMore = false;
    private ClassifyPresenter mClassifyPresenter;
    private String mVipType;
    private String mOrderby;

    @BindView(R.id.off_text_tag1)
    TextView mOffTextTag1;
    @BindView(R.id.off_text_tag2)
    TextView mOffTextTag2;
    @BindView(R.id.off_text_tag3)
    TextView mOffTextTag3;
    @BindView(R.id.menu_off_layout)
    LinearLayout mMenuOffLayout;
    @BindView(R.id.group_classify)
    RadioGroup mGroupClassify;
    @BindView(R.id.child_pay_no)
    RadioButton mChildPayNo;
    @BindView(R.id.child_pay_yes)
    RadioButton mChildPayYes;
    @BindView(R.id.child_pay_vip)
    RadioButton mChildPayVip;
    @BindView(R.id.child_pay_free)
    RadioButton mChildPayFree;
    @BindView(R.id.group_pay_type)
    RadioGroup mGroupPayType;
    @BindView(R.id.child_other_sentiment)
    RadioButton mChildOtherSentiment;
    @BindView(R.id.child_other_collect)
    RadioButton mChildOtherCollect;
    @BindView(R.id.child_other_num)
    RadioButton mChildOtherNum;
    @BindView(R.id.group_other)
    RadioGroup mGroupOther;
    @BindView(R.id.menu_on_layout)
    LinearLayout mMenuOnLayout;
    @BindView(R.id.rvBooks)
    RecyclerView mRvBooks;
    @BindView(R.id.classify_title)
    TextView mClassifyTitle;
    @BindView(R.id.swipeLayout)
    SwipeRefreshLayout mSwipeLayout;
    @BindView(R.id.actionbar)
    View actionBar;
    @BindView(R.id.classify_book_null)
    View mLayoutBookNull;
    @BindView(R.id.bookshelf_nobook_image)
    ImageView mIvBookNull;
    @BindView(R.id.bookshelf_nobook_textview)
    TextView mTvBookNull;
    private String mParentId;
    private String mChildID;
    private int selectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classify);
        ButterKnife.bind(this);
        initListener();
        initAdapter();
        initData();
    }


    @Override
    protected void onResume() {
        super.onResume();
        UMEventAnalyze.countEvent(getActivity(), UMEventAnalyze.CLASSIFY_CHILD_PAGE);
    }

    private void initData() {
        Intent intent = getIntent();
        Serializable bean = intent.getSerializableExtra("obj");
        if (bean instanceof CatalogBean) {
            CatalogBean catalogBean = (CatalogBean) bean;
            mClassifyTitle.setText(catalogBean.getName());
            if (catalogBean != null) {
                addTitleView(catalogBean);
            }
        }
        selectId = R.id.child_other_sentiment;
        //  网络请求
        mClassifyPresenter = new ClassifyPresenterImpl(this, mParentId);
        mClassifyPresenter.loadingData(CLASSIFY_LOADING_COUNT, 0, "", "", "");
    }

    /**
     * 给展开的title第一行元素，以为是每个类目不同，必须动态添加
     *
     * @param catalogBean
     */
    private void addTitleView(CatalogBean catalogBean) {
        mGroupClassify.removeAllViews();
        mParentId = catalogBean.getId();
        Map<String, String> subclass = catalogBean.getSubclass();
        if (subclass != null) {
            int id = 0;
            TextView textView = createView(getString(R.string.classify_all), "", id);
            mGroupClassify.addView(textView);
            Set<String> classIds = subclass.keySet();
            Iterator<String> iterator = classIds.iterator();
            while (iterator.hasNext()) {
                id++;
                String classifyId = iterator.next();
                String classifyName = subclass.get(classifyId);
                mGroupClassify.addView(createView(classifyName, classifyId, id));

            }
        }
    }

    private void initListener() {
        mGroupClassify.setOnCheckedChangeListener(this);
        mGroupPayType.setOnCheckedChangeListener(this);
        mGroupOther.setOnCheckedChangeListener(this);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(R.color.c01_themes_color);
        mRvBooks.setOnTouchListener(this);
    }

    private void initAdapter() {
        mRvBooks.setLayoutManager(new LinearLayoutManager(this));
        mRvBooks.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(getResources().getColor(R.color.c09_divider_color))
                .sizeResId(R.dimen.divider)
                .build());
        mClassifyAdapter = new ClassifyAdapter(R.layout.item_classify_child, new ArrayList<ClassifyChildBean.VdataBean.ListBean>());
        mClassifyAdapter.setOnLoadMoreListener(this);
        mClassifyAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        mClassifyAdapter.setOnRecyclerViewItemClickListener(this);
        mClassifyAdapter.openLoadMore(CLASSIFY_LOADING_COUNT, true);
        final View customLoading = getLayoutInflater().inflate(R.layout.custom_loading, (ViewGroup) mRvBooks.getParent(), false);
        mClassifyAdapter.setLoadingView(customLoading);
        mRvBooks.setAdapter(mClassifyAdapter);
    }


    @OnClick({R.id.back, R.id.menu_off_layout, R.id.classify_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.menu_off_layout:
                mMenuOnLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.classify_search:
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                UMEventAnalyze.countEvent(getActivity(), UMEventAnalyze.CLASSIFY_CHILD_SEARCH);
                break;
        }
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (group.getId()) {
            case R.id.group_classify:
                setOffText(group, checkedId, mOffTextTag1);
                break;
            case R.id.group_pay_type:
                setOffText(group, checkedId, mOffTextTag2);
                break;
            case R.id.group_other:
                setOffText(group, checkedId, mOffTextTag3);
                break;
            default:
                break;
        }
        statisticsData(checkedId);
        hiddenHeader();
    }

    //统计用户点击收费方式的次数
    private void statisticsData(int id) {
        switch (id) {
            case R.id.child_pay_yes:
//                System.out.println("付费=" + id);
                UMEventAnalyze.countEvent(this, UMEventAnalyze.CLASSIFY_PAY_YES);
                break;
            case R.id.child_pay_vip:
//                System.out.println("vip=" + id);
                UMEventAnalyze.countEvent(this, UMEventAnalyze.CLASSIFY_PAY_VIP);
                break;
            case R.id.child_pay_free:
//                System.out.println("免费=" + id);
                UMEventAnalyze.countEvent(this, UMEventAnalyze.CLASSIFY_PAY_FREE);
                break;
            case R.id.child_other_sentiment:
//                System.out.println("人气=" + id);
                this.selectId = id;
                UMEventAnalyze.countEvent(this, UMEventAnalyze.CLASSIFY_PAY_SENTIMENT);
                break;
            case R.id.child_other_collect:
                this.selectId = id;
//                System.out.println("收藏=" + id);
                UMEventAnalyze.countEvent(this, UMEventAnalyze.CLASSIFY_PAY_COLLECT);
                break;
            case R.id.child_other_num:
                this.selectId = id;
//                System.out.println("字数=" + id);
                UMEventAnalyze.countEvent(this, UMEventAnalyze.CLASSIFY_PAY_NUM);
                break;
            default:
                break;
        }
    }

    /**
     * 更具点击选择的选项，设置关闭选项后的
     *
     * @param group
     * @param checkedId
     * @param view
     */
    private void setOffText(RadioGroup group, int checkedId, TextView view) {
        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            RadioButton radioButton = (RadioButton) child;
            radioButton.setTextColor(getResources().getColor(R.color.c05_themes_color));
            if (radioButton.getId() == checkedId) {
                String tag = (String) radioButton.getTag();
                radioButton.setTextColor(getResources().getColor(R.color.c01_themes_color));
                CharSequence text = radioButton.getText();
                view.setText(text);
                view.setTag(tag);
            }
        }

        if (mClassifyPresenter != null) {
            mChildID = (String) mOffTextTag1.getTag();
            mVipType = (String) mOffTextTag2.getTag();
            mOrderby = (String) mOffTextTag3.getTag();
            mClassifyPresenter.loadingData(CLASSIFY_LOADING_COUNT, 0, mVipType, mOrderby, mChildID);
        }
    }

    @Override
    public void onDataList(ClassifyChildBean bean) {

        if (bean != null) {
            List<ClassifyChildBean.VdataBean.ListBean> list = bean.getVdata().getList();
            if (list != null) {
                if (isLoadingMore) {
                    isLoadingMore = false;
                    mClassifyAdapter.notifyDataChangedAfterLoadMore(list, true);
                } else {
                    mClassifyAdapter.setNewData(list);
                }
                if (bean.getVdata().getNums() == mClassifyAdapter.getData().size()) {//加载到了最后一条
                    mClassifyAdapter.notifyDataChangedAfterLoadMore(false);
//                    View view = getLayoutInflater().inflate(R.layout.not_loading, (ViewGroup) mRvBooks.getParent(), false);
//                    mClassifyAdapter.addFooterView(view);
                }

            }
        } else {
            mClassifyAdapter.getData().clear();
        }
        mClassifyAdapter.notifyDataSetChanged();
        mSwipeLayout.setRefreshing(false);
        showBookNull(mClassifyAdapter.getData().size() == 0);
    }

    @Override
    public void onLoadMoreRequested() {
        isLoadingMore = true;
        int index = mClassifyAdapter.getData().size();
        mClassifyPresenter.loadingData(CLASSIFY_LOADING_COUNT, index, mVipType, mOrderby, mChildID);
    }


    @Override
    public void onRefresh() {
        mClassifyPresenter.loadingData(CLASSIFY_LOADING_COUNT, 0, mVipType, mOrderby, mChildID);
    }


    @Override
    public void onItemClick(View view, int i) {
        if (mMenuOnLayout.getVisibility() == View.VISIBLE) {
            hiddenHeader();
        } else {
            ClassifyChildBean.VdataBean.ListBean listBean = mClassifyAdapter.getData().get(i);
            String bookId = listBean.getBk_mid();
            Intent intent = new Intent(this, BookDetailActivity.class);
            intent.putExtra("bookid", bookId);
            startActivity(intent);
        }

    }

    private void hiddenHeader() {
        if (mMenuOnLayout.getVisibility() == View.VISIBLE) {
            mMenuOnLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            hiddenHeader();
        }
        if (mMenuOnLayout.getVisibility() == View.VISIBLE) {
            return true;
        }
        return false;
    }

    /*
       * 创建不同类型的View选项
       */
    private TextView createView(String text, String tag, int id) {
        RadioButton view = new RadioButton(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, Utils.dip2px(this, 30));
        lp.rightMargin = Utils.dip2px(this, 10);
        view.setLayoutParams(lp);
        view.setButtonDrawable(android.R.color.transparent);
        view.setText(text);
        view.setTextColor(getResources().getColor(R.color.c05_themes_color));
        if (id == 0) {
            view.setChecked(true);
            view.setTextColor(getResources().getColor(R.color.c01_themes_color));
        }
        view.setTextSize(14);
        view.setButtonDrawable(null);
        view.setGravity(Gravity.CENTER_VERTICAL);
        view.setPadding(Utils.dip2px(this, 10), 0, Utils.dip2px(this, 10), 0);
        view.setFocusableInTouchMode(false);
        view.setGravity(Gravity.CENTER);
        view.setBackgroundResource(R.drawable.selector_classify_radiobutton);
        view.setId(id);
        view.setTag(tag);
        return view;
    }

    /**
     * 列表适配器
     */
    private class ClassifyAdapter extends BaseQuickAdapter<ClassifyChildBean.VdataBean.ListBean> {
        public ClassifyAdapter(int layoutResId, List<ClassifyChildBean.VdataBean.ListBean> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, ClassifyChildBean.VdataBean.ListBean listBean) {
            ImageView view = (ImageView) baseViewHolder.getView(R.id.riv_book);
            ImageUtil.loadImage(mContext.getApplicationContext(), listBean.getBk_cover_imgid(), view, R.mipmap.detail_book_default);
            baseViewHolder.setText(R.id.book_name, listBean.getBk_title());
            baseViewHolder.setText(R.id.book_info, listBean.getBk_description());
            baseViewHolder.setText(R.id.author, listBean.getBp_au_pname());
            String str = "";
            switch (selectId) {
                case R.id.child_other_sentiment:
                    int viewsNum = Integer.parseInt(listBean.getBk_total_views());
                    String views = viewsNum <= 1000 ? 1000 + " " : viewsNum >= 10000 ? viewsNum / 10000 + "万 " : viewsNum + " ";
                    str = String.format(getString(R.string.classify_attennion), views);//人气
                    break;
                case R.id.child_other_collect:
                    int favorNum = Integer.parseInt(listBean.getBk_total_favor());
                    String favors = favorNum <= 1000 ? 1000 + " " : favorNum >= 10000 ? favorNum / 10000 + "万 " : favorNum + " ";
                    str = String.format(getString(R.string.classify_favor), favors);//收藏
                    break;
                case R.id.child_other_num:
                    int wordsNum = Integer.parseInt(listBean.getBk_total_words());
                    String words = wordsNum <= 1000 ? 1000 + " " : wordsNum >= 10000 ? wordsNum / 10000 + "万 " : wordsNum + " ";
                    str = String.format(getString(R.string.classify_text_numr), words);//字数
                    break;
                default:
                    break;
            }
            baseViewHolder.setText(R.id.attention_num, str);
        }
    }

    private void showBookNull(boolean visibility) {
        if (visibility) {
            mTvBookNull.setText("没有找到书籍信息");
            mIvBookNull.setImageResource(R.mipmap.common_book_not_found);
            CircularAnim.show(mLayoutBookNull).go();

        } else {
            mLayoutBookNull.setVisibility(View.INVISIBLE);
        }
    }


}
