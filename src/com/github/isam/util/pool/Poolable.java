package com.github.isam.util.pool;

/**
 * Objects implementing this interface will have {@link #reset()} called when
 * passed to {@link #free(Object)}.
 */
public interface Poolable {
	/**
	 * Resets the object for reuse. Object references should be nulled and fields
	 * may be set to default values.
	 */
	public void reset();
}