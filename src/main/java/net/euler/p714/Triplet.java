package net.euler.p714;

import java.math.BigInteger;
import java.util.Objects;

import net.euler.util.JsonUtil;

public class Triplet {

	/** non-duodigit num */
	private final int num;
	
	/** duodigit-multiple num of <code>value1</code> */
	private final BigInteger duoDigitMultiple;
	
	/** multiple */
	private final long multiple;

	public Triplet(int value1, BigInteger value2, long value3) {
		this.num = value1;
		this.duoDigitMultiple = value2;
		this.multiple = value3;
	}
	
	public int getNum() {
		return num;
	}

	public BigInteger getDuoDigitMultiple() {
		return duoDigitMultiple;
	}

	public long getMultiple() {
		return multiple;
	}

	@Override
	public String toString() {
		return JsonUtil.toString(this);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((duoDigitMultiple == null) ? 0 : duoDigitMultiple.hashCode());
		result = prime * result + num;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		Triplet that = (Triplet)obj;
		return Objects.equals(this.num, that.num) && Objects.equals(this.duoDigitMultiple, that.duoDigitMultiple);
	}

	public static Triplet of(int value1, BigInteger value2, long value3) {
		return new Triplet(value1, value2, value3);
	}
}
