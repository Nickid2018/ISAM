package com.github.isam.util.pool;

import java.util.*;
import com.google.common.base.*;

public abstract class Pool<T extends Poolable> {

	/** The maximum number of objects that will be pooled. */
	public final int max;
	/** The highest number of free objects. Can be reset any time. */
	public int peak;

	private final Stack<T> freeObjects;

	/** Creates a pool with an initial capacity of 16 and no maximum. */
	public Pool() {
		this(16, Integer.MAX_VALUE);
	}

	/** Creates a pool with the specified initial capacity and no maximum. */
	public Pool(int initialCapacity) {
		this(initialCapacity, Integer.MAX_VALUE);
	}

	/** @param max The maximum number of free objects to store in this pool. */
	public Pool(int initialCapacity, int max) {
		freeObjects = new Stack<>();
		freeObjects.setSize(initialCapacity);
		freeObjects.clear();
		this.max = max;
	}

	abstract protected T newObject();

	/**
	 * Returns an object from this pool. The object may be new (from
	 * {@link #newObject()}) or reused (previously {@link #free(Object) freed}).
	 */
	public T obtain() {
		return freeObjects.size() == 0 ? newObject() : freeObjects.pop();
	}

	/**
	 * Puts the specified object in the pool, making it eligible to be returned by
	 * {@link #obtain()}. If the pool already contains {@link #max} free objects,
	 * the specified object is reset but not added to the pool.
	 */
	public void free(T object) {
		Preconditions.checkArgument(object != null, "object cannot be null.");
		if (freeObjects.size() < max) {
			freeObjects.push(object);
			peak = Math.max(peak, freeObjects.size());
		}
		object.reset();
	}

	/**
	 * Puts the specified objects in the pool. Null objects within the array are
	 * silently ignored.
	 * 
	 * @see #free(Object)
	 */
	public void freeAll(Stack<T> objects) {
		Preconditions.checkArgument(objects != null, "object cannot be null.");
		Stack<T> freeObjects = this.freeObjects;
		int max = this.max;
		for (int i = 0; i < objects.size(); i++) {
			T object = objects.get(i);
			if (object == null)
				continue;
			if (freeObjects.size() < max)
				freeObjects.push(object);
			object.reset();
		}
		peak = Math.max(peak, freeObjects.size());
	}

	/** Removes all free objects from this pool. */
	public void clear() {
		freeObjects.clear();
	}

	/** The number of objects available to be obtained. */
	public int getFree() {
		return freeObjects.size();
	}
}
