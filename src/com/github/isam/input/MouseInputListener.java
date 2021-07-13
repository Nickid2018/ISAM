package com.github.isam.input;

public interface MouseInputListener {

	public void onMouse(long window, int button, int action, int mods);

	public void onScroll(long window, double xoffset, double yoffset);
	
	public void onDrop(long window, int count, long names);
	
	public void onMove(long window, double xpos, double ypos);
}
