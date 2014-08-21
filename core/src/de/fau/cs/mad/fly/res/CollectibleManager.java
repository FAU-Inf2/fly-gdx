package de.fau.cs.mad.fly.res;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

import de.fau.cs.mad.fly.features.upgrades.types.Collectible;

public class CollectibleManager {
	
    private List<Collectible> collectibles;
    
    public CollectibleManager() {
    	collectibles = new ArrayList<Collectible>();
    }
    
    public CollectibleManager(List<Collectible> collectibles) {
    	this.collectibles = collectibles;
    }

    public List<Collectible> getCollectibles() {
    	return collectibles;
    }
    
    public void setCollectibles(List<Collectible> collectibles) {
    	this.collectibles = collectibles;
    }
    
    public void removeCollectible(Collectible c) {
    	collectibles.remove(c);
    }
	
	public void moveCollectibles(float delta) {
		final int numberOfUpgrades = collectibles.size();
	    for(int i = 0; i < numberOfUpgrades; i++) {
	    	collectibles.get(i).move(delta);
	    }
	}
	
	public void render(ModelBatch batch, Environment environment, PerspectiveCamera camera) {
        final int numberOfUpgrades = collectibles.size();
        for(int i = 0; i < numberOfUpgrades; i++) {
        	collectibles.get(i).render(batch, environment, camera);
        }
	}
}
