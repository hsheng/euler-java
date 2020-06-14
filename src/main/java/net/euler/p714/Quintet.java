package net.euler.p714;

import java.math.BigInteger;

import net.euler.util.JsonUtil;

public class Quintet extends net.euler.Quintet<Integer, Boolean, BigInteger, Long, Long> {
	
	protected Quintet(int num, boolean solved, BigInteger duo, long multiple, long time) {
		super(num, solved, duo, multiple, time);
	}
	
	public int getNum() {
		return v1;
	}

	public boolean isSolved() {
		return v2;
	}

	public BigInteger getDuo() {
		return v3;
	}

	public Long getMultiple() {
		return v4;
	}

	public Long getTime() {
		return v5;
	}
	
	@Override
	public String toString() {
		return JsonUtil.toString(this);
	}
	

	public static Quintet of(int num, boolean solved, BigInteger duo, long multiple, long time) {
		return new Quintet(num, solved, duo, multiple, time);
	}
}
