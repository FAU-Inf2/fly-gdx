package de.fau.cs.mad.fly.player.particle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader;
import com.badlogic.gdx.graphics.g3d.particles.emitters.Emitter;
import com.badlogic.gdx.graphics.g3d.particles.emitters.RegularEmitter;
import com.badlogic.gdx.math.Matrix4;

import de.fau.cs.mad.fly.game.ParticleController;
import de.fau.cs.mad.fly.res.Assets;

/**
 * Manages the particle effects for the spaceship.
 * 
 * @author Tobi
 *
 */
public class ShuttleParticle implements IParticle {

	private ParticleEffect effect;
	
	private String assetRef;

	/**
	 * Loads the particle for the shuttle.
	 * @param plane			The shuttle.
	 */
	public void load(String plane) {
		assetRef = "models/planes/effects/" + plane + ".pfx";
		Gdx.app.log("SpaceshipParticle", "Trying to load " + assetRef);

		ParticleEffectLoader.ParticleEffectLoadParameter loadParam = new ParticleEffectLoader.ParticleEffectLoadParameter(ParticleController.getInstance().getBatches());
		Assets.manager.load(assetRef, ParticleEffect.class, loadParam);
		Assets.manager.finishLoading();
	}
	
	/**
	 * Initializes the particle for the shuttle.
	 */
	public void init() {
		ParticleEffect originalEffect = Assets.manager.get(assetRef);

		effect = originalEffect.copy();
		effect.init();
		effect.start();
		ParticleController.getInstance().addEffect(effect);
	}
	
	/**
	 * Renders the particle for the shuttle.
	 * @param targetMatrix		The transform matrix for the particles.
	 */
	public void render(Matrix4 targetMatrix) {
	    effect.setTransform(targetMatrix);
	}
	
	/**
	 * Stops the particle for the shuttle.
	 */
	public void stop() {
		Emitter emitter = effect.getControllers().first().emitter;
		if(emitter instanceof RegularEmitter) {
			RegularEmitter reg = (RegularEmitter) emitter;
			reg.setEmissionMode(RegularEmitter.EmissionMode.EnabledUntilCycleEnd);
		}
	}

	public void dispose() {
		Assets.manager.unload(assetRef);
	}
}
