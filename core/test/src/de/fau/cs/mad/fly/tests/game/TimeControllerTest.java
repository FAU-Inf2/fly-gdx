package de.fau.cs.mad.fly.tests.game;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.fau.cs.mad.fly.game.TimeController;

/** Test class for {@link TimeController} */
public class TimeControllerTest {

	private static TimeController timeControllerTestInstance;
	private static float testTimeInSeconds;
	private static TestIntegerTimeUpdateListener integerTimeUpdateListener1;
	private static TestIntegerTimeUpdateListener integerTimeUpdateListener2;
	private static TestTimeIsUpListener testTimeIsUpListener;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		timeControllerTestInstance = new TimeController();
		testTimeInSeconds = 1f;
		integerTimeUpdateListener1 = new TestIntegerTimeUpdateListener();
		integerTimeUpdateListener2 = new TestIntegerTimeUpdateListener();
		timeControllerTestInstance.registerIntegerTimeListener(integerTimeUpdateListener1);
		timeControllerTestInstance.registerIntegerTimeListener(integerTimeUpdateListener2);
		testTimeIsUpListener = new TestTimeIsUpListener();
		timeControllerTestInstance.registerTimeIsUpListener(testTimeIsUpListener);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		timeControllerTestInstance.initTimer(testTimeInSeconds);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInitTimer() {
		timeControllerTestInstance.initTimer(-1);
	}

	@Test
	public void testCheckTime() {
		timeControllerTestInstance.checkTime();
		assertEquals((int) testTimeInSeconds, integerTimeUpdateListener1.getTime());
		assertEquals((int) testTimeInSeconds, integerTimeUpdateListener1.getTime());
		assertEquals(false, testTimeIsUpListener.isTimeUp());
		try {
			Thread.sleep((int) testTimeInSeconds *1000);
			timeControllerTestInstance.checkTime();
			assertEquals((int) 0, integerTimeUpdateListener1.getTime());
			assertEquals((int) 0, integerTimeUpdateListener1.getTime());
			assertEquals(true, testTimeIsUpListener.isTimeUp());
			
			Thread.sleep((int) testTimeInSeconds *1000);
			timeControllerTestInstance.checkTime();
			assertEquals((int) 0, integerTimeUpdateListener1.getTime());
			assertEquals((int) 0, integerTimeUpdateListener1.getTime());
			assertEquals(true, testTimeIsUpListener.isTimeUp());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
