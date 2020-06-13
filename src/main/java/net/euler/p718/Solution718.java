package net.euler.p718;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Solution718 {
	
	class MyRunnable implements Runnable {

		final long low;
		final long high;
		final CountDownLatch countdownLatch;
		BigInteger result;
		
		MyRunnable(long low, long high, CountDownLatch countdownLatch) {
			this.low = low;
			this.high = high;
			this.countdownLatch = countdownLatch;
		}
		
		@Override
		public void run() {
			try {
				result = getUnresolvableSum(low, high);
			} finally {
				countdownLatch.countDown();
			}
		}
		
		BigInteger getResult() {
			return result;
		}
	}

	private final int power;
	private final int aFactor;
	private final int bFactor;
	private final int cFactor;
	private final int factor;
	private final int concurrency;
	
	public Solution718(int power) {
		this(power, 1);
	}
	
	public Solution718(int power, int concurrency) {
		this.power = power;
		this.aFactor = (int)Math.pow(17, power);
		this.bFactor = (int)Math.pow(19, power);
		this.cFactor = (int)Math.pow(23, power);
		this.factor = aFactor + bFactor + cFactor;
		this.concurrency = concurrency;
		
		System.out.printf("(%d + %d + %d) = %s, concurrency=%d\n", aFactor, bFactor, cFactor, factor, concurrency);
	}
	
	private BigInteger sum(long lower, long upper) {
		BigInteger sum = BigInteger.valueOf(lower + upper - 1);
		sum = sum.multiply(BigInteger.valueOf(upper - lower));
		sum = sum.divide(BigInteger.valueOf(2));
		return sum;
	}
	
	private BigInteger getUnresolvableSum(long lower, long upper) {
		BigInteger sum = sum(lower, upper);
		BigInteger resolvableSum = getResolvableSum(lower, upper);
		sum = sum.add(resolvableSum.negate());
		return sum;
	}

	private BigInteger getResolvableSum(long lower, long upper) {
		BitSet processedFlag = new BitSet((int)(upper - lower));
		int processedCount = 0;
		
		BigInteger sum = BigInteger.valueOf(0);
		long aMax = upper - bFactor - cFactor;
		long a = aFactor;
		while (a <= aMax) {
			long bMax = upper - a - cFactor;
			long b = bFactor;
			while (b > 0 && b <= bMax) {
				long cMax = upper - a - b;
				long c = Math.max(cFactor, ((lower - a - b) / cFactor) * cFactor);
				while (c > 0 && c <= cMax) {
					long s = a + b + c;
					if (s >= lower && s < upper) {
						int offset = (int)(s - lower);
						if (!processedFlag.get(offset)) {
							sum = sum.add(BigInteger.valueOf(s));
							processedFlag.set(offset);
							processedCount++;
						}
					}
					c += cFactor;
				}
				b += bFactor;
			}
			a += aFactor;
		}
		
		System.out.printf("   getResolvableSum(%d, %d), %d / %d\n", lower, upper, factor, processedCount);
		return sum;
	}

	public BigInteger getResult() throws InterruptedException {
		long lower = factor;
		long upper = lower + factor;
		BigInteger result = sum(1, lower);

		long st = System.currentTimeMillis();
		
		long step = concurrency;
		while (true) {

			long et = System.currentTimeMillis();
			System.out.printf("step = %d, time-elapsed=%.2fs\n", step, (et - st)/1000.0);
			st = et;
			
			CountDownLatch latch = new CountDownLatch(concurrency);
			List<MyRunnable> runnables = new ArrayList<>(concurrency);
			while (runnables.size() < concurrency) {
				runnables.add(new MyRunnable(lower, upper, latch));
				lower += factor;
				upper += factor;
			}
			for (MyRunnable runnable : runnables) {
				new Thread(runnable).start();
			}
			latch.await();
			
			boolean done = false;
			for (MyRunnable runnable : runnables) {
				BigInteger r = runnable.getResult();
				if (r.longValue() == 0) {
					done = true;
				} else {
					result = result.add(r);
				}
			}
			if (done) {
				break;
			}
			
			step += concurrency;
		}
		return result;
	}

//	public BigInteger getResult() {
//		long lower = factor;
//		long upper = lower + factor;
//		BigInteger result = sum(1, lower);
//		
//		long step = 1;
//		BigInteger unresolvableSum = getUnresolvableSum(lower, upper);
//		while (unresolvableSum.longValue() != 0) {
//			result = result.add(unresolvableSum);
//			lower += factor;
//			upper += factor;
//			
//			step += 1;
//			System.out.printf("step %d\n", step);
//			unresolvableSum = getUnresolvableSum(lower, upper);
//		}
//		return result;
//	}
	
	public static void main(String[] args) throws Exception {
		Solution718 s = new Solution718(6, 4);
		
		long st = System.currentTimeMillis();
		BigInteger result = s.getResult();
		long et = System.currentTimeMillis();
		
		BigInteger[] mod = result.divideAndRemainder(BigInteger.valueOf(1000000007));
		System.out.printf("result=%s, %s, mod=%s, total-time=%.2fs\n", result, mod[0], mod[1], ((et-st) / 1000.0));
		
	}
}
