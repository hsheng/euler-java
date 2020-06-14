package net.euler;

public class Quintet<A, B, C, D, E> {

	public final A v1;
	public final B v2;
	public final C v3;
	public final D v4;
	public final E v5;
	
	protected Quintet(A v1, B v2, C v3, D v4, E v5) {
		this.v1 = v1;
		this.v2 = v2;
		this.v3 = v3;
		this.v4 = v4;
		this.v5 = v5;
	}
	
	public static <A, B, C, D, E> Quintet<A, B, C, D, E> of(A v1, B v2, C v3, D v4, E v5) {
		return new Quintet<A, B, C, D, E>(v1, v2, v3, v4, v5);
	}
	
}
