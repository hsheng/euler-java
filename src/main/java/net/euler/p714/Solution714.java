package net.euler.p714;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class Solution714 {

	static String SINGLE_FORMAT = "\t%s %5d %30s %6.2f\n";
	static String BLOCK_FORMAT = "%s [%5d, %5d] current=%5d, result=%.12e, time=%6.2f | result=%30s\n";
	
	private final int blockPrintSize;
	private final int maxDigits;
	private final long timeout;
	
	public Solution714(int maxDigits, long timeout, int blockPrintSize) {
		this.blockPrintSize = blockPrintSize;
		this.maxDigits = maxDigits;
		this.timeout = timeout;
	}
	
	private Quintet sum(Map<Integer, Quintet> inputs) {
		BigInteger result = BigInteger.valueOf(0);
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		long time = 0;
		boolean solved = true;
		
		for (Quintet q : inputs.values()) {
			if (!q.isSolved()) {
				solved = false;
			}
			min = Math.min(min, q.getNum());
			max = Math.max(max, q.getNum());
			result = result.add(q.getDuo());
			time += q.getTime();
		}
		return Quintet.of(min, solved, result, max, time);
	}
	
	private Map<Integer, Quintet> solveRange(final String method, final IDuoDigitMultipleFinder finder, final int start, final int end, final long timeThreshold) {
		Map<Integer, Quintet> m = new HashMap<>();
		for (int i = start; i <= end; i++) {
			Quintet q = finder.findDuoDigitMultiple(i);
			if (q.getTime() > timeThreshold) {
				System.out.printf(SINGLE_FORMAT, method, q.getNum(), q.getDuo(), q.getTime() / 1000.0);
			}
			
			m.put(q.getNum(), q);
		}
		return m;
	}
	
	public Map<Integer, Quintet> bruSolveRange(final int start, final int end, final long timeThreshold) {
		IDuoDigitMultipleFinder bruFinder = new BruDuoDigitMultipleFinder(0);
		return solveRange("bru", bruFinder, start, end, timeThreshold);
	}
	
	public Map<Integer, Quintet> bfsSolveRange(final int start, final int end, final long timeThreshold) {
		IDuoDigitMultipleFinder bfsFinder = new BFSDuoDigitMultipleFinder(maxDigits);
		return solveRange("bfs", bfsFinder, start, end, timeThreshold);
	}
	
	public Map<Integer, Quintet> mixSolveRange(final int start, final int end, final long timeThreshold) {
		IDuoDigitMultipleFinder mixFinder = new MixDuoDigitMultipleFinder(timeout, maxDigits);
		return solveRange("mix", mixFinder, start, end, timeThreshold);
	}
	
	public static void main(String[] args) {
		
		Solution714 s = new Solution714(30, 2000, 0x1FF);

		int start =  4001;
		int end   =  4096;
		long timeThreshold = 5000;
		Map<Integer, Quintet> bfsResult = s.bfsSolveRange(start, end, timeThreshold);
		Map<Integer, Quintet> bruResult = s.bruSolveRange(start, end, timeThreshold);
		Map<Integer, Quintet> mixResult = s.mixSolveRange(start, end, timeThreshold);
		
		for (int i = start; i <= end; i++) {
			Quintet bfsQuintet = bfsResult.get(i);
			Quintet bruQuintet = bruResult.get(i);
			Quintet mixQuintet = mixResult.get(i);
			if (bfsQuintet.getDuo().longValue() != bruQuintet.getDuo().longValue() || bfsQuintet.getDuo().longValue() != mixQuintet.getDuo().longValue()) {
				System.err.printf("bru - %5d: %25s\n", i, bruQuintet.getDuo(), bruQuintet.getTime() / 1000.0);
				System.err.printf("bfs - %5d: %25s\n", i, bfsQuintet.getDuo(), bfsQuintet.getTime() / 1000.0);
				System.err.printf("mix - %5d: %25s\n", i, mixQuintet.getDuo(), mixQuintet.getTime() / 1000.0);
				System.err.println("");
			}
		}
			
	}
}
