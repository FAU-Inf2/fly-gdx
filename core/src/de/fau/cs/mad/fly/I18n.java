package de.fau.cs.mad.fly;

import java.text.NumberFormat;
import java.util.Locale;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.I18NBundle;

/**
 * Created by danyel on 16/06/14.
 */
public final class I18n {
    /**
     * Locale used for the UI, settings etc.
     */
    public static Locale gameLocale = Locale.ENGLISH;
    
    /**
     * Locale used for the specific messages in the level script files.
     */
    public static Locale levelLocale = Locale.ENGLISH;
    
    private static final FileHandle gameBaseFileHandle = Gdx.files.internal("config/locales/Bundle");
    private static I18NBundle gameBundle = I18NBundle.createBundle(gameBaseFileHandle, gameLocale);
    
    private static final FileHandle levelBaseFileHandle = Gdx.files.internal("config/locales/LevelBundle");
    private static I18NBundle levelBundle = I18NBundle.createBundle(levelBaseFileHandle, levelLocale);
    
    public static String f(String key, Object... args) {
        return gameBundle.format(key, args);
    }
    
    public static String t(String key) {
        return gameBundle.get(key);
    }
    
    public static String fLevel(String key, Object... args) {
        return levelBundle.format(key, args);
    }
    
    public static String tLevel(String key) {
        return levelBundle.get(key);
    }
    
    public static String floatToString(float floatNumber) {
        Locale locale = new Locale(gameBundle.getLocale().getLanguage(), gameBundle.getLocale().getCountry());
        NumberFormat nf = NumberFormat.getNumberInstance(locale);
        return nf.format(floatNumber);
    }
    
    private static void updateLocales() {
        if (gameLocale == gameBundle.getLocale() && levelLocale == levelBundle.getLocale())
            return;
        gameBundle = I18NBundle.createBundle(gameBaseFileHandle, gameLocale);
        levelBundle = I18NBundle.createBundle(levelBaseFileHandle, levelLocale);
    }
}