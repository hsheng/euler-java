package net.euler.p714;

public class MixDuoDigitMultipleFinder implements IDuoDigitMultipleFinder {

	private final long timeout;
	private final int maxDigits;

	public MixDuoDigitMultipleFinder(long timeout, int maxDigits) {
		this.timeout = timeout;
		this.maxDigits = maxDigits;
	}
	
	@Override
	public Quintet findDuoDigitMultiple(int n) {
		BruDuoDigitMultipleFinder bruFinder = new BruDuoDigitMultipleFinder(timeout);
		Quintet q1 = bruFinder.findDuoDigitMultiple(n);
		if (q1.isSolved()) {
			return q1;
		}
		BFSDuoDigitMultipleFinder bfsFinder = new BFSDuoDigitMultipleFinder(maxDigits);
		Quintet q2 = bfsFinder.findDuoDigitMultiple(n);
		return Quintet.of(q2.getNum(), q2.isSolved(), q2.getDuo(), 0L, (q1.getTime() + q2.getTime()));
	}

}
