package de.fau.cs.mad.fly.features.overlay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Stage;

import de.fau.cs.mad.fly.features.IFeatureDraw;
import de.fau.cs.mad.fly.ui.SkinManager;

public class TouchScreenOverlay implements IFeatureDraw {
    
    public static final int X_POS_OF_STEERING_CIRCLE = (int) (Gdx.graphics.getWidth() / 3.2);
    
    public static final int Y_POS_OF_STEERING_CIRCLE = (int) (-Gdx.graphics.getHeight() / 3.6);
    
    public static final int RADIUS_OF_STEERING_CIRCLE = Gdx.graphics.getWidth() / 10;
    
    private final int FRAGMENTS_OF_STEERING_CIRCLE = 40;
    private final Color COLOR_OF_STEERING_CIRCLE;
    private final OrthographicCamera camera;
    private final ShapeRenderer shapeRenderer;
    
    public TouchScreenOverlay(final Stage stage) {
        this.shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        COLOR_OF_STEERING_CIRCLE = SkinManager.getInstance().getSkin().getColor("lightGrey");
    }
    
    @Override
    public void draw(float delta) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.setColor(COLOR_OF_STEERING_CIRCLE);
        shapeRenderer.circle(X_POS_OF_STEERING_CIRCLE, Y_POS_OF_STEERING_CIRCLE, RADIUS_OF_STEERING_CIRCLE, FRAGMENTS_OF_STEERING_CIRCLE);
        shapeRenderer.end();
    }
}