package app;

public class Pair {
	
	public double first;
	public double second;
	
	public Pair(double first, double second) {
		this.first=first;
		this.second=second;
	}
	
	public String toString() {
		return String.format("%4.1f %4.1f",first,second);
	}
}
