package de.fau.cs.mad.fly.tests.game;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileReader;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.badlogic.gdx.utils.Json;

import de.fau.cs.mad.fly.game.LevelProgress;
import de.fau.cs.mad.fly.res.Gate;
import de.fau.cs.mad.fly.res.Level;

public class LevelProgressTest {

	private static LevelProgress testProgress;
	private static Level testLevel;
	private static String testLevelPath = "test/src/de/fau/cs/mad/fly/tests/game/testLevel.json";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Json json = new Json();
		testLevel = json.fromJson(Level.class, new BufferedReader(
				new FileReader(testLevelPath)));
		testProgress = new LevelProgress(testLevel);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testListOfGates() {
		assertNotNull("The list of next gates should not be null.",
				testProgress.getNextGates());
		assertEquals(
				"The list of next gates should only contain one Gate at the beginning",
				1, testProgress.getNextGates().size());
	}

	@Test
	public void testGatePassed() {
		Gate testgate = testLevel.gates.get(0);
		testProgress.gatePassed(testgate);
		assertNotSame("", testgate, testProgress.getNextGates().get(0));
		assertEquals(
				"The list of next gates should only contain one Gate after the first gate",
				1, testProgress.getNextGates().size());
	}

}
