/*
 * Copyright 2021 ISAM
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.github.isam.util.pool;

import java.lang.reflect.Constructor;

public class ReflectionPool<T extends Poolable> extends Pool<T> {

    private final Constructor<T> constructor;

    public ReflectionPool(Class<T> type) {
        this(type, 16, Integer.MAX_VALUE);
    }

    public ReflectionPool(Class<T> type, int initialCapacity) {
        this(type, initialCapacity, Integer.MAX_VALUE);
    }

    public ReflectionPool(Class<T> type, int initialCapacity, int max) {
        super(initialCapacity, max);
        constructor = findConstructor(type);
        if (constructor == null)
            throw new RuntimeException("Class cannot be created (missing no-arg constructor): " + type.getName());
    }

    private Constructor<T> findConstructor(Class<T> type) {
        try {
            return type.getConstructor();
        } catch (Exception ex1) {
            try {
                Constructor<T> constructor = type.getDeclaredConstructor();
                constructor.setAccessible(true);
                return constructor;
            } catch (Exception ex2) {
                return null;
            }
        }
    }

    protected T newObject() {
        try {
            return constructor.newInstance((Object[]) null);
        } catch (Exception ex) {
            throw new RuntimeException("Unable to create new instance: " + constructor.getDeclaringClass().getName(),
                    ex);
        }
    }
}
