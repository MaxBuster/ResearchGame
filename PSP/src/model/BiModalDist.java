package model;

import org.apache.commons.math3.distribution.NormalDistribution;

public class BiModalDist {
	private int[] data = new int[100];
	private int totalPoints;

	public BiModalDist(int split, int percent1, int percent2) {
		// Have an array with the running totals
		// Have accessor for running totals, total
		// If random generated is between two values, give it that value
		int mean1 = 50 - (split / 2);
		int mean2 = 50 + (split / 2);
		int stdev = mean1 / 3;
		NormalDistribution normal1 = new NormalDistribution(mean1, stdev);
		NormalDistribution normal2 = new NormalDistribution(mean2, stdev);
		int previousInt = 0;
		int total = 0;
		int totalPoints = 0;
		for (int i = 0; i < 100; i++) {
			int nextInt = (int) (10 * (percent1
					* normal1.cumulativeProbability(i) + percent2
					* normal2.cumulativeProbability(i)));
			int num = (nextInt - previousInt);
			// System.out.println(num);
			data[i] = num;
			total += nextInt;
			totalPoints += num;
			previousInt = nextInt;
		}
		this.totalPoints = totalPoints;
	}

	public int[] getData() {
		return data;
	}

	public int getTotalPoints() {
		return totalPoints;
	}
}
