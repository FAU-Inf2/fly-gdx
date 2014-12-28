package de.fau.cs.mad.fly.desktop;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import de.fau.cs.mad.fly.Fly;

public class DesktopLauncher {

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "FLY";
		config.width = 960;
		config.height = 640;
		// config.fullscreen = true;

		new LwjglApplication(new Fly(), config);

		try {
			Scanner in = new Scanner(new FileReader("DesktopVersion.txt"));
			Fly.VERSION = in.nextLine().substring(8);
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
}
