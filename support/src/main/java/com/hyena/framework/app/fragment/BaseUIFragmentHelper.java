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
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.servcie.BaseService;
import com.hyena.framework.utils.AnimationUtils;
import com.hyena.framework.utils.ImageFetcher;
import com.hyena.framework.utils.UIUtils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;

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
        ImageFetcher.getImageFetcher().loadImage(url, url, new ImageFetcher.ImageFetcherListener() {
            @Override
            public void onLoadComplete(final String imageUrl, final Bitmap bitmap, Object object) {
                if (bitmap != null && !bitmap.isRecycled()) {
                    //
                    final RelativeLayout photoPanel = new RelativeLayout(getBaseUIFragment().getActivity());
                    final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT);
                    getBaseUIFragment().getRootView().addView(photoPanel, params);
                    photoPanel.setBackgroundColor(Color.BLACK);
                    final ImageView ghostImageView = new ImageView(getBaseUIFragment().getActivity());
                    photoPanel.addView(ghostImageView, imageView.getLayoutParams());

                    ghostImageView.setImageBitmap(bitmap);
                    PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(ghostImageView);
                    photoViewAttacher.update();
                    //开始播放入场动画
                    final float xyScale[] = startGhostAnimator(imageView, ghostImageView, bitmap, photoPanel);

                    photoViewAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                        @Override
                        public void onPhotoTap(View view, float arg1, float arg2) {
                            if (photoPanel != null) {
                                final int xy[] = new int[2];
                                imageView.getLocationOnScreen(xy);

                                ghostImageView.setLayoutParams(new RelativeLayout.LayoutParams(
                                        imageView.getWidth(), imageView.getHeight()));
                                AnimatorSet animatorSet = new AnimatorSet();
                                animatorSet.playTogether(ObjectAnimator.ofFloat(ghostImageView, "translationX", xyScale[0], xy[0]),
                                        ObjectAnimator.ofFloat(ghostImageView, "translationY", xyScale[1], xy[1]),
                                        ObjectAnimator.ofFloat(photoPanel, "alpha", 1.0f, 0f),
                                        ObjectAnimator.ofFloat(ghostImageView, "scaleX", xyScale[2], 1.0f),
                                        ObjectAnimator.ofFloat(ghostImageView, "scaleY", xyScale[2], 1.0f));
                                animatorSet.setDuration(200);
                                animatorSet.setInterpolator(new AccelerateInterpolator());
                                animatorSet.addListener(new AnimationUtils.ValueAnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animator) {}
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
                                    public void onAnimationUpdate(ValueAnimator valueAnimator) {}
                                });
                                animatorSet.start();
                            }
                        }
                    });
                }
            }
        });

    }

    private float[] startGhostAnimator(ImageView rawImageView, final ImageView ghostImageView
            , Bitmap bitmap, RelativeLayout photoPanel) {
        final int xy[] = new int[2];
        rawImageView.getLocationOnScreen(xy);
        //init animator position
        ViewHelper.setTranslationX(ghostImageView, xy[0]);
        ViewHelper.setTranslationY(ghostImageView, xy[1]);

        int screenWidth = UIUtils.getWindowWidth(getBaseUIFragment().getActivity());
        int screenHeight = getBaseUIFragment().getRootView().getHeight() + getStatusBarHeight();

        float scale = Math.min((screenWidth + 0.0f) / bitmap.getWidth(),
                (screenHeight + 0.0f) / bitmap.getHeight());

        float ghostWidth = bitmap.getWidth() * scale;
        float ghostHeight = bitmap.getHeight() * scale;

        //play animator to center
        final float endX = (screenWidth - ghostWidth)/2;
        final float endY = (screenHeight + getStatusBarHeight() - ghostHeight)/2;

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(ghostImageView, "translationX", xy[0], endX),
                ObjectAnimator.ofFloat(ghostImageView, "translationY", xy[1], endY),
                ObjectAnimator.ofFloat(photoPanel, "alpha", 0, 1.0f),
                ObjectAnimator.ofFloat(ghostImageView, "scaleX", 1.0f, scale),
                ObjectAnimator.ofFloat(ghostImageView, "scaleY", 1.0f, scale));
        animatorSet.setDuration(200);
        animatorSet.setInterpolator(new AccelerateInterpolator());
        animatorSet.addListener(new AnimationUtils.ValueAnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                ViewHelper.setTranslationX(ghostImageView, xy[0]);
                ViewHelper.setTranslationY(ghostImageView, xy[1]);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                ViewHelper.setTranslationX(ghostImageView, 0);
                ViewHelper.setTranslationY(ghostImageView, 0);
                ViewHelper.setAlpha(ghostImageView, 1.0f);
                ViewHelper.setScaleX(ghostImageView, 1.0f);
                ViewHelper.setScaleY(ghostImageView, 1.0f);
                ghostImageView.setLayoutParams(new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT));
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                ViewHelper.setTranslationX(ghostImageView, 0);
                ViewHelper.setTranslationY(ghostImageView, 0);
                ViewHelper.setAlpha(ghostImageView, 1.0f);
                ViewHelper.setScaleX(ghostImageView, 1.0f);
                ViewHelper.setScaleY(ghostImageView, 1.0f);
                ghostImageView.setLayoutParams(new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT));
            }
            @Override
            public void onAnimationRepeat(Animator animator) {}

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {}
        });
        animatorSet.start();
        return new float[]{endX, endY, scale};
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
