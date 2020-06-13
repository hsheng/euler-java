package net.euler.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NumberUtils extends org.apache.commons.lang3.math.NumberUtils {

	public static List<Long> factors(long num) {
		List<Long> result = new ArrayList<Long>();
		List<Long> remainders = new ArrayList<Long>();
		
		long top = (long)Math.ceil(Math.sqrt(num));
		long step = ((num & 0x1) == 0 ? 1 : 2);
		long i = ((step == 2 ? 3 : 2));
		for (; i <= top; i += step) {
			if ((num % i) == 0) {
				result.add(i);
				remainders.add(0, num / i);
			}
		}
		result.addAll(remainders);
		result.add(0, 1L);
		result.add(num);
		return result;
		
	}

	public static void main(String[] args) {
		List<Long> inputs = Arrays.asList(2188867L, 438251L, 11033873L, 110777667L);
		for (long input : inputs) {
			System.out.printf("%15d, %s\n", input, JsonUtil.toString(NumberUtils.factors(input)));
		}
	}
}
