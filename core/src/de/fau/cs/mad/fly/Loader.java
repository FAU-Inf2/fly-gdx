package de.fau.cs.mad.fly;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import de.fau.cs.mad.fly.profile.LevelManager;
import de.fau.cs.mad.fly.profile.PlayerManager;
import de.fau.cs.mad.fly.res.Assets;
import de.fau.cs.mad.fly.res.Level;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Jakob Falke on 18.06.14.
 */
public class Loader<T> {

	private Collection<ProgressListener<T>> listeners = new ArrayList<ProgressListener<T>>();
	private float progress = 0;
	private AssetManager manager;
	private AssetDescriptor<T> target;

	public Loader(AssetManager manager, String target, Class<T> type) {
		this(manager, new AssetDescriptor<T>(target, type));
	}

	public Loader(AssetManager manager, AssetDescriptor<T> target) {
		this.manager = manager;
		this.target = target;
	}

	public void addProgressListener(ProgressListener<T> listener) {
		this.listeners.add(listener);
	}

	public void initiate() {
		manager.load(target);
		for ( ProgressListener l : listeners )
			l.progressStarted();
	}

	public void update() {
		if(!manager.update()) {
			float currentProgress = ( (float) manager.getLoadedAssets() / (float) (manager.getLoadedAssets() + manager.getQueuedAssets())) * 100f;
			if(currentProgress > progress) {
				progress = currentProgress;
				for ( ProgressListener l : listeners )
					l.progressUpdated(currentProgress);
			}
		} else {
			for ( ProgressListener<T> l : listeners ) {
				l.progressUpdated(100f);
				l.progressFinished(manager.get(target));
			}
		}
	}

	public static <P> Loader<P> create(AssetManager manager, String target, Class<P> type) {
		return new Loader<P>(manager, target, type);
	}
}
