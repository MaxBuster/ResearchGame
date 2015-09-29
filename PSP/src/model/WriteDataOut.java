package model;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import com.orsoncharts.util.json.JSONArray;
import com.orsoncharts.util.json.JSONObject;

public class WriteDataOut {
	
	@SuppressWarnings("unchecked")
	public static void createPlayerString(ArrayList<Player> players, String fileName) {
		JSONObject JSONFileObject = new JSONObject();
		for (Player player : players) {
			JSONArray JSONPlayerArray = new JSONArray();
			JSONPlayerArray.add("Player Number: " + player.getPlayerNumber());
			JSONPlayerArray.add("Ideal Point: " + player.getIdealPt());
			JSONPlayerArray.add("Party: " + player.getParty());
			
			int[] playerInfo = player.getInfo();
			JSONArray JSONPlayerInfo = new JSONArray();
			for (int i=0; i<playerInfo.length; i++) {
				JSONPlayerInfo.add("Candidate " + (i+1) + " info: " + playerInfo[i]);
			}
			JSONPlayerArray.add(JSONPlayerInfo);
			
			int[] playerVotes = player.getVotes();
			JSONArray JSONPlayerVotes = new JSONArray();
			for (int i=0; i<playerVotes.length; i++) {
				JSONPlayerVotes.add("Round " + i + " candidate: " + playerVotes[i]);
			}
			JSONPlayerArray.add(JSONPlayerVotes);
			
			JSONFileObject.put("Player Data", JSONPlayerArray);
		}
		String JSONFileString = JSONFileObject.toJSONString();
		writeStringToFile(JSONFileString, fileName);
	}
	
	@SuppressWarnings("unchecked")
	public static void createCandidateString(Candidate[] candidates, String fileName) {
		JSONObject JSONFileObject = new JSONObject();
		for (Candidate candidate : candidates) {
			JSONArray JSONCandidateArray = new JSONArray();
			JSONCandidateArray.add("Player Number: " + candidate.getCandidateNumber());
			JSONCandidateArray.add("Ideal Point: " + candidate.getIdealPt());
			JSONCandidateArray.add("Party: " + candidate.getParty());
			JSONCandidateArray.add("Straw Votes: " + candidate.getStrawVotes());
			JSONCandidateArray.add("First Votes: " + candidate.getFirstVotes());
			JSONCandidateArray.add("Second Votes: " + candidate.getSecondVotes());
			JSONFileObject.put("Candidate Data", JSONCandidateArray);
		}
		String JSONFileString = JSONFileObject.toJSONString();
		writeStringToFile(JSONFileString, fileName);
	}
	
	private static void writeStringToFile(String fileData, String fileName) {
		Writer writer = null;
		try {
		    writer = new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream(fileName), "utf-8"));
		    writer.write(fileData);
		} catch (IOException ex) {
		  // report
		} finally {
		   try {writer.close();} catch (Exception ex) {/*ignore*/}
		}
	}
}
