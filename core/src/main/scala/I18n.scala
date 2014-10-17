package de.fau.cs.mad.fly

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.I18NBundle
import java.util.Locale
import java.text.NumberFormat

/**
 * Created by danyel on 16/06/14.
 */
object I18n {
  def f(key: String, args: AnyRef*) = gameBundle.format(key, args)

  def t(key: String) = gameBundle.get(key)

  def fLevel(key: String, args: AnyRef*) = levelBundle.format(key, args)

  def tLevel(key: String) = levelBundle.get(key)

  def floatToString(floatNumber: Float) = {
    val locale = new Locale(gameBundle.getLocale.getLanguage, gameBundle.getLocale.getCountry)
    val nf = NumberFormat.getNumberInstance(locale);
    nf.format(floatNumber);
  }

  /**
   * Locale used for the UI, settings etc.
   */
  var gameLocale = Locale.ENGLISH
  /**
   * Locale used for the specific messages in the level script files.
   */
  var levelLocale = Locale.ENGLISH
  private val gameBaseFileHandle = Gdx.files.internal("config/locales/Bundle")
  private val gameBundle = I18NBundle.createBundle(gameBaseFileHandle, gameLocale)
  private val levelBaseFileHandle = Gdx.files.internal("config/locales/LevelBundle")
  private val levelBundle = I18NBundle.createBundle(levelBaseFileHandle, levelLocale)
}
