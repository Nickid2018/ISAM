package com.github.isam.input;

import com.github.isam.phys.*;

public class MouseEvent {

	public enum Type {
		PRESS, REPEAT, RELEASE, SCROLL
	}

	public static boolean checkInRange(AABB position, int xpos, int ypos) {
		return position.contains(xpos, ypos);
	}
}
