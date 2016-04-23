package com.hyena.framework.samples.layer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import com.hyena.framework.animation.CLayer;
import com.hyena.framework.animation.CScene;
import com.hyena.framework.animation.CScrollLayer;
import com.hyena.framework.animation.Director;
import com.hyena.framework.animation.action.CAlphaToAction;
import com.hyena.framework.animation.action.CFrameAction;
import com.hyena.framework.animation.action.CMoveToAction;
import com.hyena.framework.animation.action.CScaleToAction;
import com.hyena.framework.animation.action.CSequenceAction;
import com.hyena.framework.animation.action.base.CAction;
import com.hyena.framework.animation.nodes.CTextNode;
import com.hyena.framework.animation.sprite.CNode;
import com.hyena.framework.animation.sprite.CPoint;
import com.hyena.framework.animation.sprite.CSprite;
import com.hyena.framework.animation.texture.BitmapManager;
import com.hyena.framework.animation.texture.CTexture;
import com.hyena.framework.samples.parser.CMap;
import com.hyena.framework.samples.parser.DefaultMapParser;
import com.hyena.framework.samples.parser.MapParser;
import com.hyena.framework.samples.parser.action.MapAction;
import com.hyena.framework.samples.parser.action.MapActionAlpha;
import com.hyena.framework.samples.parser.action.MapActionFrame;
import com.hyena.framework.samples.parser.action.MapActionScale;
import com.hyena.framework.samples.parser.action.MapActionSequence;
import com.hyena.framework.samples.parser.action.MapActionTranslate;
import com.hyena.framework.samples.parser.action.MapFrame;
import com.hyena.framework.samples.parser.node.MapNode;
import com.hyena.framework.samples.parser.node.MapNodeLayer;
import com.hyena.framework.samples.parser.node.MapNodeLine;
import com.hyena.framework.samples.parser.node.MapNodeSprite;
import com.hyena.framework.samples.parser.node.MapNodeText;
import com.hyena.framework.utils.FileUtils;

import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangzc on 16/4/22.
 */
public class MapScene extends CScene {

    private MapParser mParser = new DefaultMapParser();
    private CMap mMap = null;

    protected MapScene(Director director) {
        super(director);
    }

    public void loadAssetPath(String path) {
        try {
            InputStream is = getDirector().getContext().getAssets().open(path);
            byte buf[] = FileUtils.getBytes(is);
            load(new String(buf, HTTP.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load(String xml) {
        mMap = mParser.parse(xml,
                getDirector().getViewSize().width(),
                getDirector().getViewSize().height());

        if (mMap != null) {
            int zIndex = 0;
            CScrollLayer topLayer = null;
            List<MapNodeLayer> layers = mMap.getLayers();
            if (layers != null && !layers.isEmpty()) {
                for (int i = 0; i < layers.size(); i++) {
                    MapNodeLayer nodeLayer = layers.get(i);
                    //load layer
                    CScrollLayer layer = createLayer(nodeLayer);
                    if (nodeLayer.getZIndex() > zIndex) {
                        topLayer = layer;
                        zIndex = nodeLayer.getZIndex();
                    }
                    if (layer != null) {
                        layer.setDepth(nodeLayer.getDepth());
                        addNode(layer, nodeLayer.getZIndex());
                    }
                }
            }
            //compute depth
            if (topLayer != null) {
                topLayer.setOnScrollerListener(new OnScrollerListener() {

                    @Override
                    public void onScroll(int scrollX, int scrollY, int width, int height) {
                        List<CNode> nodes = getNodes();
                        for (int i = 0; i < nodes.size(); i++) {
                            if (nodes.get(i) instanceof CScrollLayer) {
                                CScrollLayer layer = (CScrollLayer) nodes.get(i);
                                layer.scrollTo((int) (scrollX * layer.getDepth()/width), (int) (scrollY * layer.getDepth()/ height));
                            }
                        }
                    }
                });
            }
        }
    }

    private Bitmap loadBitmap(String tag, String url) {
        if (url != null && url.startsWith("res:")) {
            try {
                InputStream is = getDirector().getContext().getAssets()
                        .open(url.replace("res:", ""));
                Bitmap bitmap = BitmapManager.getInstance().getBitmap(url, is);
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * load Layer
     *
     * @param mapLayer layer information
     */
    private CScrollLayer createLayer(MapNodeLayer mapLayer) {
        if (mapLayer == null)
            return null;

        CScrollLayer layer = CScrollLayer.create(getDirector());
        List<MapNode> nodes = mapLayer.getNodes();
        if (nodes != null && !nodes.isEmpty()) {
            for (int i = 0; i < nodes.size(); i++) {
                MapNode mapNode = nodes.get(i);
                CNode node = null;
                if (mapNode instanceof MapNodeSprite) {
                    node = loadSprite((MapNodeSprite) mapNode);
                } else if (mapNode instanceof MapNodeText) {
                    node = createText((MapNodeText) mapNode);
                } else if (mapNode instanceof MapNodeLine) {
                    node = createLine(mapLayer, (MapNodeLine) mapNode);
                }

                if (node != null) {
                    node.setTag(mapNode.getId());
                    layer.addNode(node, mapNode.getZIndex());
                }
            }
        }
        return layer;
    }

    private CAction createAction(MapAction mapAction) {
        CAction action = null;
        if (mapAction instanceof MapActionAlpha) {
            action = createAlphaAction((MapActionAlpha) mapAction);
        } else if (mapAction instanceof MapActionScale) {
            action = createScaleAction((MapActionScale) mapAction);
        } else if (mapAction instanceof MapActionTranslate) {
            action = createMoveToAction((MapActionTranslate) mapAction);
        } else if (mapAction instanceof MapActionFrame) {
            action = createFrameAction((MapActionFrame) mapAction);
        } else if (mapAction instanceof MapActionSequence) {
            action = createSequenceAction((MapActionSequence) mapAction);
        }
        return action;
    }

    private CSequenceAction createSequenceAction(MapActionSequence mapSequenceAction) {
        List<MapAction> mapActions = mapSequenceAction.getActions();
        if (mapActions == null || mapActions.isEmpty())
            return null;

        List<CAction> actions = new ArrayList<CAction>();
        for (int i = 0; i < mapActions.size(); i++) {
            MapAction mapAction = mapActions.get(i);
            CAction action = createAction(mapAction);
            if (action != null) {
                actions.add(action);
            }
        }

        CSequenceAction action = CSequenceAction.create(actions
                .toArray(new CAction[actions.size()]));
        return action;
    }

    private CFrameAction createFrameAction(MapActionFrame mapFrameAction) {
        List<MapFrame> frames = mapFrameAction.getFrames();
        if (frames != null && frames.size() > 0) {
            CFrameAction action = CFrameAction.create();
            for (int i = 0; i < frames.size(); i++) {
                MapFrame frame = frames.get(i);
                Bitmap bitmap = loadBitmap("", frame.mSrc);
                if (bitmap == null) {
                    continue;
                }
                action.addFrame(bitmap, frame.getDuration());
            }
            return action;
        }
        return null;
    }

    private CMoveToAction createMoveToAction(MapActionTranslate mapScaleAction) {
        CMoveToAction action = CMoveToAction.create(mapScaleAction.mToX,
                mapScaleAction.mToY, mapScaleAction.getDuration());
        return action;
    }

    private CScaleToAction createScaleAction(MapActionScale mapScaleAction) {
        CScaleToAction action = CScaleToAction.create(mapScaleAction.mFrom,
                mapScaleAction.mTo, mapScaleAction.getDuration());
        return action;
    }

    private CAlphaToAction createAlphaAction(MapActionAlpha mapAlphaAction) {
        CAlphaToAction action = CAlphaToAction.create(mapAlphaAction.mFrom,
                mapAlphaAction.mTo, mapAlphaAction.getDuration());
        return action;
    }

    private CNode loadSprite(MapNodeSprite spriteNode) {
        String path = spriteNode.mSrc;
        Bitmap bitmap = loadBitmap(spriteNode.getId(), path);
        CTexture texture = CTexture.create(getDirector(), bitmap);
        if (spriteNode.getActions() != null
                && !spriteNode.getActions().isEmpty()) {
            CSprite sprite = CSprite.create(getDirector(), texture);
            sprite.setScale(spriteNode.getWidth() / bitmap.getWidth(),
                    spriteNode.getHeight() / bitmap.getHeight());
            for (int i = 0; i < spriteNode.getActions().size(); i++) {
                MapAction mapAction = spriteNode.getActions().get(i);
                CAction action = createAction(mapAction);

                if (action != null) {
                    sprite.runAction(action);
                }
            }
            return sprite;
        } else {
            return texture;
        }
    }


    private CTextNode createText(MapNodeText textNode) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        CTextNode node = CTextNode.create(getDirector());
        node.setPosition(new Point(textNode.getX(), textNode.getY()));
        paint.setTextSize(textNode.mFontSize);
        paint.setColor(textNode.mColor);
        node.setPaint(paint);
        return node;
    }

    private LineNode createLine(MapNodeLayer nodeLayer, MapNodeLine line) {
        MapNodeSprite fromSprite = getSprite(nodeLayer, line.mFromId);
        MapNodeSprite toSprite = getSprite(nodeLayer, line.mToId);

        LineNode node = LineNode.create(getDirector());
        if ("dot".equals(line.mStyle)) {
            node.setStyle(LineNode.STYLE_DOT);
        } else {
            node.setStyle(LineNode.STYLE_NORMAL);
        }
        node.setColor(0xffff0000);

        node.setStartPoint(new CPoint(fromSprite.getX(), fromSprite.getY()));
        node.setEndPoint(new CPoint(toSprite.getX(), toSprite.getY()));
        return node;
    }

    private MapNodeSprite getSprite(MapNodeLayer layer, String id) {
        if (layer != null) {
            List<MapNode> nodes = layer.getNodes();
            if (nodes != null && !nodes.isEmpty()) {
                for (int i = 0; i < nodes.size(); i++) {
                    MapNode mapNode = nodes.get(i);
                    if (mapNode instanceof MapNodeSprite) {
                        if (mapNode.getId() != null && mapNode.getId().equals(id)) {
                            return (MapNodeSprite) mapNode;
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public synchronized void render(Canvas canvas) {
        //draw background color
        if (mMap != null) {
            canvas.drawColor(Color.parseColor(mMap.mBackGround));
        }
        super.render(canvas);
    }
}
