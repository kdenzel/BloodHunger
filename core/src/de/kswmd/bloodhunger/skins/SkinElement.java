package de.kswmd.bloodhunger.skins;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import de.kswmd.bloodhunger.Assets;
import de.kswmd.bloodhunger.BloodHungerGame;
import de.kswmd.bloodhunger.components.DimensionComponent;

/**
 * Holds a single animation with all the polygons for the Skin
 */
public class SkinElement {

    public final Animation<TextureRegion> animation;
    //Array with polygons
    private final Array<float[]> polygonVertices = new Array<>(1);
    private final Array<float[]> polygonVerticesTransformed = new Array<>(1);

    public SkinElement(String rootSkinPath, float initialFrameDuration, String resource, Animation.PlayMode playMode) {
        String fullResourcePath = rootSkinPath + "/" + resource;
        TextureAtlas atlas = BloodHungerGame.ASSET_MANAGER.get(Assets.TEXTURE_ATLAS_GAME_ANIMATIONS);
        this.animation = new Animation<>(initialFrameDuration,
                atlas.findRegions(fullResourcePath), playMode);
        loadPolygons(resource);
    }

    /**
     * loads the polygons for each frame
     * @param resource loads the .poly file for the specified resource
     */
    public void loadPolygons(String resource) {
        FileHandle handle = Gdx.files.internal("animation/" + resource + ".poly");
        //Create as much polygons as defined in the file
        if (handle.exists()) {
            String fileContent = handle.readString();
            String[] lines = fileContent.split("\\r?\\n");
            for (String line : lines) {
                String[] array = line.replaceAll("[{}]", "").split(",");
                float[] vertices = new float[array.length];
                for (int j = 0; j < array.length; j++) {
                    vertices[j] = Float.parseFloat(array[j]);
                }
                polygonVertices.add(vertices);
            }

        } else {
            //Create per default a square from 0,0 to width height
            polygonVertices.add(new float[]{0, 0, 1, 0, 1, 1, 0, 1});
        }
        polygonVertices.forEach(v -> polygonVerticesTransformed.add(new float[v.length]));
    }

    public boolean hasPolygons() {
        return !polygonVertices.isEmpty();
    }

    public float[] getPolygonInWorldSize(float time, float width, float height) {
        if (polygonVertices.isEmpty()) {
            return null;
        }
        float scale = ((float) animation.getKeyFrameIndex(time) / animation.getKeyFrames().length);
        int polygonFrame = (int) (polygonVertices.size * scale);


        float[] v = polygonVertices.get(polygonFrame);
        float[] tv = polygonVerticesTransformed.get(polygonFrame);
        for (int i = 0; i < v.length; i++) {
            if (i % 2 == 0) {
                tv[i] = v[i] * width;
            } else {
                tv[i] = v[i] * height;
            }
        }
        return tv;
    }

    public float[] getPolygonInWorldSize(float time, DimensionComponent dimensionComponent) {
        return getPolygonInWorldSize(time, dimensionComponent.width, dimensionComponent.height);
    }
}
