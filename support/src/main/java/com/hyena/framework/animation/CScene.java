package com.hyena.framework.animation;

import com.hyena.framework.animation.nodes.CTextNode;

import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;

/**
 * 场景
 *
 * @author yangzc
 */
public class CScene extends CLayer {

    private long mLastRefreshTs = -1;

    private CTextNode mTextNode;

    protected CScene(Director director) {
        super(director);
        if (EngineConfig.DEBUG_ABLE) {
            mTextNode = CTextNode.create(director);
            addNode(mTextNode, Integer.MAX_VALUE);
        }
    }

    public static CScene create(Director director) {
        return new CScene(director);
    }

    /**
     * 刷新场景
     */
    public void refresh() {
        Director director = getDirector();
        if (director == null || !director.isViewVisible())
            return;

        if (director.isPaused()) {
            mLastRefreshTs = -1;
            return;
        }

        long ts = System.currentTimeMillis();
        if (mLastRefreshTs > 0) {
            update(ts - mLastRefreshTs);

            if (EngineConfig.DEBUG_ABLE && mTextNode != null && ts != mLastRefreshTs) {
                mTextNode.setText(1000 / (ts - mLastRefreshTs) + " FPS");
            }
        }
        mLastRefreshTs = ts;
    }

    @Override
    public synchronized void update(float dt) {
        Director director = getDirector();
        if (director == null || !director.isViewVisible())
            return;

        super.update(dt);
    }

    public void onSceneStart() {
    }

    public void onSceneStop() {
    }

    public void onSceneResume() {
    }

    public void onScenePause() {
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onSizeChange(RenderView view, Rect rect) {
        super.onSizeChange(view, rect);
        if (mTextNode != null)
            mTextNode.setPosition(new Point(0, getHeight() - dip2px(20)));
    }

    @Override
    public boolean isActive() {
        return super.isActive();
    }
}
