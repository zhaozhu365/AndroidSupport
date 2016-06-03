package com.hyena.framework.samples.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.utils.AnimationUtils;
import com.hyena.framework.utils.UIUtils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangzc on 16/6/2.
 */
public class CircleChart extends View {

    private int mInnerRadius;
    private int mOutterRadius;
    private int mSelectScaleDistance;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF mRectF = new RectF();
    private Xfermode mXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
    private int mAnimatedAngle = 0;
    private int mAnimatedScale = 0;

    public CircleChart(Context context) {
        super(context);
        init();
    }

    public CircleChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        //初始化内径和外径
        mInnerRadius = UIUtils.dip2px(60);
        mOutterRadius = UIUtils.dip2px(80);
        //初始化选中时的内径和外径
        mSelectScaleDistance = UIUtils.dip2px(5);

        initData();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mChartItems == null)
            return;

        for (int i = 0; i < mChartItems.size(); i++) {
            CircleChartItem item = mChartItems.get(i);
            if (item == mSelectChartItem) {
                drawArc(canvas, item.mFromAngle + mAnimatedAngle, item.mToAngle + mAnimatedAngle,
                        item.mColor, item.mInnerRadius + mAnimatedScale,
                        item.mOutterRadius + mAnimatedScale);
            } else {
                drawArc(canvas, item.mFromAngle + mAnimatedAngle, item.mToAngle + mAnimatedAngle,
                        item.mColor, item.mInnerRadius, item.mOutterRadius);
            }
        }
    }

    /**
     * 绘制扇形
     *
     * @param canvas       画板
     * @param fromAngle    开始角度
     * @param toAngle      结束角度
     * @param color        颜色
     * @param innerRadius  内径
     * @param outterRadius 外径
     */
    private void drawArc(Canvas canvas, float fromAngle, float toAngle, int color,
                         int innerRadius, int outterRadius) {
        mPaint.reset();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

        int sc = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        //添加外径
        mPaint.setColor(color);
        mRectF.set((getWidth() - (outterRadius << 1)) / 2, (getHeight() - (outterRadius << 1)) / 2,
                (getWidth() + (outterRadius << 1)) / 2, (getHeight() + (outterRadius << 1)) / 2);
        canvas.drawArc(mRectF, fromAngle, toAngle - fromAngle, true, mPaint);

        //添加内径
        mPaint.setXfermode(mXfermode);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, innerRadius, mPaint);

        canvas.restoreToCount(sc);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(
                getSpecSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                getSpecSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return (mOutterRadius + mSelectScaleDistance) << 1;
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return (mOutterRadius + mSelectScaleDistance) << 1;
    }

    private int getSpecSize(int defaultSize, int measureSpec) {
        int result = defaultSize;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                result = defaultSize;
                break;
            case MeasureSpec.AT_MOST://自适应
                result = Math.min(defaultSize, specSize);
                break;
            case MeasureSpec.EXACTLY://精确尺寸
                result = specSize;
                break;
        }
        return result;
    }

    private void initData() {
        float percent[] = new float[]{0.2f, 0.2f, 0.2f, 0.2f, 0.2f};
        int color[] = new int[]{Color.BLUE, Color.RED, Color.YELLOW, Color.CYAN, Color.GREEN};
//        float percent[] = new float[]{0.5f, 0.5f};
//        int color[] = new int[]{Color.BLUE, Color.RED};
        setChartItem(percent, color);
    }

    private CircleChartItem mSelectChartItem;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                CircleChartItem item = getSelectedChartItem(x, y);
                if (item != null) {
                    mSelectChartItem = item;
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                CircleChartItem item = getSelectedChartItem(x, y);
                if (mSelectChartItem != null && item == mSelectChartItem) {
                    selectChartItem(item);
                } else {
                    mSelectChartItem = null;
                    doRotate(mSelectChartItem, true);
                }
                break;
            }
        }
        return true;
    }

    private void selectChartItem(CircleChartItem selectChartItem) {
        if (selectChartItem == null)
            return;
        doRotate(selectChartItem, false);
    }

    private void doScale(CircleChartItem selectChartItem, boolean isReset) {
        if (isReset) {
            return;
        }
        ValueAnimator animator = ValueAnimator.ofInt(0, mSelectScaleDistance);
        animator.setDuration(500);
        AnimationUtils.ValueAnimatorListener listener = new AnimationUtils.ValueAnimatorListener(){
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mAnimatedScale = (Integer)valueAnimator.getAnimatedValue();
                postInvalidate();
            }
            @Override
            public void onAnimationStart(Animator animator) {
                mAnimatedScale = 0;
                postInvalidate();
            }
            @Override
            public void onAnimationEnd(Animator animator) {
                mAnimatedScale = mSelectScaleDistance;
                postInvalidate();
            }
            @Override
            public void onAnimationCancel(Animator animator) {
                mAnimatedScale = mSelectScaleDistance;
                postInvalidate();
            }
            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        };
        animator.addUpdateListener(listener);
        animator.addListener(listener);
        animator.start();
    }

    private void doRotate(final CircleChartItem selectChartItem, final boolean isReset) {
        int startAngle = 0, toAngle = 0;
        if (isReset) {
            CircleChartItem centerChartItem = getMaxChartItem();
            int angleOffset = centerChartItem.mFromAngle + 90
                    + (centerChartItem.mToAngle - centerChartItem.mFromAngle) / 2;
            startAngle = getAngle(centerChartItem.mFromAngle);
            toAngle = centerChartItem.mFromAngle - angleOffset;

            mAnimatedScale = 0;
        } else {
            startAngle = getAngle(selectChartItem.mFromAngle);
            toAngle = 90 - (selectChartItem.mToAngle - selectChartItem.mFromAngle) / 2;
        }
        int angleRange = toAngle - startAngle;
        if (Math.abs(angleRange) > Math.abs(360 - Math.abs(angleRange))) {
            angleRange = 360 - Math.abs(angleRange);
        }

        ValueAnimator animator = ValueAnimator.ofInt(0, angleRange);
        animator.setDuration((long)(2000f * Math.abs(angleRange) / 360));
        AnimatedListener listener = new AnimatedListener(angleRange) {
            @Override
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                doScale(selectChartItem, isReset);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                super.onAnimationCancel(animator);
                doScale(selectChartItem, isReset);
            }
        };
        animator.addUpdateListener(listener);
        animator.addListener(listener);
        animator.start();
    }

    private CircleChartItem getSelectedChartItem(float x, float y) {
        float centerX = getWidth() / 2;
        float centerY = getHeight() / 2;
        double distance = Math.sqrt((x - centerX) * (x - centerX)
                + (y - centerY) * (y - centerY));

        double angle = (Math.atan2(y - centerY, x - centerX) * 180 / Math.PI + 360) % 360;
        LogUtil.v("yangzc", "angle: " + angle);
        for (int i = 0; i < mChartItems.size(); i++) {
            CircleChartItem item = mChartItems.get(i);
            if (distance >= item.mInnerRadius && distance <= item.mOutterRadius) {
                int from = getAngle(item.mFromAngle);
                int to = getAngle(item.mToAngle);
                LogUtil.v("yangzc", "item, from: " + from + ", to: " + to);
                if (from < to) {
                    if (angle >= from && angle <= to) {
                        return item;
                    }
                } else {
                    if ((angle >= from && angle <= 360) || (angle >=0 && angle < to)) {
                        return item;
                    }
                }
            }
        }
        return null;
    }

    private int getAngle(int angle) {
        while (angle < 0) {
            angle += 360;
        }
        while (angle >= 360) {
            angle -= 360;
        }
        return angle;
    }

    private List<CircleChartItem> mChartItems;

    private CircleChartItem getMaxChartItem() {
        CircleChartItem centerChartItem = null;
        int maxRange = 0;
        for (int i = 0; i < mChartItems.size(); i++) {
            CircleChartItem item = mChartItems.get(i);
            int angleRange = item.mToAngle - item.mFromAngle;
            if (angleRange > maxRange) {
                maxRange = angleRange;
                centerChartItem = item;
            }
        }
        return centerChartItem;
    }

    public void setChartItem(float items[], int colors[]) {
        if (items == null || colors == null
                || items.length != colors.length)
            return;

        int angle = 0;
        float mMaxPercent = 0;
        CircleChartItem centerChartItem = null;
        for (int i = 0; i < items.length; i++) {
            float angleRange = items[i] * 360;
            int color = colors[i];
            CircleChartItem chartItem = addChartItem(angle,
                    (int) (angle + angleRange + 0.5f), color);
            angle = (int) (angle + angleRange + 0.5f);

            if (items[i] > mMaxPercent) {
                mMaxPercent = items[i];
                centerChartItem = chartItem;
            }
        }

        if (centerChartItem != null) {
            int angleOffset = centerChartItem.mFromAngle + 90
                    + (centerChartItem.mToAngle - centerChartItem.mFromAngle) / 2;
            for (int i = 0; i < mChartItems.size(); i++) {
                CircleChartItem chartItem = mChartItems.get(i);
                chartItem.mFromAngle -= angleOffset;
                chartItem.mToAngle -= angleOffset;
            }
        }
        postInvalidate();
    }

    public CircleChartItem addChartItem(int fromAngle, int toAngle, int color) {
        CircleChartItem item = new CircleChartItem();
        item.mColor = color;
        item.mFromAngle = fromAngle;
        item.mToAngle = toAngle;
        item.mInnerRadius = mInnerRadius;
        item.mOutterRadius = mOutterRadius;
        if (mChartItems == null) {
            mChartItems = new ArrayList<CircleChartItem>();
        }
        mChartItems.add(item);
        return item;
    }

    public class CircleChartItem {

        int mInnerRadius = 10, mOutterRadius = 10;
        int mColor = Color.RED;
        int mFromAngle = 0, mToAngle = 90;
    }

    class AnimatedListener implements AnimationUtils.ValueAnimatorListener {
        private int mOffset = 0;

        public AnimatedListener(int offset) {
            this.mOffset = offset;
        }

        @Override
        public void onAnimationStart(Animator animator) {
            mAnimatedAngle = 0;
            mAnimatedScale = 0;
        }
        @Override
        public void onAnimationEnd(Animator animator) {
            if (mChartItems != null) {
                for (int i = 0; i < mChartItems.size(); i++) {
                    CircleChartItem item = mChartItems.get(i);
                    item.mFromAngle += mOffset;
                    item.mToAngle += mOffset;
                }
            }
            mAnimatedAngle = 0;
            postInvalidate();
        }

        @Override
        public void onAnimationCancel(Animator animator) {
            if (mChartItems != null) {
                for (int i = 0; i < mChartItems.size(); i++) {
                    CircleChartItem item = mChartItems.get(i);
                    item.mFromAngle += mOffset;
                    item.mToAngle += mOffset;
                }
            }
            mAnimatedAngle = 0;
            postInvalidate();
        }

        @Override
        public void onAnimationRepeat(Animator animator) {}

        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            mAnimatedAngle = (Integer)valueAnimator.getAnimatedValue();
            postInvalidate();
        }
    }
}
