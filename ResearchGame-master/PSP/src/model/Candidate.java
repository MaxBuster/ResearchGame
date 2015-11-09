package model;

public class Candidate {
	private int candidateNumber;
	private char party;
	private int idealPt;
	private int strawVotes = 0;
	private int firstRoundVotes = 0;
	private int secondRoundVotes = 0;

	public Candidate(int candidateNumber, char party, int idealPt) {
		this.candidateNumber = candidateNumber;
		this.party = party;
		this.idealPt = idealPt;
	}
	
	public Candidate(Candidate candidate) {
		this.candidateNumber = candidate.getCandidateNumber();
		this.party = candidate.getParty();
		this.idealPt = candidate.getIdealPt();
		this.strawVotes = candidate.getStrawVotes();
		this.firstRoundVotes = candidate.getFirstVotes();
		this.secondRoundVotes = candidate.getSecondVotes();
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
