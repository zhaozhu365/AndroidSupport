package com.hyena.framework.animation.texture;

import java.io.InputStream;
import java.lang.ref.SoftReference;

import com.hyena.framework.animation.utils.BitmapUtils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;

public class BitmapManager extends LruCache<String, SoftReference<Bitmap>> {

	private static BitmapManager _instance = null;
	
	private BitmapManager(int maxSize) {
		super(maxSize);
	}
	
	public synchronized static BitmapManager getInstance(){
		if(_instance == null)
			_instance = new BitmapManager(1024 * 10);
		return _instance;
	}
	
	public Bitmap getBitmap(Resources res, int resId){
		SoftReference<Bitmap> bitmap = get(resId + "");
		Bitmap value = null;
		if(bitmap == null || bitmap.get() == null || bitmap.get().isRecycled()){
			Bitmap newBitmap = BitmapUtils.decodeResourceInternal(res, resId);
			if(newBitmap != null)
				put(resId + "", new SoftReference<Bitmap>(newBitmap));
		}else{
			value = bitmap.get();
		}
		return value;
	}

	public Bitmap getBitmap(String path){
		SoftReference<Bitmap> bitmap = get(path);
		Bitmap value = null;
		if(bitmap == null || bitmap.get() == null || bitmap.get().isRecycled()){
			Bitmap newBitmap = BitmapFactory.decodeFile(path);
			if(newBitmap != null)
				put(path, new SoftReference<Bitmap>(newBitmap));
		}else{
			value = bitmap.get();
		}
		return value;
	}

	public Bitmap getBitmap(String path, InputStream is){
		SoftReference<Bitmap> bitmap = get(path);
		Bitmap value = null;
		if(bitmap == null || bitmap.get() == null || bitmap.get().isRecycled()){
			Bitmap newBitmap = BitmapFactory.decodeStream(is);
			if(newBitmap != null)
				put(path, new SoftReference<Bitmap>(newBitmap));
		}else{
			value = bitmap.get();
		}
		return value;
	}
	
	@Override
	protected void entryRemoved(boolean evicted, String key,
			SoftReference<Bitmap> oldValue, SoftReference<Bitmap> newValue) {
		super.entryRemoved(evicted, key, oldValue, newValue);
		
		if(oldValue != null && oldValue.get() != null && !oldValue.get().isRecycled()){
			oldValue.get().recycle();
		}
	}
	
	@Override
	protected int sizeOf(String key, SoftReference<Bitmap> value) {
		if(value == null || value.get() == null || value.get().isRecycled()){
			return 0;
		}else{
			Bitmap bitmap = value.get();
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			return width * height * 4;
		}
	}
	
}
