/**
 * Copyright (C) 2015 The AndroidRCStudent Project
 */
package com.hyena.framework.app.fragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hyena.framework.servcie.BaseService;
import com.hyena.framework.utils.AnimationUtils;
import com.hyena.framework.utils.ImageFetcher;
import com.hyena.framework.utils.UIUtils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * BaseUIFragment帮助接口
 * @author yangzc on 15/8/22.
 */
public class BaseUIFragmentHelper {

    private BaseUIFragment<?> mBaseUIFragment;

    public BaseUIFragmentHelper(BaseUIFragment<?> fragment){
        this.mBaseUIFragment = fragment;
    }

    public BaseUIFragment<?> getBaseUIFragment(){
        return mBaseUIFragment;
    }

    /**
     * 是否对用户可见
     */
    public void setVisibleToUser(boolean visible) {}

    /**
     * 背景颜色
     */
    public int getBackGroundColor(){
    	return 0xfff6f6f6;
    }

    /**
     * 显示预览图片
     */
    public void showPicture(final ImageView imageView, String url) {
        if (imageView == null || getBaseUIFragment() == null
                || getBaseUIFragment().getRootView() == null)
            return;
        ImageFetcher.getImageFetcher().loadImage(url, null, url, new ImageFetcher.ImageFetcherListener() {
            @Override
            public void onLoadComplete(final String imageUrl, final Bitmap bitmap, Object object) {
                if (bitmap != null && !bitmap.isRecycled()) {
                    //create photo panel
                    RelativeLayout photoPanel = createPhotoPanel();
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT);
                    //add photo panel
                    getBaseUIFragment().getRootView().addView(photoPanel, params);
                    photoPanel.setBackgroundColor(Color.BLACK);
                    //Ghost new imageView
                    final ImageView ghostImageView = new ImageView(getBaseUIFragment().getActivity());
                    ghostImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    photoPanel.addView(ghostImageView, imageView.getLayoutParams());
                    ghostImageView.setImageBitmap(bitmap);
                    ghostImageView.setScaleType(imageView.getScaleType());
                    //开始播放入场动画
                    startGhostInAnimator(imageView, ghostImageView, bitmap, photoPanel);
                }
            }
        }, new ImageLoadingProgressListener() {
            @Override
            public void onProgressUpdate(String imageUri, View view, int current, int total) {
            }
        });
    }

    /**
     *c create
     */
    public RelativeLayout createPhotoPanel() {
        RelativeLayout photoPanel = new RelativeLayout(getBaseUIFragment().getActivity());
        return photoPanel;
    }

    private void startGhostOutScaleAnimator(final ImageView rawImageView,
                                            final Bitmap bitmap,
                                            final ImageView ghostImageView,
                                            final RelativeLayout photoPanel) {
        int screenWidth = UIUtils.getWindowWidth(getBaseUIFragment().getActivity());
        int screenHeight = getBaseUIFragment().getRootView().getHeight();

        PhotoViewAttacher photoViewAttacher = (PhotoViewAttacher) ghostImageView.getTag();
        if (photoViewAttacher != null) {
            photoViewAttacher.cleanup();
            ghostImageView.setScaleType(rawImageView.getScaleType());
        }

        final int xy[] = new int[2];
        rawImageView.getLocationOnScreen(xy);
        final float currentScale = Math.min((screenWidth + 0.0f) / bitmap.getWidth(),
                (screenHeight + 0.0f) / bitmap.getHeight());
        final float currentWidth = bitmap.getWidth() * currentScale;
        final float currentHeight = bitmap.getHeight() * currentScale;
        final float currentX = (screenWidth - currentWidth)/2;
        final float currentY = (screenHeight - currentHeight)/2;
        ViewHelper.setX(ghostImageView, currentX);
        ViewHelper.setY(ghostImageView, currentY);
        ghostImageView.getLayoutParams().width = (int) currentWidth;
        ghostImageView.getLayoutParams().height = (int) currentHeight;
        ghostImageView.requestLayout();

        final float finalScale = getFitScale(rawImageView, bitmap);
        final float finalWidth = rawImageView.getWidth() * finalScale;
        final float finalHeight = rawImageView.getHeight() * finalScale;
        final float finalX = (screenWidth - finalWidth)/2;
        final float finalY = (screenHeight - finalHeight)/2;

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1.0f);
        valueAnimator.setDuration(100);
        valueAnimator.setInterpolator(new LinearInterpolator());
        AnimationUtils.ValueAnimatorListener listener = new AnimationUtils.ValueAnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {}

            @Override
            public void onAnimationEnd(Animator animator) {
                startGhostOutAnimator(rawImageView, ghostImageView, photoPanel);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                getBaseUIFragment().getRootView().removeView(photoPanel);
            }

            @Override
            public void onAnimationRepeat(Animator animator) {}

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Float value = (Float) valueAnimator.getAnimatedValue();
                float x = currentX + (finalX - currentX) * value;
                float y = currentY + (finalY - currentY) * value;
                ViewHelper.setTranslationX(ghostImageView, x);
                ViewHelper.setTranslationY(ghostImageView, y);

                ghostImageView.getLayoutParams().width = (int) (currentWidth + (finalWidth - currentWidth) * value);
                ghostImageView.getLayoutParams().height = (int) (currentHeight + (finalHeight - currentHeight) * value);
                ghostImageView.requestLayout();
            }
        };
        valueAnimator.addListener(listener);
        valueAnimator.addUpdateListener(listener);
        valueAnimator.start();
    }

    /**
     * start ghostOut animator
     */
    private void startGhostOutAnimator(final ImageView rawImageView, final ImageView ghostImageView
            , final RelativeLayout photoPanel) {
        if (photoPanel != null) {
            final float currentX = ViewHelper.getX(ghostImageView);
            final float currentY = ViewHelper.getY(ghostImageView);
            final int currentWidth = ghostImageView.getLayoutParams().width;
            final int currentHeight = ghostImageView.getLayoutParams().height;

            int xy[] = new int[2];
            rawImageView.getLocationOnScreen(xy);
            final int finalX = xy[0];
            final int finalY = xy[1];
            final int finalWidth = rawImageView.getWidth();
            final int finalHeight = rawImageView.getHeight();

            ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
            animator.setDuration(200);
            animator.setInterpolator(new LinearInterpolator());
            AnimationUtils.ValueAnimatorListener listener = new AnimationUtils.ValueAnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    getBaseUIFragment().getRootView().removeView(photoPanel);
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                    getBaseUIFragment().getRootView().removeView(photoPanel);
                }
                @Override
                public void onAnimationRepeat(Animator animator) {}

                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float value = (Float)valueAnimator.getAnimatedValue();
                    int x = (int) ((finalX - currentX) * value + currentX);
                    int y = (int) ((finalY - currentY) * value + currentY);
                    ViewHelper.setTranslationX(ghostImageView, x);
                    ViewHelper.setTranslationY(ghostImageView, y);

                    ghostImageView.getLayoutParams().width = (int) (currentWidth + (finalWidth - currentWidth)* value);
                    ghostImageView.getLayoutParams().height = (int) (currentHeight + (finalHeight - currentHeight) * value);
                    ghostImageView.requestLayout();

                    ViewHelper.setAlpha(photoPanel, 1.0f - value);
                }
            };
            animator.addListener(listener);
            animator.addUpdateListener(listener);
            animator.start();
        }
    }

    /**
     * start ghostIn animator
     */
    private void startGhostInAnimator(final ImageView rawImageView, final ImageView ghostImageView
            , final Bitmap bitmap, final RelativeLayout photoPanel) {
        final int xy[] = new int[2];
        rawImageView.getLocationOnScreen(xy);
        //init animator position
        ViewHelper.setTranslationX(ghostImageView, xy[0]);
        ViewHelper.setTranslationY(ghostImageView, xy[1]);

        int screenWidth = UIUtils.getWindowWidth(getBaseUIFragment().getActivity());
        int screenHeight = getBaseUIFragment().getRootView().getHeight();

        final int startX = xy[0];
        final int startY = xy[1];
        final int startWidth = rawImageView.getWidth();
        final int startHeight = rawImageView.getHeight();

        float finalScale = getFitScale(rawImageView, bitmap);

        final float finalWidth = startWidth * finalScale;
        final float finalHeight = startHeight * finalScale;
        final float finalX = (screenWidth - finalWidth)/2;
        final float finalY = (screenHeight - finalHeight)/2;

        ValueAnimator animator = ValueAnimator.ofFloat(0, 1.0f);
        animator.setDuration(200);
        animator.setInterpolator(new LinearInterpolator());
        AnimationUtils.ValueAnimatorListener listener = new AnimationUtils.ValueAnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                setAnimatorInStartAction(ghostImageView, xy);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (shouldRunScaleAnimator(rawImageView, bitmap)) {
                    startGhostInScaleAnimator(rawImageView, ghostImageView, bitmap, photoPanel);
                } else {
                    setAnimatorInEndAction(rawImageView, ghostImageView, bitmap, photoPanel);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                setAnimatorInEndAction(rawImageView, ghostImageView, bitmap, photoPanel);
            }

            @Override
            public void onAnimationRepeat(Animator animator) {}

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (Float)valueAnimator.getAnimatedValue();
                int x = (int) ((finalX - startX) * value + startX);
                int y = (int) ((finalY - startY) * value + startY);
                ViewHelper.setTranslationX(ghostImageView, x);
                ViewHelper.setTranslationY(ghostImageView, y);

                ghostImageView.getLayoutParams().width = (int) (startWidth + (finalWidth - startWidth) * value);
                ghostImageView.getLayoutParams().height = (int) (startHeight + (finalHeight - startHeight) * value);
                ghostImageView.requestLayout();

                ViewHelper.setAlpha(photoPanel, value);
            }
        };
        animator.addListener(listener);
        animator.addUpdateListener(listener);
        animator.start();
    }

    private void startGhostInScaleAnimator(final ImageView rawImageView, final ImageView ghostImageView
            , final Bitmap bitmap, final RelativeLayout photoPanel) {
        int screenWidth = UIUtils.getWindowWidth(getBaseUIFragment().getActivity());
        int screenHeight = getBaseUIFragment().getRootView().getHeight();

        final float startX = ViewHelper.getX(ghostImageView);
        final float startY = ViewHelper.getY(ghostImageView);
        final float startWidth = ghostImageView.getLayoutParams().width;
        final float startHeight = ghostImageView.getLayoutParams().height;

        float finalScale = Math.min((screenWidth + 0.0f) / bitmap.getWidth(),
                (screenHeight + 0.0f) / bitmap.getHeight());

        final float finalWidth = bitmap.getWidth() * finalScale;
        final float finalHeight = bitmap.getHeight() * finalScale;
        final float finalX = (screenWidth - finalWidth) / 2;
        final float finalY = (screenHeight - finalHeight) / 2;

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1.0f);
        valueAnimator.setDuration(100);
        valueAnimator.setInterpolator(new LinearInterpolator());
        AnimationUtils.ValueAnimatorListener listener = new AnimationUtils.ValueAnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {}

            @Override
            public void onAnimationEnd(Animator animator) {
                setAnimatorInEndAction(rawImageView, ghostImageView, bitmap, photoPanel);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                setAnimatorInEndAction(rawImageView, ghostImageView, bitmap, photoPanel);
            }

            @Override
            public void onAnimationRepeat(Animator animator) {}

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Float value = (Float) valueAnimator.getAnimatedValue();
                float x = startX + (finalX - startX) * value;
                float y = startY + (finalY - startY) * value;
                ViewHelper.setTranslationX(ghostImageView, x);
                ViewHelper.setTranslationY(ghostImageView, y);


                ghostImageView.getLayoutParams().width = (int) (startWidth + (finalWidth - startWidth) * value);
                ghostImageView.getLayoutParams().height = (int) (startHeight + (finalHeight - startHeight) * value);
                ghostImageView.requestLayout();
            }
        };
        valueAnimator.addListener(listener);
        valueAnimator.addUpdateListener(listener);
        valueAnimator.start();
    }

    /**
     * run on animatorIn start
     */
    private void setAnimatorInStartAction(ImageView ghostImageView, int xy[]){
        ViewHelper.setTranslationX(ghostImageView, xy[0]);
        ViewHelper.setTranslationY(ghostImageView, xy[1]);
    }

    /**
     * run on animatorIn end
     */
    private void setAnimatorInEndAction(final ImageView rawImageView, final ImageView ghostImageView
            , final Bitmap bitmap, final RelativeLayout photoPanel) {
        ViewHelper.setTranslationX(ghostImageView, 0);
        ViewHelper.setTranslationY(ghostImageView, 0);
        ViewHelper.setAlpha(ghostImageView, 1.0f);
        ViewHelper.setScaleX(ghostImageView, 1.0f);
        ViewHelper.setScaleY(ghostImageView, 1.0f);
        ghostImageView.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));
        PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(ghostImageView);
        photoViewAttacher.update();
        photoViewAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float v, float v1) {
                if (shouldRunScaleAnimator(rawImageView, bitmap)) {
                    startGhostOutScaleAnimator(rawImageView,bitmap, ghostImageView, photoPanel);
                } else {
                    startGhostOutAnimator(rawImageView, ghostImageView, photoPanel);
                }
            }
        });
        ghostImageView.setTag(photoViewAttacher);
    }

    /**
     * should run scale Animator
     */
    private boolean shouldRunScaleAnimator(ImageView rawImageView, Bitmap bitmap) {
        return rawImageView.getWidth() * bitmap.getHeight() != bitmap.getWidth() * rawImageView.getHeight();
    }

    private float getFitScale(final ImageView rawImageView, final Bitmap bitmap) {
        int screenWidth = UIUtils.getWindowWidth(getBaseUIFragment().getActivity());
        int screenHeight = getBaseUIFragment().getRootView().getHeight();

        float imageScale = Math.min((screenWidth + 0.0f) / rawImageView.getWidth(),
                (screenHeight + 0.0f) / rawImageView.getHeight());

        float bitmapScale = Math.min((screenWidth + 0.0f) / bitmap.getWidth(),
                (screenHeight + 0.0f) / bitmap.getHeight());

        float finalWidth = bitmap.getWidth() * bitmapScale;
        float finalHeight = bitmap.getHeight() * bitmapScale;

        float imageMaxWidth = rawImageView.getWidth();
        float imageMaxHeight = rawImageView.getHeight();

        return Math.min(finalWidth/imageMaxWidth, finalHeight/imageMaxHeight);
//        return 1;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getBaseUIFragment().getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        if (resourceId > 0) {
            result = getBaseUIFragment().getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * replace fragment
     */
    public void replaceFragment(int id, BaseUIFragment fragment) {
        if (mBaseUIFragment == null && mBaseUIFragment.getActivity() == null
                && mBaseUIFragment.getActivity().isFinishing())
            return;

        FragmentTransaction transaction = getBaseUIFragment()
                .getChildFragmentManager().beginTransaction();
        transaction.replace(id, fragment);
        transaction.commitAllowingStateLoss();
    }

    /**
     * showPushFragment
     */
    public void showPushFragment(Class<? extends BaseUIFragment> clz) {
        showPushFragment(clz, null);
    }

    /**
     * showPopFragment
     */
    public void showPopFragment(Class<? extends BaseUIFragment> clz) {
        showPopFragment(clz, null);
    }

    /**
     * showPushFragment
     */
    public void showPushFragment(Class<? extends BaseUIFragment> clz, Bundle bundle) {
        if (mBaseUIFragment == null && mBaseUIFragment.getActivity() == null
                && mBaseUIFragment.getActivity().isFinishing())
            return;

        BaseUIFragment fragment = BaseUIFragment.newFragment(
                getBaseUIFragment().getActivity(), clz, bundle);
        getBaseUIFragment().showPushFragment(fragment);
    }

    /**
     * showPopFragment
     */
    public void showPopFragment(Class<? extends BaseUIFragment> clz, Bundle bundle) {
        if (mBaseUIFragment == null && mBaseUIFragment.getActivity() == null
                && mBaseUIFragment.getActivity().isFinishing())
            return;

        BaseUIFragment fragment = BaseUIFragment.newFragment(
                getBaseUIFragment().getActivity(), clz, bundle);
        getBaseUIFragment().showPopFragment(fragment);

    }

    public <T extends BaseService> T getService(String serviceName) {
        if (mBaseUIFragment == null && mBaseUIFragment.getActivity() == null
                && mBaseUIFragment.getActivity().isFinishing())
            return null;
        return (T) mBaseUIFragment.getSystemService(serviceName);
    }
}
