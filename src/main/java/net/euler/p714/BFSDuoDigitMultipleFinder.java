package net.euler.p714;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BFSDuoDigitMultipleFinder implements IDuoDigitMultipleFinder {

	private final int maxDuoLen;
	
	public BFSDuoDigitMultipleFinder(int limit) {
		this.maxDuoLen = limit;
	}
	
	private List<Integer> getD1Options(final int base) {
		switch (base) {
		case 0:
			return Arrays.asList(0);
		case 2:
		case 4:
		case 6:
		case 8:
			return Arrays.asList(0, 2, 4, 6, 8);
		case 1:
		case 3:
		case 7:
		case 9:
			return Arrays.asList( 1, 2, 3, 4, 5, 6, 7, 8, 9 );
		case 5:
			return Arrays.asList( 0, 5 );
		}
		throw new RuntimeException("Invalid argument: " + base);
	}
	
	private List<Integer> getD2Options(final int d1) {
		if (d1 == 0) {
			return Arrays.asList( 1, 2, 3, 4, 5, 6, 7, 8, 9 );
		} else {
			return Arrays.asList( 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 );
		}
	}
	
	private Quintet bfsFind(final int num, final int p1, final int p2) {
		long t1 = System.currentTimeMillis();
		
		Map<Integer, BigInteger> rMap = new HashMap<>();
		
		final BigInteger base = BigInteger.valueOf(num);
		
		List<String> d1Queue = new ArrayList<>();
		List<String> d2Queue = new ArrayList<>();
		List<String> masterQueue = new ArrayList<>();
		masterQueue.add(String.valueOf(p1));
		int duoLen = 1;
		
		final String d1s = String.valueOf(Math.min(p1, p2));
		final String d2s = String.valueOf(Math.max(p1, p2));
				
		
		
		while (!masterQueue.isEmpty()) {
			String duoStr = masterQueue.remove(0);
			BigInteger duo = new BigInteger(duoStr);
			
			if (base.compareTo(duo) >= 0 && duo.intValue() == 0) {
				d1Queue.add(d1s + duoStr);
				d2Queue.add(d2s + duoStr);
			} else {
				BigInteger[] dnr = duo.divideAndRemainder(base);
				int r = dnr[1].intValue();
				if (r == 0) {
					// return
					return Quintet.of(num, true, duo, dnr[0].longValue(), (System.currentTimeMillis() - t1));
				} else if (!rMap.containsKey(r)) {
					// add for next round
					d1Queue.add(d1s + duoStr);
					d2Queue.add(d2s + duoStr);
					rMap.put(r, duo);
				}
			}
			
			if (masterQueue.isEmpty() && duoLen < maxDuoLen) {
				duoLen += 1;
				masterQueue.addAll(d1Queue);
				masterQueue.addAll(d2Queue);
				d1Queue.clear();
				d2Queue.clear();
				rMap.clear();
			}
		}

		return Quintet.of(num, false, BigInteger.ZERO, num, (System.currentTimeMillis() - t1));
	}
	
	@Override
	public Quintet findDuoDigitMultiple(int num) {
		if (isDuoDigitNum(num)) {
			return Quintet.of(num, true, BigInteger.valueOf(num), 1L, 0L);
		}

		long t1 = System.currentTimeMillis();
		
		List<BigInteger> candidates = new ArrayList<BigInteger>();
		for (int d1 : getD1Options(num % 10)) {
			for (int d2 : getD2Options(d1)) {
				Quintet q = bfsFind(num, d1, d2);
				if (q.isSolved()) {
					candidates.add(q.getDuo());
				}
			}
		}
		BigInteger result = null;
		if (!candidates.isEmpty()) {
			result = Collections.min(candidates, Comparator.naturalOrder());
		}
		
		long t2 = System.currentTimeMillis();
		return Quintet.of(num, (result != null), result, 0, (t2 - t1));
	}

}
