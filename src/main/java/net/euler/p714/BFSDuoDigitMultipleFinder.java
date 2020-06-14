package net.euler.p714;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

public class BFSDuoDigitMultipleFinder implements IDuoDigitMultipleFinder {

	private final int limit;
	
	public BFSDuoDigitMultipleFinder(int limit) {
		this.limit = limit;
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
	
	private void addToQueue(List<Triplet> queue, Triplet t, String d1s, String d2s) {
		queue.add(Triplet.of(d1s + t.getNum(), t.getLength() + 1, d1s.charAt(0) == '0'));
		queue.add(Triplet.of(d2s + t.getNum(), t.getLength() + 1, d2s.charAt(0) == '0'));
	}
	
	private Quintet bfsFind(final int num, final int d1, final int d2, final int limit) {
		long t1 = System.currentTimeMillis();
		
		BitSet flags = new BitSet(num + 1);

		String d1s = String.valueOf(d1);
		String d2s = String.valueOf(d2);
		final BigInteger base = BigInteger.valueOf(num);
		
		List<Triplet> queue = new ArrayList<>();
		queue.add(Triplet.of(d1s, d1s.length(), d1s.charAt(0) == '0'));
		
		d1s = String.valueOf(Math.min(d1, d2));
		d2s = String.valueOf(Math.max(d1, d2));
		
		while (!queue.isEmpty()) {
			Triplet t = queue.remove(0);
			if (t.getLength() > limit) {
				continue;
			}
			if (t.isZeroStart()) {
				addToQueue(queue, t, d1s, d2s);
				continue;
			}
			
			if (t.getLength() <= LONG_MAX_LEN) {
				long n = NumberUtils.toLong(t.getNum());
				if (n <= num) {
					addToQueue(queue, t, d1s, d2s);
				} else {
					int r =  (int)(n % num);
					if (r == 0) {
						long t2 = System.currentTimeMillis();
						return Quintet.of(num, true, BigInteger.valueOf(n), -1, (t2 - t1));
					} else if (!flags.get(r)) {
						flags.set(r);
						addToQueue(queue, t, d1s, d2s);
					}
				}
			} else {
				BigInteger n = new BigInteger(t.getNum());
				BigInteger[] dnr = n.divideAndRemainder(base);
				int r = dnr[1].intValue();
				if (r == 0) {
					long t2 = System.currentTimeMillis();
					return Quintet.of(num, true, n, -1, (t2 - t1));
				} else if (!flags.get(r)) {
					addToQueue(queue, t, d1s, d2s);
				}
			}
		}

		long t2 = System.currentTimeMillis();
		return Quintet.of(num, false, BigInteger.ZERO, -1, (t2 - t1));
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
				Quintet q = bfsFind(num, d1, d2, limit);
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
