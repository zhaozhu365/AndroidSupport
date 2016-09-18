package com.hyena.framework.samples.widgets.pull;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hyena.framework.samples.R;
import com.hyena.framework.utils.AnimationUtils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;

/**
 * Created by yangzc on 16/9/14.
 */

public class Pull2Refresh extends RelativeLayout {

    private static final int MODE_PULL_FROM_NONE = 0;
    private static final int MODE_PULL_FROM_START = 1;
    private static final int MODE_PULL_FROM_END = 2;

    private View mTarget = null;

    private int mTouchSlop = 0;
    private float mInitialDownY = -1;
    private float mInitialMotionY = -1;
    private boolean mIsBeingDragged;
    private int mCurrentMode = MODE_PULL_FROM_NONE;

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
        insureTarget();

        mHeaderRefreshPanel = new PullRefreshHeaderPanel(getContext());
        mFooterRefreshPanel = new PullRefreshHeaderPanel(getContext());

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
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
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
                    scrollTo(0, (int) overScroll);
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                mIsBeingDragged = false;
                float y = event.getY();
                float overScroll = (mInitialMotionY - y) * .5f;
                if (overScroll > 50) {
                    //refreshing
                    goBack(overScroll);
                } else {
                    //go back
                    goBack(overScroll);
                }
                break;
            }
        }
        return true;
    }

    private void goBack(final float overScroll) {
        ValueAnimator animator = ValueAnimator.ofFloat(1.0f, 0f);
        animator.setDuration(200);
        animator.setInterpolator(new LinearInterpolator());
        AnimationUtils.ValueAnimatorListener listener = new AnimationUtils.ValueAnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                scrollTo(0, 0);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                scrollTo(0, 0);
            }

            @Override
            public void onAnimationRepeat(Animator animator) {}

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Float value = (Float) valueAnimator.getAnimatedValue();
                float newScrollValue = value * overScroll;
                scrollTo(0, (int) newScrollValue);
            }
        };
        animator.addUpdateListener(listener);
        animator.addListener(listener);
        animator.start();
    }

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

    private boolean canChildScrollDown() {
        if (Build.VERSION.SDK_INT < 14) {
            if (mTarget instanceof AbsListView) {
                AbsListView listView = (AbsListView) mTarget;
                if (listView.getChildCount() > 0) {
                    if (listView.getLastVisiblePosition() < listView.getCount() - 1) {
                        return true;
                    } else {
                        int childIndex = listView.getLastVisiblePosition() - listView.getFirstVisiblePosition();
                        View lastVisibleChild = listView.getChildAt(childIndex);
                        if (lastVisibleChild != null) {
                            return lastVisibleChild.getBottom() > listView.getBottom();
                        }
                    }
                }
                return false;
            } else {
                return mTarget.getScrollY() < (mTarget.getHeight() - getHeight());
            }
        } else {
            return ViewCompat.canScrollVertically(mTarget, 1);
        }
    }
}
