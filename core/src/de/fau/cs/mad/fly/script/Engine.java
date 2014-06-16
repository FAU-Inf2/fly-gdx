package de.fau.cs.mad.fly.script;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import org.mozilla.javascript.*;

/**
 * Created by danyel on 15/06/14.
 */
public class Engine {
	protected final Scriptable root;
	protected final Coffee coffee;

	public Engine() {
		this("scripts/vendor/coffeescript.js");
	}

	public Engine(String coffeePath) {
		this(Gdx.files.internal(coffeePath));
	}

	public Engine(FileHandle coffee) {
		root = Context.enter().initStandardObjects();
		Context.exit();
		this.coffee = getCoffee(coffee);
	}

	private Coffee getCoffee(FileHandle f) {
		load(f);
		NativeObject coffee = get(root, "CoffeeScript");
		return new Coffee(this, coffee);
	}

	public void loadDirectory(FileHandle dir, boolean recursive) {
		for ( FileHandle f : dir.list() ) {
			if ( f.isDirectory() ) {
				if ( recursive )
					loadDirectory(f, true);
			} else if ( f.exists() )
				load(f);
		}
	}

	public void loadDirectory(FileHandle dir) {
		loadDirectory(dir, true);
	}

	public void load(FileHandle f) {
		String source = f.readString();
		if ( f.extension().equals("coffee") )
			coffee.run(source, f.path());
		else
			eval(source, f.path());
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Scriptable x, String key) {
		return (T) x.get(key, x);
	}

	@SuppressWarnings("unchecked")
	public <T> T call(Scriptable thisObj, String function, Object... args) {
		return (T) FunctionObject.callMethod(thisObj, function, args);
	}

	public Scriptable construct(String constructor, Object... args) {
		try {
			return Context.enter().newObject(root, constructor, args);
		} finally {
			Context.exit();
		}
	}

	public Coffee getCoffeeScript() {
		return coffee;
	}

	public <T> T eval(String javascript) {
		return eval(javascript, "<raw>");
	}

	@SuppressWarnings("unchecked")
	public <T> T eval(String javascript, String fileName) {
		try {
			return (T) Context.enter().evaluateString(root, javascript, fileName, 1, null);
		} finally {
			Context.exit();
		}
	}
}