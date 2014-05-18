package de.fau.cs.mad.fly.res;

import com.badlogic.gdx.files.FileHandle;

/**
 * Created by danyel on 18/05/14.
 */
public interface Loader {
	/**
	 * A loadable resource, called only when f exists.
	 * @param f the FileHandle that leads to this resource's path.
	 * @return the resource that corresponds to this path.
	 */
	public Resource load(FileHandle f);
}