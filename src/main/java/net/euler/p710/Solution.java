package net.euler.p710;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Solution {

	public static final String FILENAME_1 = "countsByNum.txt";
	public static final String FILENAME_2 = "twopalCountByNum.txt";
	public static final String FILENAME_3 = "euler.p701.log";
	
	public static final BigInteger TWO = new BigInteger("2");
	public static final BigInteger THREE = new BigInteger("3");
	public static final BigInteger ONE_MILLION = new BigInteger("1000000");
	
	static class Counts {
		public final BigInteger totalCount;
		public final BigInteger hasTwoCount;
		
		Counts(BigInteger totalCount, BigInteger hasTwoCount) {
			this.totalCount = totalCount;
			this.hasTwoCount = hasTwoCount;
		}
	}
	
	static class Pair<T> {
		public final T i1;
		public final T i2;
		
		Pair(T i1, T i2) {
			this.i1 = i1;
			this.i2 = i2;
		}
	}
	
	Map<Integer, Counts> countsByNum = new HashMap<Integer, Counts>() {{
		put(1, new Counts(new BigInteger("1"), new BigInteger("0")));
		put(2, new Counts(new BigInteger("2"), new BigInteger("1")));
		put(3, new Counts(new BigInteger("4"), new BigInteger("2")));
		put(4, new Counts(new BigInteger("8"), new BigInteger("4")));
		put(5, new Counts(new BigInteger("16"), new BigInteger("9")));
	}};
	
	Map<Integer, BigInteger> twopalCountByNum = new HashMap<Integer, BigInteger>() {{
		put(6, new BigInteger("4"));
		put(7, new BigInteger("3"));
		put(8, new BigInteger("9"));
		put(9, new BigInteger("7"));
		put(10, new BigInteger("20"));
	}};
	
	private ObjectMapper mapper = new ObjectMapper();
	
	public Solution() {
		mapper.enable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);
	}
	
	public Pair<Integer> getKeyRange(Map<Integer, ?> m) {
		int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
		for (int key : m.keySet()) {
			min = Math.min(min, key);
			max = Math.max(max, key);
		}
		return new Pair<Integer>(min, max);
	}
	
	void doHouseKeeping(Map<Integer, ?> m) {
		Pair<Integer> range = getKeyRange(m);
		int start = range.i1;
		int stop = range.i2 - 6;
		while (start < stop) {
			m.remove(start++);
		}
	}
	
	public Counts getCounts(int n) {
		Pair<Integer> range = getKeyRange(countsByNum);
		if (n > range.i2) {
			Counts counts_5 = getCounts(n - 5);
			Counts counts_4 = getCounts(n - 4);
			Counts counts_3 = getCounts(n - 3);
			Counts counts_2 = getCounts(n - 2);
			Counts counts_1 = getCounts(n - 1);

			BigInteger totalCount = counts_1.totalCount.multiply(TWO);
			BigInteger hasTwoCount = counts_1.hasTwoCount.multiply(THREE)
					.add(counts_2.hasTwoCount.negate())
					.add(counts_3.hasTwoCount.multiply(TWO).negate())
					.add(counts_4.hasTwoCount)
					.add(counts_5.hasTwoCount.multiply(TWO).negate());
			Counts counts = new Counts(totalCount, hasTwoCount);
			countsByNum.put(n, counts);
		}

		doHouseKeeping(countsByNum);
		return countsByNum.get(n);
	}
	
	public BigInteger getTwopalCount(int n) {
		if (!twopalCountByNum.containsKey(n - 1)) {
			throw new IllegalStateException("Cannot find twopal counts for number " + (n - 1));
		}

		if (twopalCountByNum.containsKey(n)) {
			return twopalCountByNum.get(n);
		}
		
		int baseNum = n / 2;
		BigInteger thisTwopalCount = new BigInteger("0");
		BigInteger prevTwopalCount = twopalCountByNum.get(n - 1);
		if ((n & 0x1) == 1) {
			// n is ODD number
			Counts thisCounts_1 = getCounts(baseNum - 1);
//			Counts thisCounts_0 = getCounts(baseNum);
			thisTwopalCount = prevTwopalCount
					.add(thisCounts_1.totalCount.negate())
					.add(thisCounts_1.hasTwoCount);
		} else {
			// n is EVEN number
			Counts thisCounts_1 = getCounts(baseNum - 1);
			Counts thisCounts_0 = getCounts(baseNum);
			thisTwopalCount = prevTwopalCount
					.add(thisCounts_1.hasTwoCount.negate())
					.add(thisCounts_1.totalCount)
					.add(thisCounts_0.hasTwoCount);
		}
		
		doHouseKeeping(twopalCountByNum);
		twopalCountByNum.put(n, thisTwopalCount);
		return thisTwopalCount;
	}
	
	void print(Counts counts) {
		System.out.println("total: " + counts.totalCount.toString());
		System.out.println("hasTwo: " + counts.hasTwoCount.toString());
	}
	
	private void saveCountsByNum() throws Exception {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILENAME_1))) {
			for (Map.Entry<Integer, Counts> entry : countsByNum.entrySet()) {
				Integer key = entry.getKey();
				Counts counts = entry.getValue();
				writer.write(key.toString());
				writer.newLine();
				writer.write(counts.totalCount.toString());
				writer.newLine();
				writer.write(counts.hasTwoCount.toString());
				writer.newLine();
			}
		}
	}

	private void saveTwopalCount() throws Exception {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILENAME_2))) {
			for (Map.Entry<Integer, BigInteger> entry : twopalCountByNum.entrySet()) {
				Integer key = entry.getKey();
				BigInteger count = entry.getValue();
				writer.write(key.toString());
				writer.newLine();
				writer.write(count.toString());
				writer.newLine();
			}
		}
	}
	
	public void saveCalculation() throws Exception {
		saveCountsByNum();
		saveTwopalCount();
	}

	private Map<Integer, Counts> readCountsByNum(File f) throws Exception {
		Map<Integer, Counts> m = new HashMap<Integer, Solution.Counts>();
		try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
			String sKey = reader.readLine();
			String sTotalCount = reader.readLine();
			String sHasTwoCount = reader.readLine();
			while (StringUtils.isNotBlank(sKey) && StringUtils.isNotBlank(sTotalCount) && StringUtils.isNotBlank(sHasTwoCount)) {
				Integer key = new Integer(sKey);
				Counts count = new Counts(new BigInteger(sTotalCount), new BigInteger(sHasTwoCount));
				m.put(key, count);

				sKey = reader.readLine();
				sTotalCount = reader.readLine();
				sHasTwoCount = reader.readLine();
			} 
		}
		return m;
	}

	private Map<Integer, BigInteger> readTwopalCountByNum(File f) throws Exception {
		Map<Integer, BigInteger> m = new HashMap<Integer, BigInteger>();
		try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
			String sKey = reader.readLine();
			String sCount = reader.readLine();
			while (StringUtils.isNotBlank(sKey) && StringUtils.isNotBlank(sCount)) {
				Integer key = new Integer(sKey);
				BigInteger count = new BigInteger(sCount);
				m.put(key, count);
				
				sKey = reader.readLine();
				sCount = reader.readLine();
			}
		}
		return m;
	}
	
	public void readCalculation() throws Exception {
		File f1 = new File(FILENAME_1);
		File f2 = new File(FILENAME_2);
		if (f1.exists() && f2.exists() && f1.canRead() && f2.canRead()) {
			Map<Integer, Counts> m1 = readCountsByNum(f1);
			Map<Integer, BigInteger> m2 = readTwopalCountByNum(f2);
			this.countsByNum = m1;
			this.twopalCountByNum = m2;
		}
	}
	
	private static String print(int n, BigInteger count) {
		String str = count.toString();
		int strLen = str.length();
		str = str.substring(strLen - 6);
		System.out.printf("%d - %s, %d\n", n, str, strLen);

		try {
			if ((n & 0xFFF) == 0) {
				try (BufferedWriter logger = new BufferedWriter(new FileWriter(FILENAME_3))) {
					logger.write(String.format("%d - %s", n, str));
					logger.newLine();
				}
			} else {
				try (BufferedWriter logger = new BufferedWriter(new FileWriter(FILENAME_3, true))) {
					logger.write(String.format("%d - %s", n, str));
					logger.newLine();
				}
			}
		} catch (Exception ex) {
			// ignore
		}
		
		return str;
	}
	
	public static void main(String[] args) throws Exception {
		final Solution s = new Solution();
		s.readCalculation();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
	            try {
	            	s.saveCalculation();
	            } catch (Exception ex) {
	                ex.printStackTrace();
	            }
	        }
		});

		Pair<Integer> range = s.getKeyRange(s.twopalCountByNum);
		int n = range.i2;

		while (true) {
			BigInteger twopals = s.getTwopalCount(n);
			String str = print(n, twopals);

			if ((n & 0x0FFF) == 0) {
				s.saveCalculation();
			}

			if ("000000".equals(str)) {
				break;
			} else {
				n++;
			}
		}

		System.out.printf("=== DONE (n=%d) ===\n", n);
	}
}

