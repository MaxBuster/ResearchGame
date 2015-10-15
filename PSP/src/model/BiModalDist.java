package model;

import org.apache.commons.math3.distribution.NormalDistribution;

public class BiModalDist {
	private int[] data = new int[100];
	private int[] sumData = new int[100];
	private int sum = 0;

	public BiModalDist(int[] graphData) {
		// Have an array with the running totals
		// Have accessor for running totals, total
		// If random generated is between two values, give it that value
		int mean1 = graphData[0];
		int stdDev1 = graphData[1];
		int mean2 = graphData[2];
		int stdDev2 = graphData[3];
		NormalDistribution normal1 = new NormalDistribution(mean1, stdDev1);
		NormalDistribution normal2 = new NormalDistribution(mean2, stdDev2);
		for (int i = 0; i < 100; i++) {
			int num = (int) (100*(50*normal1.density(i) + 50*normal2.density(i)));
			sum += num;
			sumData[i] = sum;
			data[i] = num;
		}
	}

	public int[] getData() {
		return data;
	}
	
	public int[] getSumData() {
		return sumData;
	}
	
	public int getSum() {
		return sum;
	}
}
