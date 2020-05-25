package net.euler.p701;

import java.math.BigInteger;

public class SolutionTest {

	public static final BigInteger TWO = BigInteger.valueOf(2);
	
	public static void main(String[] args) {
		
		int n = 5;
		BigInteger count = BigInteger.valueOf(16);
		System.out.printf("%d, %s\n", n, count.toString());
		
		while (true) {
			n++;
			BigInteger newCount = count.multiply(TWO);
			String str = newCount.toString();
			System.out.printf("%d, %d, %s\n", n, str.length(), str);
			
			if (str == null || str.length() == 0 || str.trim().length() == 0) {
				System.err.printf("** %d %%, %s, %s\n", n, count.toString(), newCount.toString());
				System.exit(1);
			}
			count = newCount;
		}
	}
}
