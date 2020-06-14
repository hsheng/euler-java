package net.euler.p714;

import java.math.BigInteger;

public class BruDuoDigitMultipleFinder implements IDuoDigitMultipleFinder {

	private final long timeout;
	private final long chunkSize = 0x3FF;
	
	public BruDuoDigitMultipleFinder(long timeout) {
		this.timeout = Math.max(timeout, 0);
	}
	
	@Override
	public Quintet findDuoDigitMultiple(int num) {
		if (isDuoDigitNum(num)) {
			return Quintet.of(num, true, BigInteger.valueOf(num), 1L, 0L);
		}
		
		final BigInteger base = BigInteger.valueOf(num);

		long t1 = System.currentTimeMillis();
		
		long multiple = 2;
		BigInteger duo = BigInteger.valueOf(num << 1);
		while (!isDuoDigitNum(duo)) {
			if ((timeout != 0 && (multiple & chunkSize) == 0)) {
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

}
