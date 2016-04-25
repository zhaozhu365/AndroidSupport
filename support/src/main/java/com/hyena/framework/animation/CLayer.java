package com.hyena.framework.animation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.hyena.framework.animation.sprite.CNode;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Scroller;

/**
 * 展现层
 *
 * @author yangzc
 */
public class CLayer extends CNode {

    private int mScrollX, mScrollY;
    private List<CNode> mNodes;
    private float mDepth;

    protected CLayer(Director director) {
        super(director);
    }

    public static CLayer create(Director director) {
        return new CLayer(director);
    }

    @Override
    public synchronized void render(Canvas canvas) {
        if (!isValid() || !isVisible()) {
            return;
        }

        canvas.save();
        canvas.translate(mScrollX, mScrollY);
        super.render(canvas);
        if (mNodes == null)
            return;
        try {
            for (CNode node : mNodes) {
                if (node != null && node.isVisible()) {
                    node.render(canvas);
                }
            }
        } catch (Exception e) {
        }
        canvas.restore();
    }

    @Override
    public synchronized void update(float dt) {
        super.update(dt);
        if (mNodes == null)
            return;
        try {
            for (CNode node : mNodes) {
                node.update(dt);
            }
        } catch (Exception e) {
        }
    }

    public void scrollTo(int x, int y) {
        if (!isScrollable())
            return;

        this.mScrollX = x;
        this.mScrollY = y;
        if (mScrollerListener != null) {
            mScrollerListener.onScroll(this, mScrollX, mScrollY, getWidth(), getContentHeight());
        }
    }

    public void scrollBy(int dx, int dy) {
        if (!isScrollable())
            return;

        this.mScrollX += dx;
        this.mScrollY += dy;

        if (mScrollerListener != null) {
            mScrollerListener.onScroll(this, mScrollX, mScrollY, getWidth(), getContentHeight());
        }
    }

    public int getScrollX() {
        return mScrollX;
    }

    public int getScrollY() {
        return mScrollY;
    }

    /**
     * 添加渲染节点
     *
     * @param node
     * @param zIndex z轴索引
     */
    public synchronized void addNode(CNode node, int zIndex) {
        if (node == null)
            return;

        try {
            if (mNodes == null)
                mNodes = new ArrayList<CNode>();

            node.setZIndex(zIndex);
            mNodes.add(node);
            node.setParent(this);
            Collections.sort(mNodes, new Comparator<CNode>() {
                @Override
                public int compare(CNode lhs, CNode rhs) {
                    return lhs.getZIndex() - rhs.getZIndex();
                }
            });
        } catch (Exception e) {
        }
    }

//    private CNode mTargetNode = null;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mNodes == null || mNodes.isEmpty()) {
            return super.dispatchTouchEvent(ev);
        }

//        if (mTargetNode != null)
//            return mTargetNode.dispatchTouchEvent(ev);
        
        boolean isIntercept = onInterceptTouchEvent(ev);
        if (isIntercept) {
            return super.dispatchTouchEvent(ev);
        }

//        int action = ev.getAction();
//        if (action == MotionEvent.ACTION_DOWN) {
//            mTargetNode = null;
//        }
        for (int i = 0; i < mNodes.size(); i++) {
            CNode node = mNodes.get(i);
            node.dispatchTouchEvent(ev);
//            if (node.dispatchTouchEvent(ev)) {
////                mTargetNode = node;
//                return true;
//            }
        }
//        if (mTargetNode == null)
        return super.dispatchTouchEvent(ev);

//        return mTargetNode.dispatchTouchEvent(ev);
    }

    /**
     * 判断是否需要拦截事件
     *
     * @param ev
     * @return
     */
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    /**
     * 窗口大小变化
     *
     * @param view
     * @param rect
     */
    @Override
    public void onSizeChange(RenderView view, Rect rect) {
        try {
            if (mNodes != null && mNodes.size() > 0) {
                for (int i = 0; i < mNodes.size(); i++) {
                    CNode node = mNodes.get(i);
                    node.onSizeChange(view, rect);
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    public boolean isActive() {
        try {
            if (mNodes != null && mNodes.size() > 0) {
                for (int i = 0; i < mNodes.size(); i++) {
                    CNode node = mNodes.get(i);
                    if (node.isActive()) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

//    @Override
//    public int getWidth() {
//        if (mNodes == null || mNodes.isEmpty())
//            return super.getWidth();
//
//        int width = 0;
//        for (int i = 0; i < mNodes.size(); i++) {
//            CNode node = mNodes.get(i);
//            int maxX = node.getPosition().x + node.getWidth();
//            if (maxX > width) {
//                width = maxX;
//            }
//        }
//        return width;
//    }
//
//    @Override
//    public int getHeight() {
//        return super.getHeight();
//    }

    protected int getContentHeight(){
        if (mNodes == null || mNodes.isEmpty())
            return super.getHeight();

        int height = 0;
        for (int i = 0; i < mNodes.size(); i++) {
            CNode node = mNodes.get(i);
            int maxY = node.getPosition().y + node.getHeight();
            if (maxY > height) {
                height = maxY;
            }
        }
        return height;
    }

    @Override
    public CNode findNodeByTag(String tag) {
        if (mNodes != null && mNodes.size() > 0) {
            for (int i = 0; i < mNodes.size(); i++) {
                CNode node = mNodes.get(i);
                CNode result = node.findNodeByTag(tag);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    public List<CNode> getNodes() {
        return mNodes;
    }

    public void setDepth(float depth) {
        this.mDepth = depth;
    }

    public float getDepth() {
        return mDepth;
    }

    protected boolean isScrollable() {
        return getContentHeight() > getDirector().getViewSize().height();
    }

    private OnScrollerListener mScrollerListener;

    public void setOnScrollerListener(OnScrollerListener listener) {
        this.mScrollerListener = listener;
    }

    public static interface OnScrollerListener {
        public void onScroll(CLayer layer, int scrollX, int scrollY, int width, int height);
    }
}
