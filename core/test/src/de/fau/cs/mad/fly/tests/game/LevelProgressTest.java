package de.fau.cs.mad.fly.tests.game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.io.BufferedReader;
import java.io.FileReader;

import org.junit.BeforeClass;
import org.junit.Test;

import com.badlogic.gdx.utils.Json;

import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.game.LevelProgress;
import de.fau.cs.mad.fly.res.Gate;
import de.fau.cs.mad.fly.res.Level;

/**
 * Test the class Level Progress.
 * 
 * @author Lukas Hahmann
 * 
 */
public class LevelProgressTest {

	private static LevelProgress testProgress;
	private static Level testLevel;
	private static String testLevelPath = "test/src/de/fau/cs/mad/fly/tests/game/testLevel.json";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@Test
	public void testListOfGates() {

	}

	@Test
	public void testGatePassed() {
	
	}

}
