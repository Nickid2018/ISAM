package com.github.isam.input;

public interface KeyboardInputListener {

	public void onKeyInput(long window, int key, int scancode, int action, int mods);
	
	public void onCharInput(long window, int codepoint);
	
	public void onCharModInput(long window, int codepoint, int mods);
}
