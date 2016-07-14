package com.github.fredrikzkl.furyracers;

import org.newdawn.slick.geom.Vector2f;

public interface Box {

	public float getWidth();
	public float getLength();
	public Vector2f getPosition();
	public float getCenterOfRotationYOffset();
	public float getRadDeg();
}
