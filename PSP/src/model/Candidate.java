package model;

public class Candidate {
	private static final int NOISE = 10;
	private int candidateNumber;
	private char party;
	private int idealPt;
	private int lowerBound;
	private int upperBound;
	private int strawVotes = 0;
	private int firstRoundVotes = 0;
	private int secondRoundVotes = 0;

	public Candidate(int candidateNumber, char party, int idealPt) {
		this.candidateNumber = candidateNumber;
		this.party = party;
		this.idealPt = idealPt;
		this.lowerBound = calcLowerBound(idealPt);
		this.upperBound = calcUpperBound(idealPt);
	}

	public int calcLowerBound(int idealPt) {
		idealPt -= NOISE;
		if (idealPt < 0) {
			return 0;
		} else {
			return idealPt;
		}
	}

	public int calcUpperBound(int idealPt) {
		idealPt += NOISE;
		if (idealPt > 100) {
			return 100;
		} else {
			return idealPt;
		}
	}

	public int getCandidateNumber() {
		return candidateNumber;
	}

	public char getParty() {
		return party;
	}

	public int getIdealPt() {
		return idealPt;
	}

	public int getLowerBound() {
		return lowerBound;
	}

	public int getUpperBound() {
		return upperBound;
	}

	public void voteStraw() {
		strawVotes++;
	}

	public int getStrawVotes() {
		return strawVotes;
	}

	public void voteFirst() {
		firstRoundVotes++;
	}

	public int getFirstVotes() {
		return firstRoundVotes;
	}

	public void voteSecond() {
		secondRoundVotes++;
	}

	public int getSecondVotes() {
		return secondRoundVotes;
	}

}
