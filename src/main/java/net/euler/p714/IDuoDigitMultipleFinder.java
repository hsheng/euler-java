package net.euler.p714;

import java.util.HashSet;
import java.util.Set;

public interface IDuoDigitMultipleFinder {

	int LONG_MAX_LEN = 19;
	
	default boolean isDuoDigitNum(Number n) {
		Set<Integer> chrSet = new HashSet<>();
		String str = String.valueOf(n);
		for (int i = 0; i < str.length(); i++) {
			chrSet.add((int)str.charAt(i));
		}
		return chrSet.size() <= 2;
	}
	
	Quintet findDuoDigitMultiple(int n);
	
}
