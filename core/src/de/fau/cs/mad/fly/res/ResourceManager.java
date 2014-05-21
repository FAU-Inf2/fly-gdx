package de.fau.cs.mad.fly.res;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.*;

/**
 * This class is used to load levels.
 * 
 * @author Lukas Hahmann
 * 
 */
public class ResourceManager {
	private static final Map<String, TypeInformation> typeMap = new HashMap<String, TypeInformation>();
	private static final Map<String, Resource> loadedResources = new HashMap<String, Resource>();
	private static final Gson gson = new GsonBuilder().registerTypeAdapter(
			Resource.class, new TypeAdapter<Resource>() {
				@Override
				public void write(JsonWriter out, Resource value)
						throws IOException {
					out.value(value.id);
				}

				@Override
				public Resource read(JsonReader in) throws IOException {
					return ResourceManager.get(in.nextString());
				}
			}).create();

	public static Resource get(String identifier) {
		if (!loadedResources.containsKey(identifier))
			loadedResources.put(identifier, load(identifier));
		return loadedResources.get(identifier);
	}

	public static Level getLevel(String name) {
		return (Level) get("level:" + name);
	}

	/**
	 * Searches for all levels within the defined folder and returns them in a
	 * List.
	 * 
	 * @return ArrayList of all available levels
	 */
	public static ArrayList<Level> getLevelList() {
		ArrayList<Level> allLevels = new ArrayList<Level>();
		// TODO: fix platform dependency
		FileHandle dirHandle;
		if (Gdx.app.getType() == ApplicationType.Desktop) {
			dirHandle = Gdx.files.internal("bin/levels/");
		} else {
			dirHandle = Gdx.files.internal("levels/");
		}
		for (FileHandle fh : dirHandle.list()) {
			allLevels.add(getLevel(fh.nameWithoutExtension()));
		}
		return allLevels;
	}

	public static void register(String type, Class<? extends Resource> clazz,
			Loader loader, String prefix, String... possibleExtensions) {
		if (possibleExtensions.length == 0)
			throw new IllegalArgumentException(
					"You have to specify at least one extension.");
		typeMap.put(
				type,
				new TypeInformation(clazz, prefix, loader, Arrays
						.asList(possibleExtensions)));
	}

	static {
		register("level", Level.class, new Loader() {
			@Override
			public Resource load(FileHandle f) {
				return gson.fromJson(f.readString(), Level.class);
			}
		}, "levels/", ".json");
		register("model", ModelResource.class, new Loader() {
			@Override
			public Resource load(FileHandle f) {
				return new ModelResource(f);
			}
		}, "", ".obj");
	}

	private static Resource load(String identifier) {
		TypeInformation t = TypeInformation.get(identifier);
		if (t == null)
			throw new RuntimeException("Unknown resource type: "
					+ TypeInformation.typeFor(identifier)
					+ "! Register it first.");
		for (String p : t.possiblePaths(identifier)) {
			System.out.println("Path: " + p);
			FileHandle handle = Gdx.files.internal(p);
			if (handle.exists())
				return t.loader.load(handle);
		}
		return Resource.unsatisfied(identifier);
	}

	private static class TypeInformation {
		Class<? extends Resource> clazz;
		Iterable<String> possibleExtensions;
		String prefix;
		Loader loader;

		private TypeInformation(Class<? extends Resource> clazz, String prefix,
				Loader loader, Iterable<String> possibleExtensions) {
			this.clazz = clazz;
			this.loader = loader;
			this.possibleExtensions = possibleExtensions;
			this.prefix = prefix;
		}

		public Iterable<String> possiblePaths(String identifier) {
			String file = fileFor(identifier);
			List<String> paths = new ArrayList<String>();
			for (String e : possibleExtensions)
				paths.add(prefix + file + e);
			return paths;
		}

		public static TypeInformation get(String identifier) {
			return typeMap.get(typeFor(identifier));
		}

		public static String typeFor(String identifier) {
			return identifier.split(":")[0];
		}

		public static String fileFor(String identifier) {
			return identifier.split(":")[1];
		}
	}
}
