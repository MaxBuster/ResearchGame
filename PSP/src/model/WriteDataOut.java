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
			// Do this for all of the other info
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
