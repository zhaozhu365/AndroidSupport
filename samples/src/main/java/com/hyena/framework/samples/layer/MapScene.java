package com.hyena.framework.samples.layer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.text.TextUtils;

import com.hyena.framework.animation.CLayer;
import com.hyena.framework.animation.CScene;
import com.hyena.framework.animation.CScrollLayer;
import com.hyena.framework.animation.Director;
import com.hyena.framework.animation.action.CAlphaToAction;
import com.hyena.framework.animation.action.CFrameAction;
import com.hyena.framework.animation.action.CMoveToAction;
import com.hyena.framework.animation.action.CRotateToAction;
import com.hyena.framework.animation.action.CScaleToAction;
import com.hyena.framework.animation.action.CSequenceAction;
import com.hyena.framework.animation.action.base.CAction;
import com.hyena.framework.animation.action.base.CIntervalAction;
import com.hyena.framework.animation.action.base.CRepeatAction;
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
import com.hyena.framework.samples.parser.action.MapActionRotate;
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
import com.hyena.framework.utils.UIUtils;

import org.apache.http.protocol.HTTP;

import java.io.FileNotFoundException;
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
            int zIndex = -1;
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
                    public void onScroll(CLayer layer, int scrollX, int scrollY, int width, int height) {
                        List<CNode> nodes = getNodes();
                        for (int i = 0; i < nodes.size(); i++) {
                            if (nodes.get(i) instanceof CScrollLayer) {
                                CScrollLayer scrollLayer = (CScrollLayer) nodes.get(i);
                                if (scrollLayer != layer)
                                    scrollLayer.scrollTo((int) (scrollX * layer.getDepth()/width),
                                            (int) (scrollY * layer.getDepth()/ height));
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
            } catch (FileNotFoundException e) {
//                e.printStackTrace();
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

    private CAction createAction(MapNodeSprite spriteNode, MapAction mapAction) {
        CIntervalAction action = null;
        if (mapAction instanceof MapActionAlpha) {
            action = createAlphaAction((MapActionAlpha) mapAction);
        } else if (mapAction instanceof MapActionScale) {
            action = createScaleAction(spriteNode, (MapActionScale) mapAction);
        } else if (mapAction instanceof MapActionTranslate) {
            action = createMoveToAction((MapActionTranslate) mapAction);
        } else if (mapAction instanceof MapActionFrame) {
            action = createFrameAction((MapActionFrame) mapAction);
        } else if (mapAction instanceof MapActionSequence) {
            action = createSequenceAction(spriteNode, (MapActionSequence) mapAction);
        } else if (mapAction instanceof MapActionRotate) {
            action = createRotateAction((MapActionRotate) mapAction);
        }
        if (action != null) {
            if (mapAction.getRepeat() == -1) {
                return CRepeatAction.create(action, Integer.MAX_VALUE);
            } else {
                return CRepeatAction.create(action, mapAction.getRepeat());
            }
        }
        return null;
    }

    private CRotateToAction createRotateAction(MapActionRotate mapRotateAction){
        CRotateToAction action = CRotateToAction.create(mapRotateAction.mFrom,
                mapRotateAction.mDegree, mapRotateAction.getDuration());
        return action;
    }

    private CSequenceAction createSequenceAction(MapNodeSprite spriteNode,
                                                 MapActionSequence mapSequenceAction) {
        List<MapAction> mapActions = mapSequenceAction.getActions();
        if (mapActions == null || mapActions.isEmpty())
            return null;

        List<CAction> actions = new ArrayList<CAction>();
        for (int i = 0; i < mapActions.size(); i++) {
            MapAction mapAction = mapActions.get(i);
            CAction action = createAction(spriteNode, mapAction);
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

    private CScaleToAction createScaleAction(MapNodeSprite spriteNode, MapActionScale mapScaleAction) {
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
        Bitmap pressed = null;
        if (!TextUtils.isEmpty(path)) {
            String fileName = path.substring(0, path.indexOf("."));
            String suffix = path.substring(path.indexOf("."));
            pressed = loadBitmap(spriteNode.getId(), fileName + "_p" + suffix);
        }

        CTexture texture;
        if (pressed == null) {
            texture = CTexture.create(getDirector(), bitmap);
        } else {
            texture = PressableTexture.create(getDirector(), bitmap, pressed);
        }

        texture.setViewSize(spriteNode.getWidth(), spriteNode.getHeight());
        if (spriteNode.getActions() != null
                && !spriteNode.getActions().isEmpty()) {
            CSprite sprite = CSprite.create(getDirector(), texture);
            for (int i = 0; i < spriteNode.getActions().size(); i++) {
                MapAction mapAction = spriteNode.getActions().get(i);
                CAction action = createAction(spriteNode, mapAction);

                if (action != null) {
                    sprite.runAction(action);
                }
            }
            sprite.setAnchor(spriteNode.getAnchorX(), spriteNode.getAnchorY());
            sprite.setPosition(new Point(spriteNode.getX(), spriteNode.getY()));
            sprite.setViewSize(spriteNode.getWidth(), spriteNode.getHeight());
            return sprite;
        } else {
            texture.setAnchor(spriteNode.getAnchorX(), spriteNode.getAnchorY());
            texture.setPosition(new Point(spriteNode.getX(), spriteNode.getY()));
            return texture;
        }
    }


    private CTextNode createText(MapNodeText textNode) {
        CTextNode node = CTextNode.create(getDirector());
        node.setPosition(new Point(textNode.getX(), textNode.getY()));
        node.setViewSize(textNode.getWidth(), textNode.getHeight());
        node.setFontSize(UIUtils.dip2px(textNode.mFontSize));
        node.setColor(Color.parseColor(textNode.mColor));
        if (!TextUtils.isEmpty(textNode.mPressColor)) {
            node.setPressedColor(Color.parseColor(textNode.mPressColor));
        }
        node.setText(textNode.mText);
        if (textNode.mAlign != null) {
            if("topLeft".equals(textNode.mAlign)){
                node.setTextAlign(CAlign.TOP_LEFT);
            } else if("topCenter".equals(textNode.mAlign)){
                node.setTextAlign(CAlign.TOP_CENTER);
            } else if("topRight".equals(textNode.mAlign)){
                node.setTextAlign(CAlign.TOP_RIGHT);
            } else if("centerLeft".equals(textNode.mAlign)){
                node.setTextAlign(CAlign.CENTER_LEFT);
            } else if("center".equals(textNode.mAlign)){
                node.setTextAlign(CAlign.CENTER_CENTER);
            } else if("centerRight".equals(textNode.mAlign)){
                node.setTextAlign(CAlign.CENTER_RIGHT);
            } else if("bottomLeft".equals(textNode.mAlign)){
                node.setTextAlign(CAlign.BOTTOM_LEFT);
            } else if("bottomCenter".equals(textNode.mAlign)){
                node.setTextAlign(CAlign.BOTTOM_CENTER);
            } else if("bottomRight".equals(textNode.mAlign)){
                node.setTextAlign(CAlign.BOTTOM_RIGHT);
            } else {
                node.setTextAlign(CAlign.CENTER_CENTER);
            }
        } else {
            node.setTextAlign(CAlign.CENTER_CENTER);
        }

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
        node.setColor(Color.parseColor(line.mColor));

        node.setStartPoint(new CPoint(fromSprite.getX() + fromSprite.getWidth()/2,
                fromSprite.getY() + fromSprite.getHeight()/2));
        node.setEndPoint(new CPoint(toSprite.getX() + toSprite.getWidth()/2,
                toSprite.getY() + toSprite.getHeight()/2));
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
