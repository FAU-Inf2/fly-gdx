package de.fau.cs.mad.fly;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

import de.fau.cs.mad.fly.features.IFeatureLoad;
import de.fau.cs.mad.fly.features.IFeatureRender;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.game.GameObject;

public class Spaceship implements IPlane {
	private GameController gameController;
	private GameObject instance;
	private Model model;
	
	public Spaceship() {

	}
	
	@Override
	public void load(GameController game) {
		this.gameController = game;
		
		ModelBuilder modelBuilder = new ModelBuilder();
        model = modelBuilder.createSphere(0.1f, 0.1f, 0.1f, 2,2,
            new Material(ColorAttribute.createDiffuse(Color.GREEN)),
            Usage.Position | Usage.Normal);
        instance = new GameObject(model);
        instance.transform.setToTranslation(new Vector3(0, 0, 0));
	}
	
	@Override
	public void render(float delta) {
		instance.transform.setToTranslation(gameController.getCamera().position);
	}
	
	@Override
	public GameObject getInstance() {
		return instance;
	}
	
	@Override
	public Model getModel() {
		return model;
	}

	@Override
	public float getSpeed() {
		return 2;
	}

	@Override
	public float getAzimuthSpeed() {
		return 0.9f;
	}

	@Override
	public float getRollingSpeed() {
		return 0.9f;
	}
	
	@Override
	public void dispose() {
		instance.dispose();
		model.dispose();
	}

}
