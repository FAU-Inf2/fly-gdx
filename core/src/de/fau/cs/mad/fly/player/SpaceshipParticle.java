package de.fau.cs.mad.fly.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.graphics.g3d.particles.batches.PointSpriteParticleBatch;
import com.badlogic.gdx.graphics.g3d.particles.emitters.Emitter;
import com.badlogic.gdx.graphics.g3d.particles.emitters.RegularEmitter;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Disposable;

import de.fau.cs.mad.fly.res.Assets;

/**
 * Manages the particle effects for the spaceship.
 * 
 * @author Tobi
 *
 */
public class SpaceshipParticle implements Disposable {
	
	private ParticleSystem particleSystem;
	private PointSpriteParticleBatch pointSpriteBatch;
	private ParticleEffect effect;
	
	private String assetRef;
	
	private ModelBatch batch;
	
	public SpaceshipParticle() {
	}

	
	public void load(Camera camera, ModelBatch batch, String plane) {
		this.batch = batch;

		Gdx.app.log("SpaceshipParticle", "1");
		
		particleSystem = ParticleSystem.get();
		Gdx.app.log("SpaceshipParticle", "2");
		pointSpriteBatch = new PointSpriteParticleBatch();
		Gdx.app.log("SpaceshipParticle", "3");
		
		pointSpriteBatch.setCamera(camera);
		Gdx.app.log("SpaceshipParticle", "4");
		particleSystem.add(pointSpriteBatch);
		Gdx.app.log("SpaceshipParticle", "5");

		assetRef = "models/planes/" + plane + "/effects.pfx";
		Gdx.app.log("SpaceshipParticle", "Trying to load " + assetRef);

		ParticleEffectLoader.ParticleEffectLoadParameter loadParam = new ParticleEffectLoader.ParticleEffectLoadParameter(particleSystem.getBatches());
		ParticleEffectLoader loader = new ParticleEffectLoader(new InternalFileHandleResolver());
		Gdx.app.log("SpaceshipParticle", "6");
		Assets.manager.setLoader(ParticleEffect.class, loader);
		Assets.manager.load(assetRef, ParticleEffect.class, loadParam);
		Assets.manager.finishLoading();
	}
	
	public void init() {
		Gdx.app.log("SpaceshipParticle", "7");
		ParticleEffect originalEffect = Assets.manager.get(assetRef);
		Gdx.app.log("SpaceshipParticle", "8");

		effect = originalEffect.copy();
		effect.init();
		effect.start();
		particleSystem.add(effect);
	}
	
	public void render(Matrix4 targetMatrix) {
	    effect.setTransform(targetMatrix);
	    particleSystem.update();
	    particleSystem.begin();
	    particleSystem.draw();
	    particleSystem.end();
	    batch.render(particleSystem);
	}
	
	public void stop() {
		Emitter emitter = effect.getControllers().first().emitter;
		if(emitter instanceof RegularEmitter) {
			RegularEmitter reg = (RegularEmitter) emitter;
			reg.setEmissionMode(RegularEmitter.EmissionMode.EnabledUntilCycleEnd);
		}
	}
	
	public void dispose() {
		
	}
}
