/*
 * Copyright 2021 ISAM
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
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
 * 
 */
package com.github.isam.phys;

import com.google.common.base.*;

/**
 * 
 * @author Nickid2018
 */
public class AABB {

	public double minX;
	public double minY;
	public double maxX;
	public double maxY;

	public static final AABB AABB_NULL = newAABB(0, 0, 0, 0);

	public static AABB newAABB(double minX, double minY, double maxX, double maxY) {
		return newAABB(minX, minY, maxX, maxY, false);
	}

	public static AABB newAABB(double minX, double minY, double maxX, double maxY, boolean checkValid) {
		AABB aabb = new AABB();
		aabb.minX = minX;
		aabb.minY = minY;
		aabb.maxX = maxX;
		aabb.maxY = maxY;
		if (checkValid)
			Preconditions.checkArgument(!aabb.isValid(), "Invalid AABB");
		return aabb.validate();
	}

	public boolean isValid() {
		return minX <= maxX && minY <= maxY;
	}

	public AABB validate() {
		if (minX > maxX) {
			double tmp = minX;
			minX = maxX;
			maxX = tmp;
		}
		if (minY > maxY) {
			double tmp = minY;
			minY = maxY;
			maxY = tmp;
		}
		return this;
	}

	public AABB move(double x, double y) {
		minX += x;
		minY += y;
		maxX += x;
		maxY += y;
		return validate();
	}

	public AABB inflate(double x, double y) {
		validate();
		minX -= x;
		minY -= y;
		maxX += x;
		maxY += y;
		return validate();
	}

	public AABB inflate(double d) {
		return inflate(d, d);
	}

	public AABB deflate(double d) {
		return inflate(-d);
	}

	public boolean intersects(AABB other) {
		validate();
		other.validate();
		return minX < other.maxX && maxX > other.minX && minY < other.maxY && maxY > other.minY;
	}

	public AABB intersect(AABB other) {
		if (!intersects(other))
			return AABB_NULL.newCopy();
		AABB aabb = new AABB();
		aabb.minX = Math.max(minX, other.minX);
		aabb.minY = Math.max(minY, other.minY);
		aabb.maxX = Math.min(maxX, other.maxX);
		aabb.maxY = Math.min(maxY, other.maxY);
		return aabb;
	}

	public AABB boundWith(AABB other) {
		validate();
		other.validate();
		AABB aabb = new AABB();
		aabb.minX = Math.min(minX, other.minX);
		aabb.minY = Math.min(minY, other.minY);
		aabb.maxX = Math.max(maxX, other.maxX);
		aabb.maxY = Math.max(maxY, other.maxY);
		return aabb;
	}

	public boolean contains(double x, double y) {
		return minX <= x && x <= maxX && minY <= y && y <= maxY;
	}

	public double getWidth() {
		validate();
		return maxX - minX;
	}

	public double getHeight() {
		validate();
		return maxY - minY;
	}

	public AABB newCopy() {
		validate();
		AABB aabb = new AABB();
		aabb.minX = minX;
		aabb.minY = minY;
		aabb.maxX = maxX;
		aabb.maxY = maxY;
		return aabb;
	}

	public boolean equals(AABB other) {
		return minX == other.minX && minY == other.minY && maxX == other.maxX && maxY == other.maxY;
	}

	public boolean equals(Object other) {
		return other instanceof AABB && equals((AABB) other);
	}
}
