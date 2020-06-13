package net.euler.p718;

public class Resolve {

	public final long n;
	public final Integer a;
	public final Integer b;
	public final Integer c;
	public final boolean resolved;
	
	public Resolve(long n, Integer a, Integer b, Integer c) {
		this.n = n;
		this.a = a;
		this.b = b;
		this.c = c;
		this.resolved = (a != null && b != null && c != null);
	}
}
