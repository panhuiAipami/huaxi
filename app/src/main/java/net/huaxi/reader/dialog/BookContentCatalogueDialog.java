package net.huaxi.reader.dialog;


import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tools.commonlibs.dialog.BaseDialog;
import net.huaxi.reader.activity.BookDetailActivity;
import net.huaxi.reader.adapter.XSAdapter;
import net.huaxi.reader.book.ChapterClickListener;
import net.huaxi.reader.book.SharedPreferenceUtil;
import net.huaxi.reader.book.datasource.DataSourceManager;
import net.huaxi.reader.common.AppContext;
import net.huaxi.reader.common.Constants;
import net.huaxi.reader.db.model.ChapterTable;
import net.huaxi.reader.util.XSFileUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.huaxi.reader.R;
import net.huaxi.reader.activity.BookContentActivity;
import net.huaxi.reader.adapter.XSViewHolder;
import net.huaxi.reader.db.dao.ChapterContentDao;

/**
 * Created by Saud on 16/1/5.
 */
public class BookContentCatalogueDialog extends BaseDialog implements AdapterView.OnItemClickListener, View.OnClickListener {

    private static final int COUNT_NUM = 10;
    private static final int NUM = 6;
    private View headerLine;
    private View llCatalogue, llCatalogClose, llSeriation;
    private ImageView ivSeriation;
    private ImageView ivCatalogClose;
    private TextView tvCatalogueHeader, tvSeriation, tvChapterHead;
    private Activity activity;
    private ListView lvCatalogue;
    private int theme;
    private List<ChapterTable> list = new ArrayList<>();
    private XSAdapter<ChapterTable> adapter;
    private ChapterClickListener chapterClickListener;
    private boolean isSeriation = true;     //顺序排列标志


    public BookContentCatalogueDialog(Activity activity) {
        this.activity = activity;

        initDialog(activity, null, R.layout.dialog_readpager_catalogue, BaseDialog.TYPE_CENTER_NOMENUCANCEL, true);
        mDialog.getWindow().setWindowAnimations(R.style.RightPopupDialog);
//        if (activity instanceof BookContentActivity) {
//            if (Build.VERSION.SDK_INT > 18) {
//                mDialog.getWindow().setFlags(
//                        WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            }
//        }
        WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;

        llCatalogClose = mDialog.findViewById(R.id.ll_bookconten_catalogue_btn);
        lvCatalogue = (ListView) mDialog.findViewById(R.id.lv_bookcontent_catalogue);
        lvCatalogue.setOnItemClickListener(this);
        ivCatalogClose = (ImageView) mDialog.findViewById(R.id.iv_bookconten_catalogue_back);
        llCatalogue = mDialog.findViewById(R.id.ll_catalogue);
        headerLine = mDialog.findViewById(R.id.line);
        tvCatalogueHeader = (TextView) mDialog.findViewById(R.id.tv_bookcontent_catalogue_head);
        tvChapterHead = (TextView) mDialog.findViewById(R.id.tv_bookcontent_catatlogue_chapter_head);
        tvSeriation = (TextView) mDialog.findViewById(R.id.tv_seriation);
        ivSeriation = (ImageView) mDialog.findViewById(R.id.iv_seriation);
        llSeriation = mDialog.findViewById(R.id.ll_seriation);
        llSeriation.setOnClickListener(this);
        llCatalogClose.setOnClickListener(this);


        initView();
        if (activity instanceof BookContentActivity) {//阅读页面的目录
            Map<Integer, ChapterTable> hashMap = DataSourceManager.getSingleton().getCatalogMap();
            for (int i = 1; i < hashMap.size() + 1; i++) {
                list.add(hashMap.get(i));
            }
            bindData();
        }
    }

    /**
     * 绑定数据
     */
    private void bindData() {
        if (list != null) {
            adapter.setmDatas(list);
            adapter.notifyDataSetChanged();
            final String text = String.format(AppContext.context().getString(R.string.chapter_count), list.size());
            tvChapterHead.setText(text);
        }
    }


    //数据库获取数据


    /**
     * 设置目录的样式
     */
    private void setStyle() {
        if (activity instanceof BookContentActivity) {
            theme = SharedPreferenceUtil.getInstanceSharedPreferenceUtil().getBookContentTheme();
        } else if (activity instanceof BookDetailActivity) {
            theme = 0;
        }
        ivCatalogClose.setSelected(false);
        ivCatalogClose.setAlpha(0.5f);
        ivSeriation.setImageResource(R.mipmap.catalog_seriation_light);


        switch (theme) {
            case 0:
                llCatalogue.setBackgroundColor(getColorId(R.color.bookcontent_type0_background1));
                headerLine.setBackgroundResource(R.color.bookcontent_type0_listview_driver);
                lvCatalogue.setDivider(activity.getResources().getDrawable(R.color.bookcontent_type0_listview_driver));
                lvCatalogue.setDividerHeight(1);
                tvCatalogueHeader.setTextColor(getColorId(R.color.catalogue_type0_title_textcolor));
                tvChapterHead.setTextColor(getColorId(R.color.bookcontent_type0_textcolor2));
                tvSeriation.setTextColor(getColorId(R.color.catalogue_download_textcolor));
                break;
            case 1:
                llCatalogue.setBackgroundResource(R.mipmap.background_pink);
                headerLine.setBackgroundResource(R.color.bookcontent_type1_listview_driver);
                lvCatalogue.setDivider(activity.getResources().getDrawable(R.color.bookcontent_type1_listview_driver));
                lvCatalogue.setDividerHeight(1);
                tvCatalogueHeader.setTextColor(getColorId(R.color.catalogue_type1_title_textcolor));
                tvChapterHead.setTextColor(getColorId(R.color.bookcontent_type1_textcolor2));
                tvSeriation.setTextColor(getColorId(R.color.catalogue_download_textcolor));

                break;
            case 2:
                llCatalogue.setBackgroundColor(getColorId(R.color.bookcontent_type2_background1));
                headerLine.setBackgroundResource(R.color.bookcontent_type2_listview_driver);
                lvCatalogue.setDivider(activity.getResources().getDrawable(R.color.bookcontent_type2_listview_driver));
                lvCatalogue.setDividerHeight(1);
                tvCatalogueHeader.setTextColor(getColorId(R.color.catalogue_type2_title_textcolor));
                tvChapterHead.setTextColor(getColorId(R.color.bookcontent_type2_textcolor2));
                tvSeriation.setTextColor(getColorId(R.color.catalogue_download_textcolor));

                break;
            case 3:
                llCatalogue.setBackgroundResource(R.mipmap.background_yellow);
                headerLine.setBackgroundResource(R.color.bookcontent_type3_listview_driver);
                lvCatalogue.setDivider(activity.getResources().getDrawable(R.color.bookcontent_type3_listview_driver));
                lvCatalogue.setDividerHeight(1);
                tvCatalogueHeader.setTextColor(getColorId(R.color.catalogue_type3_title_textcolor));
                tvChapterHead.setTextColor(getColorId(R.color.bookcontent_type3_textcolor2));
                tvSeriation.setTextColor(getColorId(R.color.catalogue_download_textcolor));

                break;
            case 4:
                llCatalogue.setBackgroundColor(getColorId(R.color.bookcontent_type4_background1));
                headerLine.setBackgroundResource(R.color.bookcontent_type4_listview_driver);
                lvCatalogue.setDivider(activity.getResources().getDrawable(R.color.bookcontent_type4_listview_driver));
                lvCatalogue.setDividerHeight(1);
                tvCatalogueHeader.setTextColor(getColorId(R.color.catalogue_type4_title_textcolor));
                tvChapterHead.setTextColor(getColorId(R.color.bookcontent_type4_textcolor2));
                tvSeriation.setTextColor(getColorId(R.color.bookcontent_type4_textcolor3));
                ivCatalogClose.setSelected(true);
                ivSeriation.setImageResource(R.mipmap.catalog_seriation_dark);

                break;
            default:

                break;
        }
    }


    private void initView() {

        lvCatalogue.setAdapter(adapter = new XSAdapter<ChapterTable>(activity.getApplicationContext(), R.layout
                .item_bookcontent_catalogue, list) {
            @Override
            public void convert(XSViewHolder holder, ChapterTable chapterTable) {
                if (chapterTable != null) {
                    holder.setText(R.id.tv_bookcontent_catatlogue_Chapter_text, chapterTable.getName());
                    setReadingState(holder,chapterTable);
                    setVipState(holder, chapterTable);
//                    SetViewDataStyle(holder, chapterTable);
                }
            }

        });
    }

    /**
     * 设置阅读状态
     * @param holder
     */
    private void setReadingState(XSViewHolder holder,ChapterTable chapterTable) {
        if (holder != null && chapterTable != null) {
//            LogUtils.debug("position = " + (holder.getPosition() + 1) + " chapterno =" + DataSourceManager.getSingleton()
//                    .getCurrentChapterNo());
            if (activity instanceof BookContentActivity) {
                theme = SharedPreferenceUtil.getInstanceSharedPreferenceUtil().getBookContentTheme();
            } else if (activity instanceof BookDetailActivity) {
                theme = 0;
            }
            int selectedColor = R.color.c01_themes_color;
            int readedColor = R.color.readpage_catelog_readed;
            int noclickColor = R.color.readpage_catelog_noclick;
            if (theme == Constants.THEME_NIGHT) {
                readedColor = R.color.readpage_catelog_night_readed;
                noclickColor = R.color.readpage_catelog_night_noclick;
            }
            int position = holder.getPosition() + 1;
            if (!isSeriation) {
                position = DataSourceManager.getSingleton().getChapterCount() - position + 1;
            }

            if (position == DataSourceManager.getSingleton().getCurrentChapterNo()) {
                holder.setTextColor(R.id.tv_bookcontent_catatlogue_Chapter_text, getColorId(selectedColor));
            } else {
                if (ChapterContentDao.getInstance().isReaded(chapterTable.getBookId(), chapterTable.getChapterId())){
                    holder.setTextColor(R.id.tv_bookcontent_catatlogue_Chapter_text, getColorId(readedColor));
                }else {
                    holder.setTextColor(R.id.tv_bookcontent_catatlogue_Chapter_text, getColorId(noclickColor));
                }
            }
        }
    }

    /**
     * 设置VIP标识
     * @param holder
     * @param chapterTable
     */
    private void setVipState(XSViewHolder holder, ChapterTable chapterTable) {
        if (chapterTable.getIsVip() == 0 || chapterTable.getIsSubscribed() == 1 || XSFileUtils.ChapterExists(chapterTable.getBookId(), chapterTable.getChapterId()))
        {
            holder.setVisible(R.id.vip_lock, false);

        } else {

            holder.setVisible(R.id.vip_lock, true);
        }
    }

    /**
     * 根据样式，设置列表的样式，和数据
     *
     * @param holder
     * @param chapterTable
     */
    private void SetViewDataStyle(XSViewHolder holder, ChapterTable chapterTable) {
        switch (theme) {
            case 0:

                holder.setTextColor(R.id.tv_bookcontent_catatlogue_Chapter_text, BookContentCatalogueDialog.this.getColorId(R.color
                        .catalogue_type0_title_textcolor));

                break;
            case 1:

                holder.setTextColor(R.id.tv_bookcontent_catatlogue_Chapter_text, getColorId(R.color.catalogue_type1_title_textcolor));

                break;
            case 2:

                holder.setTextColor(R.id.tv_bookcontent_catatlogue_Chapter_text, getColorId(R.color.catalogue_type2_title_textcolor));

                break;
            case 3:

                holder.setTextColor(R.id.tv_bookcontent_catatlogue_Chapter_text, getColorId(R.color.catalogue_type3_title_textcolor));

                break;
            case 4:

                holder.setTextColor(R.id.tv_bookcontent_catatlogue_Chapter_text, getColorId(R.color.catalogue_type4_title_textcolor));
                break;
            default:
                break;
        }

    }

    private int getColorId(int id) {
        return activity.getResources().getColor(id);
    }

    public Dialog getDialog() {
        return mDialog;
    }

    public void show() {
        setStyle();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        setScollIndex();
        mDialog.show();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (list != null) {
            ChapterTable chapterTable = list.get(position);
            if (chapterTable != null) {
                String chpterId = chapterTable.getChapterId();
                if (chapterClickListener != null) {
                    chapterClickListener.onCatalogItemClick(chpterId);
                }
            }
        }
        mDialog.dismiss();
    }

    public void setChapterClickListener(ChapterClickListener chapterClickListener) {
        this.chapterClickListener = chapterClickListener;

    }

    /**
     * 设置list显示到当前阅读章节的位置
     */
    private void setScollIndex() {
        if (isSeriation) {
            final int chapterNum = DataSourceManager.getSingleton().getCurrentChapterNo() + 1;
            if (chapterNum > COUNT_NUM) {
                lvCatalogue.setSelection(chapterNum - NUM);
            } else {
                lvCatalogue.setSelection(0);
            }
        }else{
            final int position = DataSourceManager.getSingleton().getChapterCount() - DataSourceManager.getSingleton().getCurrentChapterNo();
            if(position > COUNT_NUM){
                lvCatalogue.setSelection(position - NUM + 1);
            }else{
                lvCatalogue.setSelection(DataSourceManager.getSingleton().getChapterCount()-1);
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_bookconten_catalogue_btn:
                mDialog.dismiss();
                break;
            case R.id.ll_seriation:
                isSeriation = !isSeriation;
                if (isSeriation) {
                    tvSeriation.setText(R.string.readpage_catalog_permutation);
                } else {
                    tvSeriation.setText(R.string.readpage_catalog_positive_sequence);
                }
                Collections.reverse(list);
                adapter.notifyDataSetChanged();
                setScollIndex();
                break;

            default:
                break;
        }
    }
}
