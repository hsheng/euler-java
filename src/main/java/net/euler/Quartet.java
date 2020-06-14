package net.euler;

import net.euler.util.JsonUtil;

public class Quartet<A, B, C, D> {

	public final A v1;
	public final B v2;
	public final C v3;
	public final D v4;
	
	public Quartet(A a, B b, C c, D d) {
		this.v1 = a;
		this.v2 = b;
		this.v3 = c;
		this.v4 = d;
	}
	
	@Override
	public String toString() {
		return JsonUtil.toString(this);
	}
}
