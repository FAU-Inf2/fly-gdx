package de.fau.cs.mad.fly;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.Locale;

/**
 * Created by danyel on 16/06/14.
 */
public final class I18n {
	public static Locale locale = Locale.GERMAN;

	private static final FileHandle baseFileHandle = Gdx.files.internal("config/locales/Bundle");
	private static I18NBundle bundle = I18NBundle.createBundle(baseFileHandle, locale);

	public static String f(String key, Object... args) {
		return format(key, args);
	}

	public static String format(String key, Object... args) {
		updateLocale();
		return bundle.format(key, args);
	}

	public static String t(String key) {
		return translate(key);
	}

	public static String translate(String key) {
		updateLocale();
		return bundle.get(key);
	}

	private static void updateLocale() {
		if ( locale == bundle.getLocale() )
			return;
		bundle = I18NBundle.createBundle(baseFileHandle, locale);
	}
}