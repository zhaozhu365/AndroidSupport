package com.hyena.framework.samples.parser.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by yangzc on 16/4/21.
 */
public class MapLayer {

    private int mZIndex;
    private List<MapNode> mNodes;

    public void setZIndex(int zIndex) {
        this.mZIndex = zIndex;
    }

    public int getZIndex() {
        return mZIndex;
    }

    public void addNode(MapNode node) {
        if (mNodes == null)
            mNodes = new ArrayList<MapNode>();
        mNodes.add(node);
        sort();
    }

    public List<MapNode> getNodes() {
        return mNodes;
    }

    private void sort(){
        if (mNodes != null) {
            Collections.sort(mNodes, new Comparator<MapNode>() {
                @Override
                public int compare(MapNode lhs, MapNode rhs) {
                    return 0;
                }
            });
        }
    }
}
