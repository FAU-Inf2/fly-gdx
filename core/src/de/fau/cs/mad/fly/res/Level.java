package de.fau.cs.mad.fly.res;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.utils.Disposable;
import de.fau.cs.mad.fly.features.ICollisionListener;
import de.fau.cs.mad.fly.features.IFeatureLoad;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.game.GameObject;
import de.fau.cs.mad.fly.geo.Perspective;
import de.fau.cs.mad.fly.player.Spaceship;

import java.util.*;

/**
 * 
 * @author Lukas Hahmann
 * 
 */
public class Level implements Disposable, IFeatureLoad, ICollisionListener {
	public static class Gate implements Iterable<Gate> {
		public final int id;
		public GameObject display;
		public GameObject goal;
		public Collection<Gate> successors;

		public Gate(Integer id) {
			this.id = id;
		}

		public void mark() {
			if ( display != null )
				display.mark();
		}

		public void unmark() {
			if ( display != null )
				display.unmark();
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Gate gate = (Gate) o;
			return id == gate.id;
		}

		@Override
		public int hashCode() {
			return id;
		}

		@Override
		public String toString() {
			return "#<Gate " + id + ">";
		}

		private void buildIterator(Collection<Gate> gs) {
			for ( Gate g : successors )
				if ( !gs.contains( g ) ) {
					gs.add(g);
					g.buildIterator(gs);
				}
		}

		@Override
		public Iterator<Gate> iterator() {
			final Set<Gate> set = new HashSet<Gate>();
			buildIterator(set);
			return set.iterator();
		}
	}

	public static class Head {
		public String name;
		public FileHandle file;
	}

	public static interface EventListener {
		public void onFinished();
		public void onGatePassed(Gate gate, Iterable<Gate> current);
	}


	public static class EventAdapter implements EventListener {
		@Override
		public void onFinished() {}
		@Override
		public void onGatePassed(Gate gate, Iterable<Gate> current) {}
	}

	@Override
	public void load(GameController game) {
		activeGatePassed(virtualGate);
	}

	/**
	 * Radius of the Level which defines the outer boundary which should be
	 * never reached by the user. The default level border defines a sphere with
	 * radius 100.
	 */
	public final float radius = 100.0f;
	public final Head head;
	public final Collection<GameObject> components;
	public final Perspective start;
	private Gate virtualGate;
	private final Gate startingGate;
	private final Environment environment;
	private List<EventListener> eventListeners = new ArrayList<EventListener>();

	public Level(String name, Perspective start, Collection<GameObject> components, Gate startingGate) {
		this.head = new Head();
		this.head.name = name;
		this.virtualGate = startingGate;
		this.startingGate = startingGate;
		this.components = components;
		this.start = start;
		this.environment = new Environment();
		setUpEnvironment();
	}

	public void gatePassed(Gate gate) {
		if ( currentGates().contains(gate) )
			activeGatePassed(gate);
	}

	public void activeGatePassed(Gate gate) {
		for ( EventListener s : eventListeners)
			s.onGatePassed(gate, virtualGate.successors);
		virtualGate = gate;
		if ( gate.successors.isEmpty() )
			for ( EventListener s : eventListeners)
				s.onFinished();
	}

	public void addEventListener(EventListener listener) {
		eventListeners.add(listener);
	}

	public Collection<Gate> currentGates() {
		return Collections.unmodifiableCollection(virtualGate.successors);
	}

	public Iterable<Gate> allGates() {
		return startingGate;
	}

	public Iterable<Gate> remainingGates() { return virtualGate; }

	/**
	 * Environment in the level.
	 * <p>
	 * Includes ambient and directional lights.
	 * 
	 * @return environment
	 */
	public Environment getEnvironment() {
		return environment;
	}

	/**
	 * Render the level
	 * 
	 * @param camera
	 *            that displays the level
	 */
	public void render(ModelBatch batch, PerspectiveCamera camera) {
		for (GameObject c : components)
			c.render(batch, environment, camera);
	}

	/**
	 * Sets up the environment for the level with its light.
	 */
	private void setUpEnvironment() {
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
	}

	@Override
	public String toString() {
		return "#<Level name=" + head.name + " virtualGate=" + virtualGate + ">";
	}

	@Override
	public void dispose() {
		Gdx.app.log("Level.dispose", "Disposing...");
		for ( GameObject o : components )
			o.dispose();
	}

	@Override
	public void onCollision(GameObject o1, GameObject o2) {
		if ( !playerGateCollision(o1, o2) )
			playerGateCollision(o2, o1);
	}

	private boolean playerGateCollision(GameObject x, GameObject y) {
		if ( x.userData instanceof Spaceship && y.userData instanceof Gate ) {
			Gate gate = (Gate) y.userData;
			gatePassed(gate);
			return true;
		}
		return false;
	}
}