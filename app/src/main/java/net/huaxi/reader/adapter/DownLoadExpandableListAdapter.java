package net.huaxi.reader.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.tools.commonlibs.common.CommonApp;
import net.huaxi.reader.activity.DownLoadActivity;
import net.huaxi.reader.util.XSFileUtils;

import java.util.List;

import net.huaxi.reader.R;

import net.huaxi.reader.bean.DownLoadChild;
import net.huaxi.reader.bean.DownLoadGroup;

/**
 * Created by Saud on 16/1/9.
 */
public class DownLoadExpandableListAdapter extends BaseExpandableListAdapter {


    private List<DownLoadGroup> groupList;
    private Activity ctx;
    private LayoutInflater mInflater;

    public DownLoadExpandableListAdapter(Activity ctx, List<DownLoadGroup> groupList) {
        this.groupList = groupList;
        this.ctx = ctx;
        mInflater = LayoutInflater.from(ctx);
    }


    @Override
    public int getGroupCount() {
        return groupList == null ? 0 : groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        if (groupList == null) {
            return 0;
        } else if (groupList.get(groupPosition) == null) {
            return 0;
        } else {
            List list = groupList.get(groupPosition).getChildList();
            return list == null ? 0 : list.size();
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groupList.get(groupPosition).getChildList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = newGroupView(parent);
        }
        ImageView ivDownloadGroup = (ImageView) convertView.findViewById(R.id.iv_download_group);
        CheckBox cbDownloadGroup = (CheckBox) convertView.findViewById(R.id.cb_download_proup);
        TextView tvDownloadGroups = (TextView) convertView.findViewById(R.id.tv_download_chapter_groups);
        if (isExpanded) {
            ivDownloadGroup.setSelected(true);
        } else {
            ivDownloadGroup.setSelected(false);
        }
        bindGroupView(tvDownloadGroups, cbDownloadGroup, groupPosition);
        return convertView;
    }

    /*
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = newChildView(parent);
        }
        CheckBox cbChild = (CheckBox) convertView.findViewById(R.id.cb_download_child);
        TextView tvChild = (TextView) convertView.findViewById(R.id.tv_download_chapter_child);
        TextView tvChildTag = (TextView) convertView.findViewById(R.id.tv_download_tag_child);
        bindChildView(tvChild, tvChildTag, cbChild, groupPosition, childPosition);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {

        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void setData(List<DownLoadGroup> groupList) {
        this.groupList = groupList;
    }

    /**
     * 创建新的组视图
     *
     * @param parent
     * @return
     */
    public View newGroupView(ViewGroup parent) {
        return mInflater.inflate(R.layout.item_download_groups, parent, false);
    }

    /**
     * 创建新的子视图
     *
     * @param parent
     * @return
     */
    public View newChildView(ViewGroup parent) {
        return mInflater.inflate(R.layout.item_download_childs, parent, false);
    }

    /**
     * 绑定数据
     *
     * @param groupPosition
     */
    private void bindGroupView(TextView tvGroups, CheckBox cbGroup, int groupPosition) {
        // 绑定组视图的数据，针对Group的Layout都是TextView的情况
        boolean isDownload = true;
        if (tvGroups != null) {
            final DownLoadGroup group = groupList.get(groupPosition);
            tvGroups.setText(group.getName());

            for (DownLoadChild child : group.getChildList()) {
                if (!((DownLoadActivity) ctx).chapterIsExist(child.getChapterId())) {//章节没有下载
                    isDownload = false;
                    if (!isDownload) {
                        break;
                    }
                }
            }
            if (cbGroup != null) {
                cbGroup.setSelected(isDownload);
                cbGroup.setChecked(!isDownload);
                cbGroup.setClickable(!isDownload);
                if (!isDownload) {
                    cbGroup.setChecked(groupList.get(groupPosition).isChecked());
                    cbGroup.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    group.changeChecked();
                                    notifyDataSetChanged();
                                    ((DownLoadActivity) ctx).countCheck();

                                }
                            }
                    );
                }

            }
        }
    }


    /**
     * 绑定数据
     *
     * @param groupPosition
     * @param childPosition
     */
    private void bindChildView(TextView tvChild, TextView tvChildTag, CheckBox cbChild, int
            groupPosition, int childPosition) {

        DownLoadChild child = groupList.get(groupPosition).getChildList().get(childPosition);

        if (tvChild != null) {
            tvChild.setText(child.getName());
        }
        if (tvChildTag != null) {
            tvChildTag.setVisibility(View.VISIBLE);
            if (XSFileUtils.ChapterExists(child.getBookId(), child.getChapterId())) {
                child.setChecked(false);
                cbChild.setSelected(true);
                tvChildTag.setText(CommonApp.context().getString(R.string.has_download));
                tvChildTag.setTextColor(CommonApp.context().getResources().getColor(R.color.c07_themes_color));
            } else if (child.getPrice() != 0) {
                cbChild.setSelected(false);
                String strPric = "";
                if (child.getIsSubscribe() == 1) {//已订购
                    strPric= ctx.getString(R.string.chapter_buy);
                } else {
                    strPric = String.format(CommonApp.context().getString(R.string.chapter_price), child.getPrice());
                }
                tvChildTag.setTextColor(CommonApp.context().getResources().getColor(R.color.c01_themes_color));
                tvChildTag.setText(strPric);
            } else {
                cbChild.setSelected(false);
                tvChildTag.setVisibility(View.GONE);
            }
        }

        if (cbChild != null) {
            cbChild.setChecked(child.isChecked());
        }


    }
}
