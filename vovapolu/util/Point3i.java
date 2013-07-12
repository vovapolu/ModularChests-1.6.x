package vovapolu.util;

public class Point3i {
	
	private int x, y, z;
	
	public Point3i(int aX, int aY, int aZ) { 
		x = aX;
		y = aY;
		z = aZ;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getZ() {
		return z;
	}
	
	@Override
	public int hashCode() {
		return (x *  73856093 ^ y * 19349663 ^ z * 83492791);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Point3i)
		{
			Point3i point = (Point3i)obj;
			return point.x == x && point.y == y && point.z == z;
		}
		else 
			return false;
	}
}
