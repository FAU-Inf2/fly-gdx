package de.fau.cs.mad.fly.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import de.fau.cs.mad.fly.Fly;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "FLY";
		//config.width = 640;
		//config.height = 8;
		new LwjglApplication(new Fly(), config);
	}
}
