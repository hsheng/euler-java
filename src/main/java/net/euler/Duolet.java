package net.euler;

import net.euler.util.JsonUtil;

public class Duolet<L, R> {
	public final L v1;
	public final R v2;
	
	public Duolet(L u, R v) {
		this.v1 = u;
		this.v2 = v;
	}
	
	@Override
	public String toString() {
		return JsonUtil.toString(this);
	}
}
