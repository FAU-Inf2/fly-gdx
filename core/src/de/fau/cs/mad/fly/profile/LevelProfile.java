package de.fau.cs.mad.fly.profile;
/**
 * Level properties for read level from json file
 * 
 * @author Fan
 *
 */
public class LevelProfile {
    public String name;
    public int id;
    public String file;
    
    public boolean isEndless() {
    	return name.equals("Endless");
    }
}