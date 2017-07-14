package net.huaxi.reader.adapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.huaxi.reader.util.listener.ItemTouchHelperAdapter;
import net.huaxi.reader.util.listener.ItemTouchHelperViewHolder;
import net.huaxi.reader.util.OnStartDragListener;

/**
 * Created by ZMW on 2015/12/8.
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_FOOTER = 2;
    public List<T> mDatas = new ArrayList<T>();

    private View mHeaderView;
    private View mFooterView;

    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener li) {
        mListener = li;
    }

    private OnStartDragListener dragListener;

    public void setDragListener(OnStartDragListener listener) {
        dragListener = listener;
    }

    boolean preventDrag;//阻止刷新

    public void setHeaderView(View headerView) {
        notifyItemInserted(0);
        mHeaderView = headerView;
    }

    public void removeHeadView() {
        if (mHeaderView != null) {
            mHeaderView = null;
            notifyItemRemoved(0);
        }
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    public void setFooterView(View footerView) {
        notifyItemInserted(getItemCount() - 1);
        mFooterView = footerView;
    }

    public void removeFooterView() {
        if (mHeaderView != null) {
            mFooterView = null;
            notifyItemRemoved(getItemCount() - 1);
        }
    }

    public void addDatas(ArrayList<T> datas) {
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderView == null && mFooterView == null) return TYPE_NORMAL;
        if (mHeaderView != null && position == 0) return TYPE_HEADER;
        if (mFooterView != null && position == getItemCount() - 1) return TYPE_FOOTER;
        return TYPE_NORMAL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        if (mHeaderView != null && viewType == TYPE_HEADER) return new Holder(mHeaderView);
        if (mFooterView != null && viewType == TYPE_FOOTER) return new Holder(mFooterView);
        return onCreate(parent, viewType);
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == TYPE_HEADER || getItemViewType(position) == TYPE_FOOTER)
            return;

        final int pos = getRealPosition(viewHolder);
        if (mDatas.size() < pos) {
            return;
        }
        final T data = mDatas.get(pos);
        onBind(viewHolder, pos, data);

        if (mListener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(pos, data);
                }
            });
            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (dragListener != null && !preventDrag) {
//                        dragListener.onStartDrag(viewHolder);
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (getItemViewType(position) == TYPE_HEADER || getItemViewType(position)
                            == TYPE_FOOTER)
                            ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp != null
                && lp instanceof StaggeredGridLayoutManager.LayoutParams
                && holder.getLayoutPosition() == 0 && mHeaderView != null) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;

            p.setFullSpan(true);
        }
        if (lp != null
                && lp instanceof StaggeredGridLayoutManager.LayoutParams
                && holder.getLayoutPosition() == getItemCount() - 1 && mFooterView != null) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
            p.setFullSpan(true);
        }

    }

    public int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        position = mHeaderView == null ? position : position - 1;
        if (position < 0) {
            position = mHeaderView == null ? 0 : 1;
        }
        return position;
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (mDatas != null) {
            count = count + mDatas.size();
            count = mHeaderView == null ? count : count + 1;
            count = mFooterView == null ? count : count + 1;
        }
        return count;
    }

    public abstract RecyclerView.ViewHolder onCreate(ViewGroup parent, final int viewType);

    public abstract void onBind(RecyclerView.ViewHolder viewHolder, int RealPosition, T data);

    public class Holder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        public Holder(View itemView) {
            super(itemView);
        }

        @Override
        public void onItemSelected() {
            itemView.setScaleX(1.1f);
            itemView.setScaleY(1.1f);
        }

        @Override
        public void onItemClear() {
            itemView.setScaleX(1f);
            itemView.setScaleY(1f);
        }
    }

    public interface OnItemClickListener<T> {
        void onItemClick(int position, T data);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
//        System.out.print("fromPosition==" + fromPosition + "--->");
//        System.out.println("toPosition==" + toPosition);
        if (mHeaderView != null) {
            Collections.swap(mDatas, fromPosition - 1, toPosition - 1);
        } else {
            Collections.swap(mDatas, fromPosition, toPosition);
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
//        mDatas.remove(position);
//        notifyItemRemoved(position);
    }

}
