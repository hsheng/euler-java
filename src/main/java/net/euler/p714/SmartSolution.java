package net.euler.p714;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class SmartSolution {
	
	private static long ts = System.currentTimeMillis();
	
	Helper helper = Helper.instance();
	ReentrantReadWriteLock listLock = new ReentrantReadWriteLock();
	ReentrantReadWriteLock mapLock = new ReentrantReadWriteLock();
	
	private synchronized Quintet getNextUnresolvedQuintet(Map<Integer, Quintet> unresolvedQuintetPool) {
		Iterator<Integer> keys = unresolvedQuintetPool.keySet().iterator();
		while (keys.hasNext()) {
			return unresolvedQuintetPool.remove(keys.next());
		}
		return null;
	}
	
	private synchronized void postQuintetResolved(Quintet resolvedQuintet, Map<Integer, Quintet> unresolvedQuintetPool, int numBoundary) {
		unresolvedQuintetPool.remove(resolvedQuintet.getNum());

		long multiple = resolvedQuintet.getMultiple();
		int nextNum = resolvedQuintet.getNum();
		
		while ((multiple & 0x1) == 0 && nextNum <= numBoundary ) {
			multiple = multiple >> 1;
			Quintet nextQuintet = unresolvedQuintetPool.get(nextNum);
			if (nextQuintet != null) {
				nextQuintet.setResolved(true);
				nextQuintet.setDuoDigitMultiple(resolvedQuintet.getDuoDigitMultiple());
				nextQuintet.setMultiple(multiple);
				unresolvedQuintetPool.remove(nextNum);
			}
			nextNum = nextNum << 1;
		}
		
		int poolSize = unresolvedQuintetPool.size();
		if ((poolSize & 0x3F) == 0) {
			long t = System.currentTimeMillis();
			System.out.printf("    %d numbers remained, %.2f m elapsed\n", poolSize, 1.0*(t - ts)/60000);
		}
	}
	
	private void resolveQuintet(Quintet quintet) {
		synchronized(quintet) {
			if (!quintet.isResolved()) {
				Quartet duoNum = helper.getDuoNumAndMultiple(quintet.getNum());
				quintet.setResolved(true);
				quintet.setDuoDigitMultiple(duoNum.getDuoDigitMultiple());
				quintet.setMultiple(duoNum.getMultiple());
			}
		}
	}
	
	public void doit(final int n, final int concurrency) throws Exception {
		
		final List<Quintet> quintetPool = helper.separate(n);
		final Map<Integer, Quintet> unresolvedQuintetPool = quintetPool.stream().filter(num->!num.isResolved())
				.collect(Collectors.toMap(Quintet::getNum, quintet->quintet, (u,v) -> { throw new IllegalStateException(String.format("Duplicate key %s", u)); }, LinkedHashMap::new));

		System.out.printf("*** (%d : %5d) - Total %d numbers to be solved\n", concurrency, n, unresolvedQuintetPool.size());
		ts = System.currentTimeMillis();
		
		CountDownLatch countdownLatch = new CountDownLatch(concurrency);

		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				while (!unresolvedQuintetPool.isEmpty()) {
					Quintet quintet = getNextUnresolvedQuintet(unresolvedQuintetPool);
					if (quintet != null) {
						resolveQuintet(quintet);
						postQuintetResolved(quintet, unresolvedQuintetPool, n);
					}
				}
				countdownLatch.countDown();
			}
			
		};
		
		List<Thread> threads = new ArrayList<Thread>(concurrency);
		for (int i = 0; i < concurrency; i++) {
			Thread thread = new Thread(runnable);
			threads.add(thread);
			thread.start();
		}

		countdownLatch.await();
		
		long et = System.currentTimeMillis();
		System.out.printf("%d: %5d - solved duo-digit-multiple in %10ds\n", concurrency, n, (et-ts)/1000);

		BigInteger sum = quintetPool.stream().map(Quintet::getDuoDigitMultiple).reduce(BigInteger::add).get();
		
		System.out.printf("%d: %5d - %s, %10ds\n", concurrency, n, sum.toString(), (et-ts)/1000);
	}
	
//	public static void main(String[] args) throws Exception {
//		final int concurrency = 6;
//		SmartSolution s = new SmartSolution();
////		s.doit(110, concurrency);
////		s.doit(150, concurrency);
////		s.doit(500, concurrency);
////		s.doit(1000, concurrency);
////		s.doit(2000, concurrency);
////		s.doit(3000, concurrency);
////		s.doit(4000, concurrency);
////		s.doit(5000, concurrency);
//		s.doit(50000, concurrency);
//	}
	
	public static void main(String[] args) throws Exception {
		Helper helper = Helper.instance();
		
		for (int i = 50000; i > 49990; i--) {
			if (!helper.isDuoDigitNumber(i)) {
				System.err.printf("%d ...\n", i);
				Quartet solved = helper.getDuoNumAndMultiple(i);
				System.out.printf("\tdata=%s\n", solved);
			}
		}
	}
}
