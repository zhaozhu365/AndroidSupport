package com.hyena.framework.samples.layer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;

import com.hyena.framework.animation.Director;
import com.hyena.framework.animation.sprite.CNode;
import com.hyena.framework.animation.texture.CTexture;

/**
 * Created by yangzc on 16/4/24.
 */
public class PressableTexture extends CTexture {

    private Bitmap mNormal;
    private Bitmap mPressed;
    public static PressableTexture create(Director director, Bitmap normal, Bitmap pressed){
        return new PressableTexture(director, normal, pressed);
    }

    protected PressableTexture(Director director, Bitmap normal, Bitmap pressed) {
        super(director, normal);
        this.mNormal = normal;
        this.mPressed = pressed;
//        setOnNodeClickListener(new OnNodeClickListener() {
//            @Override
//            public void onClick(CNode node) {
//
//            }
//        });
    }

    @Override
    public boolean onTouch(MotionEvent event) {
        return super.onTouch(event);
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);
    }

    @Override
    protected void onTouchDown() {
        super.onTouchDown();
        if (mPressed != null)
            setTexture(mPressed);
    }

    @Override
    protected void onTouchUp() {
        super.onTouchUp();
        setTexture(mNormal);
    }
}
