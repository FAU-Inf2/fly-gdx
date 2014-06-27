package de.fau.cs.mad.fly.script;

import com.badlogic.gdx.Gdx;
import de.fau.cs.mad.fly.res.Level;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * Created by danyel on 15/06/14.
 */
public class FlyEngine extends Engine implements Level.EventListener {
	private Scriptable fly;

	private FlyEngine() {
		super();
		loadFramework();
	}

	private void loadFramework() {
		loadDirectory(Gdx.files.internal("scripts/framework"));
	}

	public void setLevel(Level level) {
		fly = construct("Fly", level);
		ScriptableObject.putProperty(root, "fly", fly);
	}

	@Override
	public void onFinished() {
		call(fly, "trigger", "finish");
	}

	@Override
	public void onGatePassed(Level.Gate gate, Iterable<Level.Gate> current) {
		call(fly, "trigger", "gatepass", gate, current);
	}
	
	@Override
	public void onUpdate() {
		//call(fly, "trigger", "update");
	}

	@Override
	public void onRender() {
		call(fly, "trigger", "render");
	}

	private static FlyEngine e = new FlyEngine();
	public static FlyEngine get() {
		return e;
	}
}
