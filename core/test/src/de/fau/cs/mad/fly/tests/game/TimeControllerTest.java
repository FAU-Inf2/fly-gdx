package de.fau.cs.mad.fly.tests.game;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.fau.cs.mad.fly.game.TimeController;

/** Test class for {@link TimeController} */
public class TimeControllerTest {

	private static TimeController timeControllerTestInstance;
	private static TestIntegerTimeUpdateListener integerTimeUpdateListener1;
	private static TestIntegerTimeUpdateListener integerTimeUpdateListener2;
	private static TestTimeIsUpListener testTimeIsUpListener;

	@Before
	public void setUp() throws Exception {
		timeControllerTestInstance = new TimeController();
		integerTimeUpdateListener1 = new TestIntegerTimeUpdateListener();
		integerTimeUpdateListener2 = new TestIntegerTimeUpdateListener();
		timeControllerTestInstance.registerIntegerTimeListener(integerTimeUpdateListener1);
		timeControllerTestInstance.registerIntegerTimeListener(integerTimeUpdateListener2);
		testTimeIsUpListener = new TestTimeIsUpListener();
		timeControllerTestInstance.registerTimeIsUpListener(testTimeIsUpListener);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInitTimer() {
		timeControllerTestInstance.initAndStartTimer(-1);
	}

	@Test
	public void testCheckTime() {
		float testTimeInSeconds = 1f;
		timeControllerTestInstance.initAndStartTimer(testTimeInSeconds);
		timeControllerTestInstance.checkTime();
		assertEquals((int) testTimeInSeconds, integerTimeUpdateListener1.getTime());
		assertEquals((int) testTimeInSeconds, integerTimeUpdateListener1.getTime());
		assertEquals(false, testTimeIsUpListener.isTimeUp());
		try {
			Thread.sleep((int) testTimeInSeconds * 1000);
			timeControllerTestInstance.checkTime();
			assertEquals((int) 0, integerTimeUpdateListener1.getTime());
			assertEquals((int) 0, integerTimeUpdateListener1.getTime());
			assertEquals(true, testTimeIsUpListener.isTimeUp());

			Thread.sleep((int) testTimeInSeconds * 1000);
			timeControllerTestInstance.checkTime();
			assertEquals((int) 0, integerTimeUpdateListener1.getTime());
			assertEquals((int) 0, integerTimeUpdateListener1.getTime());
			assertEquals(true, testTimeIsUpListener.isTimeUp());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testPause() {
		final int initTime = 3;
		for (int rounds = 0; rounds < 2; rounds++) {
			timeControllerTestInstance.initAndStartTimer(initTime);
			try {
				assertEquals(initTime, integerTimeUpdateListener1.getTime());
				Thread.sleep(1000);
				timeControllerTestInstance.checkTime();
				assertEquals(initTime - 1, integerTimeUpdateListener1.getTime());
				assertEquals(false, testTimeIsUpListener.isTimeUp());

				timeControllerTestInstance.pause();
				Thread.sleep(2000);
				timeControllerTestInstance.checkTime();
				assertEquals(initTime - 1, integerTimeUpdateListener1.getTime());
				assertEquals(false, testTimeIsUpListener.isTimeUp());

				timeControllerTestInstance.resume();
				Thread.sleep(500);
				timeControllerTestInstance.checkTime();
				assertEquals(initTime - 1, integerTimeUpdateListener1.getTime());
				assertEquals(false, testTimeIsUpListener.isTimeUp());
				
				timeControllerTestInstance.pause();
				Thread.sleep(2000);
				timeControllerTestInstance.checkTime();
				assertEquals(initTime - 1, integerTimeUpdateListener1.getTime());
				assertEquals(false, testTimeIsUpListener.isTimeUp());

				timeControllerTestInstance.resume();
				Thread.sleep(2000);
				timeControllerTestInstance.checkTime();
				assertEquals(0, integerTimeUpdateListener1.getTime());
				assertEquals(true, testTimeIsUpListener.isTimeUp());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			testTimeIsUpListener.resetTimeIsUp();
		}
	}
}
