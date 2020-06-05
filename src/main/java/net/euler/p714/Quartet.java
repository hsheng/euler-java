package net.euler.p714;

import java.math.BigInteger;
import java.util.Objects;

import net.euler.util.JsonUtil;

public class Quartet {

	/** non-duodigit num */
	private final int num;
	
	/** duodigit-multiple num of <code>value1</code> */
	private final BigInteger duoDigitMultiple;
	
	/** multiple */
	private final long multiple;

	private final long time;
	
	public Quartet(int num, BigInteger duoDigitMultiple, long multiple, long time) {
		this.num = num;
		this.duoDigitMultiple = duoDigitMultiple;
		this.multiple = multiple;
		this.time = time;
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

	public long getTime() {
		return time;
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
		Quartet that = (Quartet)obj;
		return Objects.equals(this.num, that.num) && Objects.equals(this.duoDigitMultiple, that.duoDigitMultiple);
	}

	public static Quartet of(int num, BigInteger duoDigitMultiple, long multiple, long time) {
		return new Quartet(num, duoDigitMultiple, multiple, time);
	}

}
