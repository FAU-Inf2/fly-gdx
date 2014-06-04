package de.fau.cs.mad.fly;

public class Spaceship implements IPlane{

	@Override
	public float getSpeed() {
		return 2;
	}

	@Override
	public float getAzimuthSpeed() {
		return 0.9f;
	}

	@Override
	public float getRollingSpeed() {
		return 0.9f;
	}

}
