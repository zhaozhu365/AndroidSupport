package com.hyena.framework.samples.layer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;

import com.hyena.framework.animation.Director;
import com.hyena.framework.animation.sprite.CNode;
import com.hyena.framework.utils.UIUtils;

/**
 * Created by yangzc on 16/4/26.
 */
public class BlockNode extends CNode {

    private String mTitle;
    private String mSubTitle;
    private Bitmap mSubTitleBitmap;
    private Paint mPaint;

    public static BlockNode create(Director director){
        return new BlockNode(director);
    }

    private BlockNode(Director director) {
        super(director);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public void setSubTitle(String subTitle, Bitmap subTitleBitmap) {
        this.mSubTitle = subTitle;
        this.mSubTitleBitmap = subTitleBitmap;
    }

    private int mTitleFontSize;
    private int mTitleColor;
    public void setTitleStyle(int fontSize, String color){
        this.mTitleFontSize = fontSize;
        this.mTitleColor = Color.parseColor(color);
        mPaint.setTextSize(mTitleFontSize);
        mPaint.setColor(mTitleColor);
    }

    private int mSubTitleFontSize;
    public void setSubTitleStyle(int fontSize){
        this.mSubTitleFontSize = fontSize;
    }


    @Override
    public void render(Canvas canvas) {
        super.render(canvas);
        mPaint.setTextSize(mTitleFontSize);
        mPaint.setColor(mTitleColor);
        int y = getPosition().y + getTextHeight(mPaint);
        if (!TextUtils.isEmpty(mTitle))
            canvas.drawText(mTitle, getPosition().x, y, mPaint);

        mPaint.setTextSize(mSubTitleFontSize);
        y += getTextHeight(mPaint);
        if (!TextUtils.isEmpty(mSubTitle))
            canvas.drawText(mSubTitle, getPosition().x, y, mPaint);

        int x = (int) mPaint.measureText(mSubTitle) + getPosition().x;
        canvas.drawBitmap(mSubTitleBitmap, x + UIUtils.dip2px(3), y - (getTextHeight(mPaint) + mSubTitleBitmap.getHeight())/2 + UIUtils.dip2px(3), mPaint);
    }

    @Override
    public int getWidth() {
        float titleWidth = 0;
        if (!TextUtils.isEmpty(mTitle)) {
            titleWidth = mPaint.measureText(mTitle);
        }
        float subTitleWidth = 0;
        if (!TextUtils.isEmpty(mSubTitle) && mSubTitleBitmap != null) {
            subTitleWidth = mPaint.measureText(mSubTitle) + mSubTitleBitmap.getWidth();
        }
        if (subTitleWidth > titleWidth) {
            titleWidth = subTitleWidth;
        }
        return (int) titleWidth;
    }

    @Override
    public int getHeight() {
        return 100;
    }

    private int getTextHeight(Paint paint){
        if (paint != null) {
            Paint.FontMetrics fm = paint.getFontMetrics();
            return (int) (fm.bottom - fm.top);
        }
        return 0;
    }
}
