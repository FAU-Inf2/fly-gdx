package de.fau.cs.mad.fly.player;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import de.fau.cs.mad.fly.game.CollisionDetector;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.game.GameModel;
import de.fau.cs.mad.fly.game.GameObject;

public class Spaceship implements IPlane {
	private GameController gameController;
	private GameObject instance;
	private Model model;
	
	public Spaceship() {

	}
	
	@Override
	public void load(final GameController game) {
		this.gameController = game;
		
		final ModelBuilder modelBuilder = new ModelBuilder();
        model = modelBuilder.createSphere(0.1f, 0.1f, 0.1f, 2,2, new Material(ColorAttribute.createDiffuse(Color.GREEN)), Usage.Position | Usage.Normal);
        instance = new GameObject(new GameModel(model));
        instance.transform.setToTranslation(new Vector3(0, 0, 0));

		instance.filterGroup = CollisionDetector.PLAYER_FLAG;
		instance.filterMask = CollisionDetector.ALL_FLAG;
		instance.setCollisionObject(new btSphereShape(0.1f), CollisionDetector.Types.Player, this);
		gameController.getCollisionDetector().addCollisionObject(instance);
	}

	@Override
	public void render(float delta) {
		instance.transform.setToTranslation(gameController.getCamera().position);

		instance.getCollisionObject().setWorldTransform(instance.transform);
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
		return 9.0f;
	}

	@Override
	public float getRollingSpeed() {
		return 9.0f;
	}
	
	
	@Override
	public void dispose() {
		instance.dispose();
		model.dispose();
	}
}
