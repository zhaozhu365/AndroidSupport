package com.hyena.framework.samples.widgets.pull;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.hyena.framework.app.widget.BaseUIRootLayout;
import com.hyena.framework.samples.R;
import com.hyena.framework.utils.AnimationUtils;
import com.hyena.framework.utils.UIUtils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;

/**
 * Created by yangzc on 16/9/14.
 */

public class Pull2Refresh extends BaseUIRootLayout {

    private static final int MODE_PULL_FROM_NONE = 0;
    private static final int MODE_PULL_FROM_START = 1;
    private static final int MODE_PULL_FROM_END = 2;

    private static final int MAX_MOVE_DISTANCE = UIUtils.dip2px(120);

    private View mTarget = null;

    private int mTouchSlop = 0;
    private float mInitialDownY = -1;
    private float mInitialMotionY = -1;
    private boolean mIsBeingDragged;
    private int mCurrentMode = MODE_PULL_FROM_NONE;

    private boolean mRefreshing, mLoadingMore;

    private IPullRefresh mHeaderRefreshPanel = null;
    private IPullRefresh mFooterRefreshPanel = null;

    public Pull2Refresh(Context context) {
        super(context);
        init();
    }

    public Pull2Refresh(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mHeaderRefreshPanel = new PullRefreshHeaderPanel(getContext());
        mFooterRefreshPanel = new Pull2RefreshFooterPanel(getContext());

        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

        int paddingTop = 0;
        if (mHeaderRefreshPanel != null) {
            RelativeLayout.LayoutParams headerParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, mHeaderRefreshPanel.getContentHeight());
            headerParams.addRule(ALIGN_PARENT_TOP);
            mHeaderRefreshPanel.setId(R.id.header);
            addView(mHeaderRefreshPanel, headerParams);
            paddingTop = -mHeaderRefreshPanel.getContentHeight();
            mHeaderRefreshPanel.setBackgroundColor(Color.RED);
        }

        int paddingBottom = 0;
        if (mFooterRefreshPanel != null) {
            RelativeLayout.LayoutParams footerParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, mFooterRefreshPanel.getContentHeight());
            footerParams.addRule(ALIGN_PARENT_BOTTOM);
            mFooterRefreshPanel.setId(R.id.footer);
            addView(mFooterRefreshPanel, footerParams);
            paddingBottom = -mFooterRefreshPanel.getContentHeight();
            mFooterRefreshPanel.setBackgroundColor(Color.YELLOW);
        }
        setPadding(0, paddingTop, 0, paddingBottom);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (mTarget == null)
            insureTarget();

        if (mTarget != null) {
            RelativeLayout.LayoutParams params = (LayoutParams) mTarget.getLayoutParams();
            params.addRule(RelativeLayout.BELOW, R.id.header);
            params.addRule(RelativeLayout.ABOVE, R.id.footer);
        }
    }

    private void insureTarget() {
        if (getChildCount() > 0) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (!(child instanceof IPullRefresh)) {
                    mTarget = child;
                    break;
                }
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mRefreshing || mLoadingMore)
            return false;

        if (mTarget == null) {
            insureTarget();
        }

        int action = MotionEventCompat.getActionMasked(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mIsBeingDragged = false;
                mInitialDownY = ev.getY();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                float y = ev.getY();
                float yDiff = y - mInitialDownY;
                if (Math.abs(yDiff) > mTouchSlop && !mIsBeingDragged) {
                    if (!canChildScrollUp() && yDiff > 1) {
                        //top
                        mCurrentMode = MODE_PULL_FROM_START;
                        mInitialMotionY = mInitialDownY + mTouchSlop;
                        mIsBeingDragged = true;
                    } else if (!canChildScrollDown() && yDiff < -1) {
                        //bottom
                        mCurrentMode = MODE_PULL_FROM_END;
                        mInitialMotionY = mInitialDownY + mTouchSlop;
                        mIsBeingDragged = true;
                    } else {
                        //NO-OP
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                mIsBeingDragged = false;
                break;
            }
        }
        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mIsBeingDragged = false;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                float y = event.getY();
                float overScroll = (mInitialMotionY - y) * .5f;
                if (mIsBeingDragged) {
                    if (mCurrentMode == MODE_PULL_FROM_START) {
                        overScroll = Math.max(Math.min(0, overScroll), -MAX_MOVE_DISTANCE);
                    } else if (mCurrentMode == MODE_PULL_FROM_END){
                        overScroll = Math.min(Math.max(0, overScroll), MAX_MOVE_DISTANCE);
                    } else {
                        overScroll = 0;
                    }
                    moveTarget(overScroll);
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                mIsBeingDragged = false;
                moveRelease();
                break;
            }
        }
        return true;
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean b) {
        if ((android.os.Build.VERSION.SDK_INT < 21 && mTarget instanceof AbsListView)
                || (mTarget != null && !ViewCompat.isNestedScrollingEnabled(mTarget))) {
            // Nope.
        } else {
            super.requestDisallowInterceptTouchEvent(b);
        }
    }

    public void setRefreshing(boolean isRefreshing) {
        if (mHeaderRefreshPanel != null) {
            this.mRefreshing = isRefreshing;
            mCurrentMode = MODE_PULL_FROM_START;
            if (isRefreshing) {
                scroll(0, -mHeaderRefreshPanel.getContentHeight());
                mHeaderRefreshPanel.setStatus(IPullRefresh.STATUS_REFRESH);
            } else {
                scroll(getScrollY(), 0);
                mHeaderRefreshPanel.setStatus(IPullRefresh.STATUS_RESET);
            }
        }
    }

    public void setLoadingMore(boolean isLoading) {
        if (mFooterRefreshPanel != null) {
            this.mLoadingMore = isLoading;
            mCurrentMode = MODE_PULL_FROM_END;
            if (isLoading) {
                scroll(0, mFooterRefreshPanel.getContentHeight());
                mFooterRefreshPanel.setStatus(IPullRefresh.STATUS_REFRESH);
            } else {
                scroll(getScrollY(), 0);
                mFooterRefreshPanel.setStatus(IPullRefresh.STATUS_RESET);
            }
        }
    }

    /**
     * 手势释放
     */
    private void moveRelease() {
        IPullRefresh pullItem = null;
        int toScrollY = 0;
        if (mCurrentMode == MODE_PULL_FROM_START) {
            pullItem = mHeaderRefreshPanel;
            toScrollY = -pullItem.getContentHeight();
        } else if (mCurrentMode == MODE_PULL_FROM_END) {
            pullItem = mFooterRefreshPanel;
            toScrollY = pullItem.getContentHeight();
        }

        if (pullItem != null) {
            if (Math.abs(getScrollY()) >= pullItem.getContentHeight()) {
                pullItem.setStatus(IPullRefresh.STATUS_REFRESH);
                scroll(getScrollY(), toScrollY);
                if (mCurrentMode == MODE_PULL_FROM_START) {
                    mRefreshing = true;
                    if (mRefreshListener != null) {
                        mRefreshListener.onRefresh();
                    }
                } else if(mCurrentMode == MODE_PULL_FROM_END) {
                    mLoadingMore = true;
                    if (mRefreshListener != null) {
                        mRefreshListener.onLoadMore();
                    }
                }
            } else {
                scroll(getScrollY(), 0);
            }
        }
    }

    /**
     * 手势拖动
     */
    private void moveTarget(float overScroll) {
        scrollTo(0, (int) overScroll);
        IPullRefresh pullItem = null;
        if (mCurrentMode == MODE_PULL_FROM_START) {
            pullItem = mHeaderRefreshPanel;
        } else if (mCurrentMode == MODE_PULL_FROM_END) {
            pullItem = mFooterRefreshPanel;
        }

        if (pullItem != null) {
            pullItem.setScrolling(overScroll, pullItem.getContentHeight());
            if (Math.abs(overScroll) >= pullItem.getContentHeight()) {
                pullItem.setStatus(IPullRefresh.STATUS_READY_REFRESH);
            } else {
                pullItem.setStatus(IPullRefresh.STATUS_START_PULL);
            }
        }
    }

    /**
     * 滚动回退
     * @param fromScroll 开始位置
     * @param toScroll 结束位置
     */
    private void scroll(final float fromScroll, final float toScroll) {
        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setDuration(200);
        animator.setInterpolator(new LinearInterpolator());
        AnimationUtils.ValueAnimatorListener listener = new AnimationUtils.ValueAnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                scrollTo(0, (int) fromScroll);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                scrollTo(0, (int) toScroll);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                scrollTo(0, (int) toScroll);
            }

            @Override
            public void onAnimationRepeat(Animator animator) {}

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Float value = (Float) valueAnimator.getAnimatedValue();
                float newScrollValue = value * (toScroll - fromScroll) + fromScroll;
                scrollTo(0, (int) newScrollValue);
            }
        };
        animator.addUpdateListener(listener);
        animator.addListener(listener);
        animator.start();
    }

    /**
     * 是否可以向上滚动
     */
    private boolean canChildScrollUp() {
        if (Build.VERSION.SDK_INT < 14) {
            if (mTarget instanceof AbsListView) {
                AbsListView listView = (AbsListView) mTarget;
                return listView.getChildCount() > 0
                        && (listView.getFirstVisiblePosition() > 0
                        || listView.getChildAt(0).getTop() < listView.getPaddingTop());
            } else {
                return mTarget.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mTarget, -1);
        }
    }

    /**
     * 是否可以向下滚动
     */
    private boolean canChildScrollDown() {
        if (!canChildScrollUp()) {//less data
            return true;
        }
        if (mTarget instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) mTarget;
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            int count = recyclerView.getAdapter().getItemCount();
            if (layoutManager instanceof LinearLayoutManager && count > 0) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == count - 1) {
                    return false;
                }
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                int[] lastItems = new int[4];
                staggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(lastItems);
                int lastItem = max(lastItems);
                if (lastItem == count - 1) {
                    return false;
                }
            }
            return true;
        } else if (mTarget instanceof AbsListView) {
            final AbsListView absListView = (AbsListView) mTarget;
            int count = absListView.getAdapter().getCount();
            int firstVisiblePosition = absListView.getFirstVisiblePosition();
            if (firstVisiblePosition == 0
                    && absListView.getChildAt(0).getTop() >= absListView
                    .getPaddingTop()) {
                return false;
            }
            int lastPos = absListView.getLastVisiblePosition();
            return lastPos > 0 && count > 0 && lastPos == count - 1;
        } else if (mTarget instanceof ScrollView) {
            ScrollView scrollView = (ScrollView) mTarget;
            View view = scrollView.getChildAt(scrollView.getChildCount() - 1);
            if (view != null) {
                int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));
                if (diff == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private int max(int[] a){
        // 返回数组最大值
        int x;
        int aa[]=new int[a.length];
        System.arraycopy(a,0,aa,0,a.length);
        x=aa[0];
        for(int i=1;i<aa.length;i++){
            if(aa[i]>x){
                x=aa[i];
            }
        }
        return x;
    }

    private OnRefreshListener mRefreshListener;

    public void setRefreshListener(OnRefreshListener listener) {
        this.mRefreshListener =  listener;
    }
    public static interface OnRefreshListener {
        void onRefresh();
        void onLoadMore();
    }
}
