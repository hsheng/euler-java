package net.euler.p714;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.math.NumberUtils;

public class MixSolution {

	final static int LONG_MAX_LEN = 19;
	final static long TIMEOUT = 2000;		// mills
	final static int MAX_DIGITS = 30;
	
	private boolean isDuoNum(final Number num) {
		Set<Integer> chrSet = new HashSet<>();
		String str = String.valueOf(num);
		for (int i = 0; i < str.length(); i++) {
			chrSet.add((int)str.charAt(i));
		}
		return chrSet.size() <= 2;
	}
	
	public Quintet brutalFindSingle(final int num) {
		if (isDuoNum(num)) {
			return Quintet.of(num, true, BigInteger.valueOf(num), 1L, 0L);
		}
		
		long t1 = System.currentTimeMillis();
		
		final BigInteger base = BigInteger.valueOf(num);
		long multiple = 1;
		BigInteger duo = BigInteger.valueOf(num);
		while (!isDuoNum(duo)) {
			duo = duo.add(base);
			multiple += 1;
		}

		long t2 = System.currentTimeMillis();
		return Quintet.of(num, true, duo, multiple, (t2 - t1));
	}
	
	public Quintet brutalFindSingleLimited(final int num, final long timeout) {
		if (isDuoNum(num)) {
			return Quintet.of(num, true, BigInteger.valueOf(num), 1L, 0L);
		}
		
		long t1 = System.currentTimeMillis();
		
		final BigInteger base = BigInteger.valueOf(num);
		long multiple = 1;
		BigInteger duo = BigInteger.valueOf(num);
		while (!isDuoNum(duo)) {
			if ((multiple & 0x3FF) == 0) {
				long t2 = System.currentTimeMillis();
				if (t2 - t1 > timeout) {
					return Quintet.of(num, false, duo, multiple, (t2 - t1));
				}
			}
			duo = duo.add(base);
			multiple += 1;
		}

		long t2 = System.currentTimeMillis();
		return Quintet.of(num, true, duo, multiple, (t2 - t1));
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
	
	public Quintet bfsFind(final int num, final int d1, final int d2, final int limit) {
		long t1 = System.currentTimeMillis();
		
		BitSet flags = new BitSet(num + 1);
		final String d1s = String.valueOf(d1);
		final String d2s = String.valueOf(d2);
		final BigInteger base = BigInteger.valueOf(num);
		
		List<Triplet> queue = new ArrayList<>();
		queue.add(Triplet.of(d1s, d1s.length(), d1s.charAt(0) == '0'));
		
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
	
	public Quintet bfsFind(final int num, final int limit) {
		if (isDuoNum(num)) {
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
	
	
	public Quintet mixFindSingle(final int num) {
		long t1 = System.currentTimeMillis();

		Quintet q = brutalFindSingleLimited(num, TIMEOUT);
		if (!q.isSolved()) {
			q = bfsFind(num, MAX_DIGITS);
		}

		long t2 = System.currentTimeMillis();
		return Quintet.of(q.getNum(), q.isSolved(), q.getDuo(), q.getMultiple().longValue(), (t2 - t1));
	}
	
	public Map<Integer, Quintet> mixFindRange(final int start, final int end) {
		Map<Integer, Quintet> m = new LinkedHashMap<>();
		for (int i = start; i <= end; i++) {
			Quintet q = mixFindSingle(i);
			m.put(i, q);
			singlePrint(q, 10000);
		}
		return m;
	}
	
	public Map<Integer, Quintet> brutalFindRange(final int start, final int end) {
		Map<Integer, Quintet> m = new LinkedHashMap<>();
		for (int i = start; i <= end; i++) {
			Quintet q = brutalFindSingle(i);
			m.put(i, q);
			singlePrint(q, 10000);
		}
		return m;
	}

	public Map<Integer, Quintet> bfsFindRange(final int start, final int end) {
		Map<Integer, Quintet> m = new LinkedHashMap<>();
		for (int i = start; i <= end; i++) {
			Quintet q = bfsFind(i, MAX_DIGITS);
			m.put(i, q);
			singlePrint(q, 10000);
		}
		return m;
	}

	public BigInteger mixFindRangeSummary(final int start, final int end) {
		BigInteger result = BigInteger.valueOf(0);

		long t1 = System.currentTimeMillis();
		
		for (int i = start; i <= end; i++) {
			Quintet q = mixFindSingle(i);
			singlePrint(q, 5000L);
			
			if (!q.isSolved()) {
				System.err.printf("*** %d\n", i);
			} else {
				result = result.add(q.getDuo());
			}
			
			blockPrint(t1, start, end, i, result);
		}
		
		long t2 = System.currentTimeMillis();
		System.out.printf(FORMAT, start, end, end, result.doubleValue(), (t2 - t1) / 1000.0, result);

		return result;
	}

	public BigInteger brutalFindRangeSummary(final int start, final int end) {
		BigInteger result = BigInteger.valueOf(0);

		long t1 = System.currentTimeMillis();
		
		for (int i = start; i <= end; i++) {
			Quintet q = brutalFindSingle(i);
			singlePrint(q, 5000L);
			
			if (!q.isSolved()) {
				System.err.printf("*** %d\n", i);
			} else {
				result = result.add(q.getDuo());
			}
			
			blockPrint(t1, start, end, i, result);
		}
		
		long t2 = System.currentTimeMillis();
		System.out.printf(FORMAT, start, end, end, result.doubleValue(), (t2 - t1) / 1000.0, result);

		return result;
	}

	static final String FORMAT="[%5d, %5d] current=%5d, result=%.12e, time=%6.2f | result=%30s\n";

	static void singlePrint(final Quintet q, final long timeThreshold) {
		if (q.getTime() > timeThreshold) {
			System.out.printf("\t%5d %30s %6.2f\n", q.getNum(), q.getDuo(), q.getTime() / 1000.0);
		}
	}
	
	static void blockPrint(final long ts1, final int S, final int E, final int n, final BigInteger result) {
		if ((n & 0x3FF) == 0) {
			long ts2 = System.currentTimeMillis();
			System.out.printf(FORMAT, S, E,n, result.doubleValue(), (ts2 - ts1) / 1000.0, result);
		}
	}
	
	public static void main(String[] args) {
		MixSolution s = new MixSolution();
		
		final int start = 8193;
		final int end = 9216;
		
		System.out.println("--- mix ---");
		Map<Integer, Quintet> mixRange = s.mixFindRange(start, end);
		System.out.println("--- brutal ---");
		Map<Integer, Quintet> brutalRange = s.brutalFindRange(start, end);

		BigInteger mResult = BigInteger.valueOf(0);
		BigInteger bResult = BigInteger.valueOf(0);
		
		for (int i = start; i <= end; i++) {
			Quintet mq = mixRange.get(i);
			Quintet bq = brutalRange.get(i);
			if (!Objects.equals(mq.getDuo(), bq.getDuo())) {
				System.err.printf("*** %5d, bru=%30s|%s, mix=%30s|%s\n", i, bq.getDuo(), bq.isSolved(), mq.getDuo(), mq.isSolved());
			}
			
			if (bq.getTime() > 10000) {
				System.out.printf("--- %5d: %6.2fs, %6.2fs\n", i, bq.getTime()/1000.0, mq.getTime()/1000.0);
			}
			mResult = mResult.add(mq.getDuo());
			bResult = bResult.add(bq.getDuo());
		}
		
		System.out.printf("bru=%30s\n", bResult);
		System.out.printf("mix=%30s\n", mResult);
	}
}
