package net.euler.p714;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Helper {

	private static Helper instance = new Helper();
	
	public static Helper instance() {
		return instance;
	}
	
	private Helper() {
		
	}
	
	/**
	 * @return [ [duo-num], [non-duo-num] ]
	 */
	public List<Quintet> separate(int n) {
		List<Quintet> nums = new ArrayList<>();
		for (int i = n; i > 0; i--) {
			if (isDuoDigitNumber(i)) {
				nums.add(0, Quintet.of(i, true, true, BigInteger.valueOf(i), 1L));
			} else {
				Triplet triplet = getDuoNumAndMultiple(i, n);
				if (triplet == null) {
					nums.add(0, Quintet.of(i, false, false, null, null));
				} else {
					nums.add(0, Quintet.of(i, false, true, triplet.getDuoDigitMultiple(), triplet.getMultiple()));
				}
			}
		}
		return nums;
	}
	
	public boolean isDuoDigitNumber(Number n) {
		Set<Integer> set = n.toString().chars().boxed().collect(Collectors.toSet());
		return set.size() <= 2;
	}
	
	public Quartet getDuoNumAndMultiple(final int nonDuoNum) {
		long st = System.currentTimeMillis();
		BigInteger step = BigInteger.valueOf(nonDuoNum);
		BigInteger duoNum = BigInteger.valueOf(nonDuoNum << 1);
		int multiple = 2;
		while (!isDuoDigitNumber(duoNum)) {
			duoNum = duoNum.add(step);
			multiple++;
		}
		long et = System.currentTimeMillis();
		return Quartet.of(nonDuoNum, duoNum, multiple, (et - st));
	}

	public Triplet getDuoNumAndMultiple(final int nonDuoNum, final int duoNumBoundary) {
		long duoNum = nonDuoNum << 1;
		long multiple = 2;
		while (duoNum < duoNumBoundary) {
			if (isDuoDigitNumber(duoNum)) {
				return Triplet.of(nonDuoNum, BigInteger.valueOf(duoNum), multiple);
			} else {
				duoNum += nonDuoNum;
				multiple++;
			}
		}
		return null;
	}

}
