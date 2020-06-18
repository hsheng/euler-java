package net.euler.p714;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import net.euler.Triplet;

public class Solution714 {

//	static String SINGLE_FORMAT = "\t%s %5d %30s %6.2f\n";
//	static String BLOCK_FORMAT = "%s [%5d, %5d] current=%5d, result=%.12e, time=%6.2f | result=%30s\n";
	
//	private final int blockPrintSize;
//	private final int maxDigits;
//	private final long timeout;
	
//	public Solution714(int maxDigits, long timeout, int blockPrintSize) {
//		this.blockPrintSize = blockPrintSize;
//		this.maxDigits = maxDigits;
//		this.timeout = timeout;
//	}
	
	public Map<Integer, Quintet> bruFindRange(final int start, final int end, final long timeout) {
		BruDuoDigitMultipleFinder finder = new BruDuoDigitMultipleFinder(timeout);
		Map<Integer, Quintet> result = new HashMap<Integer, Quintet>();
		for (int num = start; num <= end; num++) {
			result.put(num, finder.findDuoDigitMultiple(num));
		}
		return result;
	}
	
	public Map<Integer, Quintet> bfsFindRange(final int start, final int end, final int maxDuoLen) {
		IDuoDigitMultipleFinder finder = new BFSDuoDigitMultipleFinder(maxDuoLen);
		Map<Integer, Quintet> result = new HashMap<Integer, Quintet>();
		for (int num = start; num <= end; num++) {
			result.put(num, finder.findDuoDigitMultiple(num));
		}
		return result;
	}
	
	public Map<Integer, Quintet> mixFindRange(final int start, final int end, final long timeout, final int maxDuoLen) {
		IDuoDigitMultipleFinder finder = new MixDuoDigitMultipleFinder(timeout, maxDuoLen);
		Map<Integer, Quintet> result = new HashMap<Integer, Quintet>();
		for (int num = start; num <= end; num++) {
			result.put(num, finder.findDuoDigitMultiple(num));
		}
		return result;
	}
	
	public Triplet<Boolean, BigInteger, Long> bruSumRange(final int start, final int end) {
		long t1 = System.currentTimeMillis();
		boolean solved = true;
		BigInteger result = BigInteger.valueOf(0);
		for (Quintet q : bruFindRange(start, end, 0).values()) {
			solved &= q.isSolved();
			result = result.add(q.getDuo());
		}
		return Triplet.of(solved, result, (System.currentTimeMillis() - t1));
	}
	
	public Triplet<Boolean, BigInteger, Long> bfsSumRange(final int start, final int end, final int maxDuoLen) {
		long t1 = System.currentTimeMillis();
		boolean solved = true;
		BigInteger result = BigInteger.valueOf(0);
		for (Quintet q : bfsFindRange(start, end, maxDuoLen).values()) {
			solved &= q.isSolved();
			result = result.add(q.getDuo());
		}
		return Triplet.of(solved, result, (System.currentTimeMillis() - t1));
	}
	
	public Triplet<Boolean, BigInteger, Long> mixSumRange(final int start, final int end, final long timeout, final int maxDuoLen) {
		long t1 = System.currentTimeMillis();
		boolean solved = true;
		BigInteger result = BigInteger.valueOf(0);
		for (Quintet q : mixFindRange(start, end, timeout, maxDuoLen).values()) {
			solved &= q.isSolved();
			result = result.add(q.getDuo());
		}
		return Triplet.of(solved, result, (System.currentTimeMillis() - t1));
	}
	
	public void compareFindRange(final int start, final int end, final long timeout, final int maxDuoLen) {
		System.out.printf("compareFindRange: [%d, %d] timeout=%d, maxDuoOen=%d at %s\n", start, end, timeout, maxDuoLen, Calendar.getInstance().getTime());
		
		Map<Integer, Quintet> bfsResult = bfsFindRange(start, end, maxDuoLen);
		System.out.println("done bfsFindRange at " + Calendar.getInstance().getTime());
		
		Map<Integer, Quintet> bruResult = bruFindRange(start, end, 0);
		System.out.println("done bruFindRange at " + Calendar.getInstance().getTime());
		
		for (int i = start; i <= end; i++) {
			Quintet bfsQuintet = bfsResult.get(i);
			Quintet bruQuintet = bruResult.get(i);
			if (!Objects.equals(bfsQuintet.getDuo(), bruQuintet.getDuo())) {
				System.err.printf("bru - %5d: %25s\n", i, bruQuintet.getDuo(), bruQuintet.getTime() / 1000.0);
				System.err.printf("bfs - %5d: %25s\n", i, bfsQuintet.getDuo(), bfsQuintet.getTime() / 1000.0);
			}
		}
	}
	
	public void resolve(final int boundary) {
		System.out.println("resolve() started at " + Calendar.getInstance().getTime());

		long t1 = System.currentTimeMillis();
		final long timeout = 2000;
		final int maxDuoLen = 30;
		final int chunkSize = 1000;
		final int chunks = (int)Math.ceil(1.0 * boundary / chunkSize);

		BigInteger result = BigInteger.valueOf(0);
		boolean solved = true;
		
		for (int chunk = 1; chunk <= chunks; chunk++) {
			final int start = (chunk - 1) * chunkSize + 1;
			final int end = chunk * chunkSize;
			Triplet<Boolean, BigInteger, Long> sum = mixSumRange(start, end, timeout, maxDuoLen);
			print("mix", start, end, sum);
			solved = solved && sum.v1;
			result = result.add(sum.v2);
		}
		
		System.out.println("resolve() finished at " + Calendar.getInstance().getTime());
		System.out.printf("*** solved=%s, result=%.12e [%s], time=%.2fs\n", solved, result.doubleValue(), result, (System.currentTimeMillis() - t1)/1000.0);
	}
	
	private static void print(String m, final int start, final int end, Triplet<Boolean, BigInteger, Long> sum) {
		System.out.printf("%3s [%5d,%5d] %5s %.12e %10.2f | %25s\n", 
				m, start, end, sum.v1, sum.v2.doubleValue(), sum.v3/1000.0, sum.v2);
	}
	
	public static void main(String[] args) {
		
		Solution714 s = new Solution714();

		s.resolve(50000);
		
//		int start =  24001;
//		int end   =  25000;
//		long timeout = 2000;
//		int maxDuoLen = 30;
//		
//		Triplet<Boolean, BigInteger, Long> mixSum = s.mixSumRange(start, end, timeout, maxDuoLen);
//		print("mix", start, end, mixSum);
//		
//		Triplet<Boolean, BigInteger, Long> bruSum = s.bruSumRange(start, end);
//		print("bru", start, end, bruSum);
		
	}
}
