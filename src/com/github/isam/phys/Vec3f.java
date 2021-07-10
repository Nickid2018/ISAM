package com.github.isam.phys;

public class Vec3f {

	public static final Vec3f ZERO = new Vec3f(0, 0, 0);
	public static final Vec3f POSITIVE_X = new Vec3f(1, 0, 0);
	public static final Vec3f NEGATIVE_X = new Vec3f(-1, 0, 0);
	public static final Vec3f POSITIVE_Y = new Vec3f(0, 1, 0);
	public static final Vec3f NEGATIVE_Y = new Vec3f(0, -1, 0);
	public static final Vec3f POSITIVE_Z = new Vec3f(0, 0, 1);
	public static final Vec3f NEGATIVE_Z = new Vec3f(0, 0, -1);

	public float x;
	public float y;
	public float z;

	public Vec3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vec3f copy() {
		return new Vec3f(x, y, z);
	}

	public Vec3f move(float xa, float ya, float za) {
		x += xa;
		y += ya;
		z += za;
		return this;
	}

	public Vec3f add(Vec3f vec) {
		return move(vec.x, vec.y, vec.z);
	}

	public Vec3f normalize() {
		float normal = getNormal();
		x /= normal;
		y /= normal;
		z /= normal;
		return this;
	}

	public float getNormal() {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}
}
