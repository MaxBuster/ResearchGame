package model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedList;

import com.orsoncharts.util.json.JSONArray;
import com.orsoncharts.util.json.JSONObject;

public class WriteDataOut {
	public static void writeData(ArrayList<GameInfo> gameInfo) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File("data.csv")));
			for (GameInfo game : gameInfo) {
				// Game Info
				writer.write("Game: #," + game.getGameNum() + "\n");
				writer.write("Budget:, " + game.getBudget() + "\n");
				int[] dist = game.getDistribution();
				writer.write("Distribution:, " + implode(dist));
				int[] idealPts = game.getIdealPts();
				writer.write("Ideal Points:, " + implode(idealPts));
				// Player Info
				LinkedList<Player> players = game.getPlayers();
				for (Player player : players) {
					writer.write("Ideal Point:, " + player.getIdealPt() + "\n");
					writer.write("Votes:, " + implode(player.getVotes()));
					int[][] playerInfo = player.getInfo();
					int[] candNums = new int[playerInfo.length];
					int[] ones = new int[playerInfo.length];
					int[] zeroes = new int[playerInfo.length];
					for (int i=0; i<playerInfo.length; i++) {
						candNums[i] = i;
						zeroes[i] = playerInfo[i][0];
						ones[i] = playerInfo[i][1];
					}
					writer.write("Candidate #s:, " + implode(candNums));
					writer.write("Ones:, " + implode(ones));
					writer.write("Zeroes:, " + implode(zeroes));
				}
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String implode(int[] input) {
		String output = "";
		for (int i=0; i<input.length-1; i++) {
			output += input[i] + ",";
		}
		output += input[input.length-1] + "\n";
		return output;
	}
}
