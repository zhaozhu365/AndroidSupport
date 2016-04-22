package com.hyena.framework.samples.layer;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.hyena.framework.animation.Director;
import com.hyena.framework.animation.sprite.CNode;
import com.hyena.framework.animation.sprite.CPoint;
import com.hyena.framework.utils.UIUtils;

/**
 * Created by yangzc on 16/4/22.
 */
public class LineNode extends CNode {

    public static final int STYLE_NORMAL = 0;
    public static final int STYLE_DOT = 1;

    private int mDistance;
    private int mRadius;

    private Paint mPaint;
    private CPoint mStartPoint;
    private CPoint mEndPoint;
    private int mStyle = STYLE_NORMAL;

    public static LineNode create(Director director){
        return new LineNode(director);
    }

    private LineNode(Director director) {
        super(director);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDistance = UIUtils.dip2px(5);
        mRadius = UIUtils.dip2px(2);
    }

    public void setColor(int color) {
        if (mPaint != null) {
            mPaint.setColor(color);
        }
    }

    public Paint getPaint() {
        return mPaint;
    }

    public void setStartPoint(CPoint start) {
        this.mStartPoint = start;
    }

    public void setEndPoint(CPoint end) {
        this.mEndPoint = end;
    }

    public void setStyle(int style) {
        this.mStyle = style;
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);
        if (mStartPoint != null && mEndPoint != null) {
            switch (mStyle) {
                case STYLE_NORMAL: {
                    canvas.drawLine(mStartPoint.mX, mStartPoint.mY, mEndPoint.mX, mEndPoint.mY, mPaint);
                    break;
                }
                case STYLE_DOT: {
                    CPoint mTempPoint;
                    if (mEndPoint.mY < mStartPoint.mY) {
                        mTempPoint = mEndPoint;
                        mEndPoint = mStartPoint;
                        mStartPoint = mTempPoint;
                    }

                    int y = mStartPoint.mY;
                    while (y < mEndPoint.mY) {
                        y += mDistance;
                        int cx = (mEndPoint.mX - mStartPoint.mX) * y / (mEndPoint.mY - mStartPoint.mY);
                        canvas.drawCircle(cx, y, mRadius, mPaint);
                    }
                    break;
                }
            }
        }
    }
}