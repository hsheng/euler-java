package net.euler.p714;

import net.euler.util.JsonUtil;

public class Triplet extends net.euler.Triplet<String, Integer, Boolean> {

	protected Triplet(String num, int length, boolean zeroStart) {
		super(num, length, zeroStart);
	}
	
	public String getNum() {
		return v1;
	}

	public int getLength() {
		return v2;
	}

	public boolean isZeroStart() {
		return v3;
	}

	@Override
	public String toString() {
		return JsonUtil.toString(this);
	}
	
	public static Triplet of(String num, int length, boolean zeroStart) {
		return new Triplet(num, length, zeroStart);
	}
}
