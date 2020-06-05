package net.euler.p714;

import java.math.BigInteger;
import java.util.Objects;

import net.euler.util.JsonUtil;

public class Quintet {
	/** num */
	private final int num;
	
	/** is duo-digit num ? */
	private boolean isDuoDigitNum;
	
	/** if <code>value1</code> is not duo-digit num, has its duo-digit multiple num been solved? */
	private boolean isResolved;
	
	/** duo-digit-multiple num of <code>value1</code> */
	private BigInteger duoDigitMultiple;
	
	/** multiple */
	private Long multiple;

	public Quintet(int value1, boolean value2, boolean value3, BigInteger value4, Long value5) {
		this.num = value1;
		this.isDuoDigitNum = value2;
		this.isResolved = value3;
		this.duoDigitMultiple = value4;
		this.multiple = value5;
	}
	
	public int getNum() {
		return num;
	}

	public boolean isDuoDigitNum() {
		return isDuoDigitNum;
	}

	public void setDuoDigitNum(boolean value2) {
		this.isDuoDigitNum = value2;
	}

	public boolean isResolved() {
		return isResolved;
	}

	public void setResolved(boolean value3) {
		this.isResolved = value3;
	}

	public BigInteger getDuoDigitMultiple() {
		return duoDigitMultiple;
	}

	public void setDuoDigitMultiple(BigInteger value4) {
		this.duoDigitMultiple = value4;
	}

	public Long getMultiple() {
		return multiple;
	}

	public void setMultiple(Long value5) {
		this.multiple = value5;
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
		
		Quintet that = (Quintet)obj;
		return Objects.equals(this.num, that.num) && Objects.equals(this.duoDigitMultiple, that.duoDigitMultiple);
	}

	public static Quintet of(int value1, boolean value2, boolean value3, BigInteger value4, Long value5) {
		return new Quintet(value1, value2, value3, value4, value5);
	}
}
