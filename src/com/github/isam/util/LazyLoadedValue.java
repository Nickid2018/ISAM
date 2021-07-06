package com.github.isam.util;

import java.util.function.*;

public class LazyLoadedValue<T> {

	private Supplier<T> factory;

	private T value;

	public LazyLoadedValue(Supplier<T> supplier) {
		factory = supplier;
	}

	public T get() {
		Supplier<T> supplier = factory;
		if (supplier != null) {
			value = supplier.get();
			factory = null;
		}
		return value;
	}
}