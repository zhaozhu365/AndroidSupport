package com.hyena.framework.animation.texture;


import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.hyena.framework.animation.Director;

/**
 * 默认纹理
 * 处理图片展现
 * @author yangzc
 */
public class CTexture extends CBaseTexture {

	private Bitmap mBitmap;
	
	private CTexture(Director director, Bitmap bitmap){
		super(director);
		this.mBitmap = bitmap;
	}
	
	/**
	 * 创建Texture
	 * @param bitmap 纹理
	 * @return
	 */
	public static CTexture create(Director director, Bitmap bitmap){
		CTexture texture = new CTexture(director, bitmap);
		return texture;
	}
	
	@Override
	public void render(Canvas canvas) {
		super.render(canvas);
		if(mBitmap == null || mMatrix == null 
				|| mPaint == null || mBitmap.isRecycled())
			return;
		
		canvas.drawBitmap(mBitmap, mMatrix, mPaint);
	}

	/**
	 * 设置纹理
	 * @param bitmap
	 */
	public void setTexture(Bitmap bitmap){
		this.mBitmap = bitmap;
	}
	
	@Override
	public int getWidth(){
		if(mBitmap != null){
			return (int) (mBitmap.getWidth() * mScaleX);
		}
		return 0;
	}

	@Override
	public int getHeight(){
		if(mBitmap != null){
			return (int) (mBitmap.getHeight() * mScaleY);
		}
		return 0;
	}
	
	/**
	 * 获得纹理图片
	 * @return
	 */
	public Bitmap getTexture(){
		return mBitmap;
	}
}
