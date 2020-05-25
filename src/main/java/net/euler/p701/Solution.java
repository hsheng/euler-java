package net.euler.p701;

import java.io.File;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Solution {

	public static final String FILENAME = "euler_p701_numbers.json";
	
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
			Counts thisCounts_0 = getCounts(baseNum);
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
	
	public void saveCalculation() throws Exception {
		ObjectNode oNode = mapper.createObjectNode();
		ObjectNode oNode1 = oNode.putObject("countsByNum");
		for (Map.Entry<Integer, Counts> entry : countsByNum.entrySet()) {
			Integer key = entry.getKey();
			Counts value = entry.getValue();
			ObjectNode vNode = oNode1.putObject(key.toString());
			vNode.put("totalCount", value.totalCount.toString());
			vNode.put("hasTwoCount", value.hasTwoCount.toString());
		}

		ObjectNode oNode2 = oNode.putObject("twopalCountByNum");
		for (Map.Entry<Integer, BigInteger> entry : twopalCountByNum.entrySet()) {
			Integer key = entry.getKey();
			BigInteger value = entry.getValue();
			oNode2.put(key.toString(), value.toString());
		}
		
		mapper.writeValue(new File(FILENAME), oNode);
	}

	public void initCalculation() throws Exception {
		File f = new File(FILENAME);
		if (f.exists() && f.canRead()) {
			ObjectNode rootNode = (ObjectNode) mapper.readTree(f);
			if (rootNode != null) {
				{
					Map<Integer, Counts> savedCountsByNum = new HashMap<Integer, Solution.Counts>();
					Iterator<Map.Entry<String, JsonNode>> fields = rootNode.get("countsByNum").fields();
					while (fields.hasNext()) {
						Map.Entry<String, JsonNode> entry = fields.next();
						Integer key = new Integer(entry.getKey());
						ObjectNode vNode = (ObjectNode)entry.getValue();
						BigInteger totalCount = new BigInteger(vNode.get("totalCount").asText());
						BigInteger twopalCount = new BigInteger(vNode.get("hasTwoCount").asText());
						savedCountsByNum.put(key, new Counts(totalCount, twopalCount));
					}
					this.countsByNum = savedCountsByNum;
				}

				{
					Map<Integer, BigInteger> savedTwopalCountByNum = new HashMap<Integer, BigInteger>();
					Iterator<Map.Entry<String, JsonNode>> fields = rootNode.get("twopalCountByNum").fields();
					while (fields.hasNext()) {
						Map.Entry<String, JsonNode> entry = fields.next();
						Integer key = new Integer(entry.getKey());
						BigInteger value = new BigInteger(entry.getValue().asText());
						savedTwopalCountByNum.put(key, value);
					}
					this.twopalCountByNum = savedTwopalCountByNum;
				}			
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		final Solution s = new Solution();
		s.initCalculation();
		
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
		
		long t1 = System.currentTimeMillis();
		while (true) {
			BigInteger twopals = s.getTwopalCount(n);
			System.out.printf("* %d - %s\n", n, twopals.toString());
			
			if (n % 10000 == 0) {
				long t2 = System.currentTimeMillis();
				System.out.printf("== %d - %d sec\n", n, (t2 - t1) / 1000);
				s.saveCalculation();
				t1 = System.currentTimeMillis();
			}
			
			BigInteger reminder = twopals.mod(ONE_MILLION);
			if (reminder.intValue() == 0) {
				break;
			} else {
				n++;
			}
		}
		
		System.out.printf("=== DONE (n=%d) ===\n", n);
	}
}

