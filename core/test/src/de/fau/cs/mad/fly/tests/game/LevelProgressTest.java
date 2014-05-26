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

		// passing first gate
		testProgress.gatePassed(testgate);
		assertNotSame(
				"When passing a valid gate, a new gate should be the next gate.",
				testgate, testProgress.getNextGates().get(0));
		assertEquals(
				"The list of next gates should only contain one gate after the first gate",
				1, testProgress.getNextGates().size());

		// passing second gate
		testgate = testLevel.gates.get(1);
		testProgress.gatePassed(testgate);
		assertNotSame(
				"When passing a valid gate, a new gate should be the next gate.",
				testgate, testProgress.getNextGates().get(0));
		assertEquals(
				"The list of next gates should only contain one gate after the second gate",
				1, testProgress.getNextGates().size());

		// passing third gate
		testgate = testLevel.gates.get(2);
		testProgress.gatePassed(testgate);
		assertNotSame(
				"When passing a valid gate, a new gate should be the next gate.",
				testgate, testProgress.getNextGates().get(0));
		assertEquals(
				"The list of next gates should only contain two gates after the third gate",
				2, testProgress.getNextGates().size());

		// passing fourth gate
		testgate = testLevel.gates.get(3);
		testProgress.gatePassed(testgate);
		assertNotSame(
				"When passing a valid gate, a new gate should be the next gate.",
				testgate, testProgress.getNextGates().get(0));
		assertEquals(
				"The list of next gates should only contain two gates after the fourth gate",
				2, testProgress.getNextGates().size());
		
		// passing last gate
		testgate = testLevel.gates.get(5);
		testProgress.gatePassed(testgate);
		assertNotSame(
				"When passing a valid gate, a new gate should be the next gate.",
				testgate, testProgress.getNextGates().get(0));
		assertEquals(
				"The list of next gates should only contain two gates after the fourth gate",
				1, testProgress.getNextGates().size());
	}

}
