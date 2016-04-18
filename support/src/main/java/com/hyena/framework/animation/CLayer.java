package com.hyena.framework.animation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.hyena.framework.animation.sprite.CNode;

import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * 展现层
 *
 * @author yangzc
 */
public class CLayer extends CNode implements OnTouchListener {

    private List<CNode> mNodes;

    protected CLayer(Director director) {
        super(director);
    }

    public static CLayer create(Director director) {
        return new CLayer(director);
    }

    @Override
    public synchronized void render(Canvas canvas) {
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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mNodes == null)
            return false;
        try {
            for (CNode node : mNodes) {
                if (node.onTouch(v, event)) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
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
}
