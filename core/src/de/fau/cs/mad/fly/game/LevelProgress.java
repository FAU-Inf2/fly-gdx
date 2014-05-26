package de.fau.cs.mad.fly.game;

import java.util.ArrayList;

import de.fau.cs.mad.fly.res.Gate;
import de.fau.cs.mad.fly.res.Level;

/**
 * This class contains the current progress of the user in a level. Especially
 * which gates have passed.
 * 
 * @author Lukas Hahmann
 * 
 */
public class LevelProgress {

	/** list of all gates that can be passed next */
	private ArrayList<Gate> nextGates;

	private ArrayList<Gate> allGates;

	public LevelProgress(Level level) {
		nextGates = new ArrayList<Gate>();
		allGates = (ArrayList<Gate>) level.gates;
		// the first has always id 0 and there is no alternative to this first
		// gate
		nextGates.add(allGates.get(0));
	}

	/**
	 * Returns a list of all gates that should be passed next. In case of a
	 * linear level with no parallel routes, this list only contains one
	 * element.
	 */
	public ArrayList<Gate> getNextGates() {
		return nextGates;
	}

	/**
	 * This method indicates that a gate has been passed. If gate is in the list
	 * of next Gates, this list is updated. Otherwise, nothing is done.
	 * 
	 * @param gate
	 *            that has been passed
	 */
	public void gatePassed(Gate gate) {
		if (nextGates != null && nextGates.contains(gate)) {
			nextGates.remove(gate);
			if (gate.successors.size() == 0) {
				// level is finished
			} else {
				// add all successors to nextGates
				for (Integer id : gate.successors) {
					Gate g = new Gate();
					g.id = id;
					if (allGates.contains(g)) {
						nextGates.add(allGates.get(allGates.indexOf(g)));
					}
				}
			}
		}
	}

}
