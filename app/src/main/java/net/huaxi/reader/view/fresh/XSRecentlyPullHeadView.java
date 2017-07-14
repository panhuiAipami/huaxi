package net.huaxi.reader.view.fresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;

import net.huaxi.reader.R;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;
import in.srain.cube.views.ptr.indicator.PtrTensionIndicator;

/**
 * Created by ZMW on 2016/4/11.
 */
public class XSRecentlyPullHeadView extends FrameLayout implements PtrUIHandler {

    PtrFrameLayout mPtrFrameLayout;
    PtrTensionIndicator mPtrTensionIndicator;
    private TextView tvCenter;
    private ImageView ivIcon;
    int duration = 300;
    ObjectAnimator mFilpAnimation;
    ObjectAnimator mReverseAnimation;

    public XSRecentlyPullHeadView(Context context) {
        super(context);
        init();
    }

    public XSRecentlyPullHeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public XSRecentlyPullHeadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setup(PtrFrameLayout ptrFrameLayout) {
        this.mPtrFrameLayout = ptrFrameLayout;
//        mPtrTensionIndicator = new PtrTensionIndicator();
//        mPtrFrameLayout.setPtrIndicator(mPtrTensionIndicator);
    }

    private void init() {
        View headview = LayoutInflater.from(getContext()).inflate(R.layout.shelf_recently_pull_head_view, this);
        tvCenter = (TextView) headview.findViewById(R.id.shelf_recently_pull_head_center_textview);
        ivIcon = (ImageView) headview.findViewById(R.id.shelf_recently_pull_head_left_imageview);
        initAnimation();
    }


    private void initAnimation() {
        mFilpAnimation = ObjectAnimator.ofFloat(ivIcon, "rotation", 0.0f, 180.0f).setDuration(duration);
        mFilpAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                ivIcon.setRotation(180f);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ivIcon.setRotation(180f);
            }
        });
        mReverseAnimation = ObjectAnimator.ofFloat(ivIcon, "rotation", 180.0f, 0.0f).setDuration(duration);
        mReverseAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ivIcon.setRotation(0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                ivIcon.setRotation(0);
            }
        });
    }

    //重新初始化
    @Override
    public void onUIReset(PtrFrameLayout frame) {

    }

    //准备显示最近阅读
    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        ivIcon.setRotation(0);
        tvCenter.setText(R.string.shelf_recently_pull_torefresh);
    }

    //开始刷新
    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
    }

    //刷新完成
    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
        final int mOffsetToRefresh = frame.getOffsetToRefresh();
        final int currentPos = ptrIndicator.getCurrentPosY();
        final int lastPos = ptrIndicator.getLastPosY();

        if (currentPos < mOffsetToRefresh && lastPos >= mOffsetToRefresh) {
            if (isUnderTouch && status == PtrFrameLayout.PTR_STATUS_PREPARE) {
                tvCenter.setText(R.string.shelf_recently_pull_torefresh);
                if(mReverseAnimation.isRunning()){
                    mReverseAnimation.cancel();
                }
                mReverseAnimation.start();
            }
        } else if (currentPos > mOffsetToRefresh && lastPos <= mOffsetToRefresh) {
            if (isUnderTouch && status == PtrFrameLayout.PTR_STATUS_PREPARE) {
                tvCenter.setText(R.string.shelf_recently_let_go);
                if(mFilpAnimation.isRunning()){
                    mFilpAnimation.cancel();
                }
                mFilpAnimation.start();
            }
        }
    }

}
