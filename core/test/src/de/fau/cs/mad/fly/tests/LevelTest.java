package de.fau.cs.mad.fly.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import com.badlogic.gdx.graphics.g3d.ModelInstance;

import de.fau.cs.mad.fly.res.Level;

public class LevelTest {
	
	private Level level = new Level();

	@Test
	public void testToString() {
		System.out.println(level.toString());
		System.out.println("There is nothing to test here.");
	}
	
	@Test
	public void testGetLevelBorder(){
		//ModelInstance model = level.getLevelBorder();	
	}
	
}
