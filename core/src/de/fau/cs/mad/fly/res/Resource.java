package de.fau.cs.mad.fly.res;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by danyel on 15/05/14.
 */
public abstract class Resource {

	public String id;

	public Map<String, Resource> dependencies = Collections.emptyMap();

	protected Resource(String id) {
		this.id = id;
	}

	protected Resource() {
		this(null);
	}

	/**
	 * @return {@code true} if this resource and all its dependencies are available, {@code false} otherwise.
	 */
	public boolean isSatisfied() {
		boolean b = true;
		for ( Resource r : dependencies.values() )
			b &= r.isSatisfied();
		return b;
	}

	public static Resource unsatisfied(String id) {
		return new UnsatisfiedResource(id);
	}

	private static class UnsatisfiedResource extends Resource {
		private UnsatisfiedResource(String id) {
			super(id);
		}

		public boolean isSatisfied() { return false; }
	}
}
