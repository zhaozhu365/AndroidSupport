package com.hyena.framework.samples.parser;

import com.hyena.framework.samples.parser.node.MapNode;
import com.hyena.framework.samples.parser.node.MapNodeLayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by yangzc on 16/4/21.
 */
public class CMap {

    public String mBackGround;

    private List<MapNodeLayer> mLayers;

    public void addLayer(MapNodeLayer layer) {
        if (mLayers == null)
            mLayers = new ArrayList<MapNodeLayer>();
        mLayers.add(layer);
        sort();
    }

    public List<MapNodeLayer> getLayers() {
        return mLayers;
    }

    private void sort() {
        if (mLayers != null) {
            Collections.sort(mLayers, new Comparator<MapNodeLayer>() {
                @Override
                public int compare(MapNodeLayer lhs, MapNodeLayer rhs) {
                    return lhs.getZIndex() - rhs.getZIndex();
                }
            });
        }
    }
}
