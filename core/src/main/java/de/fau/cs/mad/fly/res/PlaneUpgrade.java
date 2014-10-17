package de.fau.cs.mad.fly.res;

import java.util.Arrays;

/**
 * 
 * @author Sebastian
 * 
 */
public class PlaneUpgrade {
    public String name;
    public int timesAvailable;
    public int type;
    public int price;
    public int[] upgradeValues;

	@Override
	public String toString() {
		return "PlaneUpgrade{" +
				"name='" + name + '\'' +
				", timesAvailable=" + timesAvailable +
				", type=" + type +
				", price=" + price +
				", upgradeValues=" + Arrays.toString(upgradeValues) +
				'}';
	}
}
