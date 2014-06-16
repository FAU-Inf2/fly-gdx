package de.fau.cs.mad.fly.script;

import org.mozilla.javascript.*;

/**
 * Created by danyel on 15/06/14.
 */
public class Coffee {

	private final Engine engine;
	private final NativeObject coffeeScript;

	public Coffee(Engine e, NativeObject root) {
		this.engine = e;
		this.coffeeScript = root;
	}

	public String compile(String coffee) {
		return call("compile", coffee);
	}

	public <T> T eval(String coffee) {
		return call("eval", coffee);
	}

	public <T> T run(String coffee, String fileName) {
		try {
			Scriptable x = Context.enter().newObject(engine.root);
			ScriptableObject.putProperty(x, "filename", fileName);
			return call("run", coffee, x);
		} finally {
			Context.exit();
		}
	}

	private <T> T call(String f, String coffee) {
		try {
			return call(f, coffee, Context.enter().newObject(engine.root));
		} finally {
			Context.exit();
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T call(String f, String coffee, Scriptable o) {
		return (T) engine.call(coffeeScript, f, coffee, o);
	}
}