package com.hyena.framework.animation;

import com.hyena.framework.animation.nodes.CTextNode;
import com.hyena.framework.animation.sprite.CNode;

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
            mTextNode.setTextAlign(CAlign.BOTTOM_LEFT);
            mTextNode.setViewSize(100, 40);
            mTextNode.setPosition(new Point(director.getViewSize().left + 10, director.getViewSize().bottom - 60));
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

//    @Override
//    public int getContentHeight() {
//        int height = 0;
//        if (getNodes() != null) {
//            for (int i = 0; i < getNodes().size(); i++) {
//                CNode node = getNodes().get(i);
//                if (node != null && node instanceof CLayer) {
//                    int nodeHeight = ((CLayer)node).getHeight();
//                    if (nodeHeight > height) {
//                        height = nodeHeight;
//                    }
//                }
//            }
//        }
//        return height;
//    }
}
