package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

public class ReadConfig {

	public static String[] cleanArray(String[] input) {
		Vector<String> cleaned = new Vector<String>();
		for (String s : input) {
			if (!s.isEmpty()) {
				cleaned.add(s);
			}
		}
		String[] cleanedArray = new String[cleaned.size()];
		for (int i = 0; i<cleaned.size(); i++) {
			cleanedArray[i] = cleaned.get(i);
		}
		return cleanedArray;
	}

	public static ArrayList<GameInfo> readFile() {
		String fileName = "config.csv";
		File file = new File(fileName);
		if (file.canRead()) {
			try {
				ArrayList<GameInfo> games = new ArrayList<GameInfo>();
				FileReader fReader = new FileReader(file);
				BufferedReader bReader = new BufferedReader(fReader);
				int gameNum = 1;
				String line;
				// Get first line = numgames
				while ((line = bReader.readLine()) != null) {
					line = bReader.readLine();
					// FIXME Check that the line is not blank
					String[] candidates = cleanArray(line.split("[ ]*,[ ]*"));
					int[] candidateNums = new int[candidates.length];
					for (int i=0; i<candidates.length; i++) {
						candidateNums[i] = Integer.parseInt(candidates[i]);
					}
					if ((line = bReader.readLine()) != null) {
						// FIXME Check that the line is not blank
						String[] parties = cleanArray(line.split("[ ]*,[ ]*"));
						char[] partyChars = new char[parties.length];
						for (int i=0; i<parties.length; i++) {
							partyChars[i] = parties[i].charAt(0);
						}
						if ((line = bReader.readLine()) != null) {
							String[] distribution = cleanArray(line.split(","));
							// FIXME check that distribution is 4 long
							int[] distributionNums = new int[distribution.length];
							for (int i=0; i<distribution.length; i++) {
								distributionNums[i] = Integer.parseInt(distribution[i]);
							}
							line = bReader.readLine();
							// FIXME Check that the line is not blank
							String[] payoffInfo = cleanArray(line.split("[ ]*,[ ]*"));
							int[] payoffNums = new int[payoffInfo.length];
							for (int i=0; i<payoffInfo.length; i++) {
								payoffNums[i] = Integer.parseInt(payoffInfo[i]);
							}
							// FIXME get parties of cands too
							if ((line = bReader.readLine()) != null) {
								line = line.replace(",", "");
								int budget = Integer.parseInt(line); // FIXME Catch errors thrown by this
								GameInfo game = new GameInfo(gameNum, candidateNums, partyChars, distributionNums, budget, payoffNums);
								games.add(game);
								gameNum++;
							}
						}
					}
				}
				bReader.close();
				return games;
			} catch (FileNotFoundException e) {
				return null;
			} catch (IOException e) {
				return null;
			}
		} else {
			return null;
		}
	}
}
