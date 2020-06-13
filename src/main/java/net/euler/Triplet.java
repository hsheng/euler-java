package net.euler;

import net.euler.util.JsonUtil;

public class Triplet<X, Y, Z> {
	public final X v1;
	public final Y v2;
	public final Z v3;
	
	public Triplet(X x, Y y, Z z) {
		this.v1 = x;
		this.v2 = y;
		this.v3 = z;
	}
	
	@Override
	public String toString() {
		return JsonUtil.toString(this);
	}
}
