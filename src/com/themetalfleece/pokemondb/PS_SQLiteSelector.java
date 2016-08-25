/**
 * 
 */
package com.themetalfleece.pokemondb;

/**
 *
 * Created by themetalfleece at 26 Jun 2016
 *
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class PS_SQLiteSelector {

	private PreparedStatement prep;
	private ResultSet rs;
	private Connection c = null;

	// for similarityMode
	private HashMap<String, String> allPokes = new HashMap<String, String>();
	private HashMap<String, String> allMoves = new HashMap<String, String>();
	private HashMap<String, String> allItems = new HashMap<String, String>();
	private HashMap<String, String> allAbilities = new HashMap<String, String>();

	// lings
	private String serebiiBegin = "www.serebii.net/pokedex-xy/";
	private String serebiiEnd = ".shtml";
	private String smogonPokemonBegin = "www.smogon.com/dex/xy/pokemon/";
	private String smogonMoveBegin = "www.smogon.com/dex/xy/moves/";
	private String smogonItemBegin = "www.smogon.com/dex/xy/items/";
	private String smogonAbilityBegin = "www.smogon.com/dex/xy/abilities/";
	private String smogonEnd = "/";

	// searches specific Map
	private String getIdBySimilarity(String input, HashMap<String, String> map) {

		double max = 0;
		String maxName = null;

		Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, String> mentry = (Map.Entry<String, String>) iterator.next();
			double currentSim = StringSimilarity.similarity(input, mentry.getValue());
			if (currentSim > max) {
				max = currentSim;
				maxName = mentry.getKey();

			}
		}

		return maxName;

	}

	// searches all Maps. return id and info
	public String[] getAnyInfoByName(String input) {

		double max = 0;
		String maxName = null;

		List<Iterator<Entry<String, String>>> iterators = new ArrayList<Iterator<Entry<String, String>>>();
		int whichList = 0;

		iterators.add(0, allPokes.entrySet().iterator());
		iterators.add(1, allAbilities.entrySet().iterator());
		iterators.add(2, allMoves.entrySet().iterator());
		iterators.add(3, allItems.entrySet().iterator());
		for (int i = 0; i < 4; i++) {
			while (iterators.get(i).hasNext()) {
				Map.Entry<String, String> mentry = (Map.Entry<String, String>) iterators.get(i).next();
				double currentSim = StringSimilarity.similarity(input, mentry.getValue());
				if (currentSim > max) {
					max = currentSim;
					maxName = mentry.getKey();
					whichList = i;

				}
			}
		}

		String[] info = new String[2];
		info[0] = maxName;

		switch (whichList) {
		case 0:
			info[1] = getPokeInfoById(maxName);
			break;
		case 1:
			info[1] = getAbilityInfoById(maxName);
			break;
		case 2:
			info[1] = getMoveInfoById(maxName);
			break;
		case 3:
			info[1] = getItemInfoById(maxName);
			break;
		default:
			return null;

		}
		
		return info;

	}

	public String getAbilityInfoById(String id) {

		try {
			prep = c.prepareStatement("SELECT * FROM Abilities WHERE id = ?;");
			prep.setString(1, id);
			rs = prep.executeQuery();
			if (rs.next()) {
				String name = rs.getString("name");
				String info = new String();
				info += name;
				info += " | " + "Description: " + rs.getString("shortDesc");
				info += " | " + "Rating (-2 to 5): " + rs.getString("rating");
				info += " | " + smogonAbilityBegin + id + smogonEnd;
				return info;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return "Database error";
		}

		return "Ability not found";
	}

	public String getMoveInfoById(String id) {

		try {
			prep = c.prepareStatement("SELECT * FROM Moves WHERE id = ?;");
			prep.setString(1, id);
			rs = prep.executeQuery();
			if (rs.next()) {
				String name = rs.getString("name");
				String info = new String();
				info += name;
				info += " | " + "Type: " + rs.getString("type");
				int basePower = rs.getInt("basePower");
				info += " | " + "Base Power: " + (basePower == 0 ? "--" : rs.getString("basePower"));
				int accuracy = rs.getInt("accuracy");
				info += " | " + "Accuracy: " + (accuracy == 0 ? "--" : rs.getString("accuracy"));
				info += " | " + "Category: " + rs.getString("category");
				info += " | " + "Description: " + rs.getString("shortDesc");
				info += " | " + "PP: " + rs.getString("pp");
				int priority = rs.getInt("priority");
				info += (priority != 0 ? " | " + "Priority: " + (priority > 0 ? "+" : "") + priority : "");

				String rawTarget = rs.getString("target");
				StringBuffer target = new StringBuffer();
				target.append(Character.toUpperCase(rawTarget.charAt(0)));
				for (int i = 1; i < rawTarget.length(); i++) {
					if (Character.isUpperCase(rawTarget.charAt(i)))
						target.append(" ");
					target.append(rawTarget.charAt(i));
				}

				info += " | " + "Target: " + target;
				info += " | " + smogonMoveBegin + id + smogonEnd;
				return info;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return "Database error";
		}

		return "Ability not found";
	}

	public String getItemInfoById(String id) {

		try {
			prep = c.prepareStatement("SELECT * FROM Items WHERE id = ?;");
			prep.setString(1, id);
			rs = prep.executeQuery();
			if (rs.next()) {
				String name = rs.getString("name");
				String info = new String();
				info += name;
				info += " (Gen " + rs.getString("gen") + ")";
				info += " | " + "Description: " + rs.getString("desc");
				info += " | " + smogonItemBegin + id + smogonEnd;

				return info;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return "Database error";
		}

		return "Item not found";
	}

	public String getPokeInfoById(String id) {

		try {
			prep = c.prepareStatement("SELECT * FROM Pokemon WHERE name = ?;");
			prep.setString(1, id);
			rs = prep.executeQuery();
			if (rs.next()) {
				String species = rs.getString("species");
				String num = rs.getString("num");
				String type1 = rs.getString("type1"), type2 = rs.getString("type2");

				String[] statNames = { "hp", "atk", "def", "spa", "spd", "spe" };
				int[] stats = new int[statNames.length];
				String statInfo = new String();
				int BST = 0;
				for (int i = 0; i < stats.length; i++) {
					stats[i] = rs.getInt(statNames[i]);
					if (i != 0)
						statInfo += "/";
					statInfo += Integer.toString(stats[i]);
					BST += stats[i];
				}

				String abilities = new String();
				int abilitiesCount = 0;
				prep = c.prepareStatement("SELECT abilityName, slot FROM PokeAbilities WHERE pokeName = ?;");
				prep.setString(1, id);
				ResultSet rs = prep.executeQuery();

				while (rs.next())
					abilities += (abilitiesCount++ != 0 ? ", " : "") + rs.getString("abilityName")
							+ (rs.getString("slot").equals("H") ? " (Hidden)" : "");

				String forme = new String();
				prep = c.prepareStatement("SELECT baseSpecies FROM PokeFormes WHERE pokeName = ?;");
				prep.setString(1, id);
				rs = prep.executeQuery();
				while (rs.next())
					forme += rs.getString("baseSpecies");

				String prevo = new String();
				prep = c.prepareStatement(
						"SELECT Pokemon.species FROM Pokemon JOIN PokePrevos ON Pokemon.name = PokePrevos.prevoName WHERE PokePrevos.pokeName = ?;");
				prep.setString(1, id);
				rs = prep.executeQuery();
				while (rs.next())
					prevo += rs.getString("species");

				String evos = new String();
				prep = c.prepareStatement(
						"SELECT Pokemon.species FROM Pokemon JOIN PokeEvos ON Pokemon.name = PokeEvos.evoName WHERE PokeEvos.pokeName = ?;");
				prep.setString(1, id);
				rs = prep.executeQuery();
				int evosCount = 0;
				while (rs.next())
					evos += (evosCount++ != 0 ? ", " : "") + rs.getString("species");

				String otherFormes = new String();
				prep = c.prepareStatement(
						"SELECT Pokemon.species FROM Pokemon JOIN PokeOtherFormes ON Pokemon.name = PokeOtherFormes.formeName WHERE PokeOtherFormes.pokeName = ? LIMIT 0,6;");
				prep.setString(1, id);
				rs = prep.executeQuery();
				int otherFormesCount = 0;
				while (rs.next()) {
					otherFormes += (otherFormesCount++ != 0 ? ", " : "") + rs.getString("species");
				}

				String eggGroups = new String();
				prep = c.prepareStatement("SELECT eggGroup FROM PokeEggGroups WHERE pokeName = ?;");
				prep.setString(1, id);
				rs = prep.executeQuery();
				int eggGroupsCount = 0;
				while (rs.next()) {
					eggGroups += (eggGroupsCount++ != 0 ? ", " : "") + rs.getString("eggGroup");
				}

				// w/r
				prep = c.prepareStatement(
						"SELECT * FROM Types WHERE Type = (SELECT type1 FROM Pokemon WHERE name = ?);");
				prep.setString(1, id);
				rs = prep.executeQuery();
				ResultSetMetaData allTypes = rs.getMetaData();
				float[] damageTaken = new float[18];
				if (rs.next()) {
					for (int i = 0; i < 18; i++)
						damageTaken[i] = rs.getFloat(i + 2);
				}
				if (type2 != null) {
					prep = c.prepareStatement(
							"SELECT * FROM Types WHERE Type = (SELECT type2 FROM Pokemon WHERE name = ?);");
					prep.setString(1, id);
					rs = prep.executeQuery();
					if (rs.next()) {
						for (int i = 0; i < 18; i++) {
							damageTaken[i] *= rs.getFloat(i + 2);
						}
					}
				}

				String immuneDamage = new String(), quarterDamage = new String(), halfDamage = new String(),
						doubleDamage = new String(), quadDamage = new String();

				for (int i = 0; i < 18; i++) {
					if (Float.compare(damageTaken[i], 0f) == 0) {
						if (immuneDamage.isEmpty()) {
							immuneDamage += "*x0*: " + allTypes.getColumnName(i + 2);
						} else
							immuneDamage += ", " + allTypes.getColumnName(i + 2);
					} else if (Float.compare(damageTaken[i], 0.25f) == 0) {
						if (quarterDamage.isEmpty()) {
							quarterDamage += "*x1/4*: " + allTypes.getColumnName(i + 2);
						} else
							quarterDamage += ", " + allTypes.getColumnName(i + 2);
					} else if (Float.compare(damageTaken[i], 0.5f) == 0) {
						if (halfDamage.isEmpty()) {
							halfDamage += "*x1/2*: " + allTypes.getColumnName(i + 2);
						} else
							halfDamage += ", " + allTypes.getColumnName(i + 2);
					} else if (Float.compare(damageTaken[i], 2.0f) == 0) {
						if (doubleDamage.isEmpty()) {
							doubleDamage += "*x2*: " + allTypes.getColumnName(i + 2);
						} else
							doubleDamage += ", " + allTypes.getColumnName(i + 2);
					} else if (Float.compare(damageTaken[i], 4.0f) == 0) {
						if (quadDamage.isEmpty()) {
							quadDamage += "*x4*: " + allTypes.getColumnName(i + 2);
						} else
							quadDamage += ", " + allTypes.getColumnName(i + 2);
					}
				}

				String weakness_resistance = "Weaknesses/Resistances: ";
				ArrayList<String> wrList = new ArrayList<String>();
				if (!immuneDamage.isEmpty())
					wrList.add(immuneDamage);
				if (!quarterDamage.isEmpty())
					wrList.add(quarterDamage);
				if (!halfDamage.isEmpty())
					wrList.add(halfDamage);
				if (!doubleDamage.isEmpty())
					wrList.add(doubleDamage);
				if (!quadDamage.isEmpty())
					wrList.add(quadDamage);

				for (int i = 0; i < wrList.size(); i++) {
					if (i != 0)
						weakness_resistance += ", ";
					weakness_resistance += wrList.get(i);
				}

				int generation = getGeneration(Integer.parseInt(num));

				String serebiiLink = getSerebiiLink(Integer.parseInt(num));
				String smogonLink = smogonPokemonBegin + id + smogonEnd;

				String info = new String();
				info += species;
				info += (forme.isEmpty() ? "" : " | " + "Base Forme: " + forme);
				info += " | " + "Number: " + num + " (Gen " + generation + ")";
				info += " | " + "Type" + (type2 == null ? "" : "s") + ": " + type1 + (type2 == null ? "" : "/" + type2);
				info += " | " + "Stats: " + statInfo + ", BST: " + BST;
				info += " | " + "Abilit" + (abilitiesCount == 1 ? "y" : "ies") + ": " + abilities;
				info += (prevo.isEmpty() ? "" : " | " + "Pre-Evolution: " + prevo);
				info += (evos.isEmpty() ? "" : " | " + "Evolution" + (evosCount == 1 ? "" : "s") + ": " + evos);
				info += (otherFormes.isEmpty() ? ""
						: " | " + "Other Forme" + (otherFormesCount == 1 ? "" : "s") + ": " + otherFormes);
				info += (eggGroups.isEmpty() ? ""
						: " | " + "Egg Group" + (eggGroupsCount == 1 ? "" : "s") + ": " + eggGroups);
				info += " | " + weakness_resistance;
				info += " | " + serebiiLink + " | " + smogonLink;

				return info;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return "Database error";
		}

		return "Pokemon not found";

	}

	public String getPokeInfoByName(String input) {

		String id = getIdBySimilarity(input, allPokes);

		return getPokeInfoById(id);

	}

	public String getCommonEggGroupsByNames(String input1, String input2) {

		String names[] = new String[2];
		names[0] = getIdBySimilarity(input1, allPokes);
		names[1] = getIdBySimilarity(input2, allPokes);
		String[] species = new String[2];
		String[][] eggGroups = new String[2][2];
		int[] nums = new int[2];
		try {
			for (int i = 0; i < 2; i++) {
				prep = c.prepareStatement(
						"SELECT PokeEggGroups.eggGroup, Pokemon.species, Pokemon.num FROM PokeEggGroups JOIN Pokemon ON PokeEggGroups.pokeName = Pokemon.name WHERE pokeName = ?;");
				prep.setString(1, names[i]);
				rs = prep.executeQuery();
				if (rs.next()) {
					species[i] = rs.getString("species");
					eggGroups[i][0] = rs.getString("eggGroup");
					nums[i] = rs.getInt("num");
				}
				if (rs.next()) {
					eggGroups[i][1] = rs.getString("eggGroup");
				}
			}

			String commonEggGroups = new String();
			int commonCount = 0;
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 2; j++) {
					if (eggGroups[0][i] != null && eggGroups[1][j] != null && eggGroups[0][i].equals(eggGroups[1][j])) {
						if (commonCount != 0)
							commonEggGroups += ", ";
						commonEggGroups += eggGroups[0][i];
						commonCount++;
					}
				}
			}
			String eggInfo;
			if (commonCount == 0)
				eggInfo = "No common Egg Groups between " + species[0] + " and " + species[1];
			else
				eggInfo = "Common Egg Group" + (commonCount != 1 ? "s" : "") + " between " + species[0] + " and "
						+ species[1] + ": " + commonEggGroups;
			return eggInfo + " | " + getSerebiiLink(nums[0]) + " - " + getSerebiiLink(nums[1]);
		} catch (SQLException e) {
			e.printStackTrace();
			return "Database error";
		}

	}

	private String getSerebiiLink(int num) {
		return serebiiBegin + (num < 100 ? "0" : "") + (num < 10 ? "0" : "") + num + serebiiEnd;
	}

	public String getLearnInfoByNamesInGivenGeneration(String pokeInput, String moveInput, int generation) {
		String pokeName = getIdBySimilarity(pokeInput, allPokes);
		String moveId = getIdBySimilarity(moveInput, allMoves);

		String pokeSpecies = allPokes.get(pokeName);
		String moveName = allMoves.get(moveId);

		ArrayList<String> allLearnInfo = new ArrayList<String>();
		boolean onItsOwn = false;

		try {

			// Learn [baseForme] -> (can learn) -> check evos and prevos
			// Learn [baseForme] -> (can't learn) -> check evos and prevos ->
			// check other Formes
			// Learn [otherForme] -> (can learn) -> get Base Forme -> check evos
			// and prevos
			// Learn [otherForme] -> (can't learn) -> check Base Forme -> check
			// evos and prevos

			// check if the Pokemon is a Forme
			String baseSpecies = null;
			String baseName = null;
			prep = c.prepareStatement(
					"SELECT PokeFormes.baseSpecies, Pokemon.name FROM PokeFormes JOIN Pokemon ON PokeFormes.baseSpecies = Pokemon.species WHERE pokeName = ?");
			prep.setString(1, pokeName);
			rs = prep.executeQuery();
			if (rs.next()) {
				baseSpecies = rs.getString("baseSpecies");
				baseName = rs.getString("name");
			}

			// check if the given Pokemon can learn the move
			prep = c.prepareStatement(
					"SELECT Learnsets.learnInfo FROM Learnsets " + "WHERE pokeName = ? AND moveId = ?;");
			prep.setString(1, pokeName);
			prep.setString(2, moveId);
			rs = prep.executeQuery();

			// if it can learn the move
			if (rs.next()) {

				// see if it can learn it in the given generation
				String decodedInfo = decodeLearnInfo(rs.getString("learnInfo"), generation);
				if (decodedInfo != null) {
					onItsOwn = true;
					allLearnInfo.add(pokeSpecies + " learns the Move " + moveName + ": "
							+ decodeLearnInfo(rs.getString("learnInfo"), generation));
				}

				// if it's not a forme - case [baseForme] -> (can learn)
				String evosInfo;
				if (baseSpecies == null)
					// check evos
					evosInfo = checkEvosAndPrevos(pokeName, moveId, generation);
				// if it's a forme - case [otherForme] -> (can learn)
				else
					// check evos of base forme
					evosInfo = checkEvosAndPrevos(baseSpecies, moveId, generation);

				if (evosInfo != null)
					allLearnInfo.add(evosInfo);
			}
			// if it can't learn the move
			else {

				// if it's not a forme - [baseForme] -> (can't learn)
				if (baseSpecies == null) {

					// check evos and prevos
					String evosInfo = checkEvosAndPrevos(pokeName, moveId, generation);
					if (evosInfo != null)
						allLearnInfo.add(evosInfo);

					// check other Formes
					prep = c.prepareStatement("SELECT Learnsets.learnInfo, Pokemon.species FROM Learnsets "
							+ "JOIN PokeOtherFormes ON Learnsets.pokeName = PokeOtherFormes.formeName "
							+ "JOIN Pokemon ON Learnsets.pokeName = Pokemon.name "
							+ "WHERE PokeOtherFormes.pokeName = ? AND Learnsets.moveId = ?;");
					prep.setString(1, pokeName);
					prep.setString(2, moveId);
					rs = prep.executeQuery();
					// if an other forme can learn it
					while (rs.next()) {
						String decodedInfo = decodeLearnInfo(rs.getString("learnInfo"), generation);
						if (decodedInfo != null)
							allLearnInfo.add("Other Forme " + rs.getString("species") + " learns it: " + decodedInfo);
					}

				}

				// if it's a forme - case [otherForme] -> (can't learn)
				else {
					// check base forme
					prep = c.prepareStatement("SELECT Learnsets.learnInfo, Pokemon.species FROM Learnsets "
							+ "JOIN Pokemon ON Learnsets.pokeName = Pokemon.name "
							+ "WHERE Learnsets.pokeName = ? AND Learnsets.moveId = ?;");
					prep.setString(1, baseName);
					prep.setString(2, moveId);
					rs = prep.executeQuery();
					while (rs.next()) {
						String decodedInfo = decodeLearnInfo(rs.getString("learnInfo"), generation);
						if (decodedInfo != null)
							allLearnInfo.add("Other Forme " + rs.getString("species") + " learns it: " + decodedInfo);
					}

					// check evos and prevos of base forme
					String evosInfo = checkEvosAndPrevos(baseName, moveId, generation);
					if (evosInfo != null)
						allLearnInfo.add(evosInfo);
				}
			}

			int num = 0;
			prep = c.prepareStatement("SELECT num FROM Pokemon WHERE name = ?;");
			prep.setString(1, pokeName);
			rs = prep.executeQuery();
			if (rs.next()) {
				num = rs.getInt("num");
			}

			String inGeneration = "In Generation " + generation + ", ";
			if (allLearnInfo.isEmpty())
				return inGeneration + pokeSpecies + " can't learn " + moveName + " | " + getSerebiiLink(num);
			else {
				String formattedInfo = inGeneration;
				if (!onItsOwn)
					formattedInfo += pokeSpecies + " can't learn " + moveName + " on its own. ";
				for (int i = 0; i < allLearnInfo.size(); i++) {
					if (i != 0)
						formattedInfo += ". ";
					formattedInfo += allLearnInfo.get(i);
				}
				return formattedInfo + " | " + getSerebiiLink(num);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return "Database error";
		}

	}

	private String checkEvosAndPrevos(String targetName, String moveId, int generation) throws SQLException {

		ArrayList<String> evosInfo = new ArrayList<String>();

		// get Prevo info - this method works even if the pre evo can't learn
		// the move, but the pre evos' pre evo can
		String firstPrevoSpecies = null;
		prep = c.prepareStatement("SELECT prevoName " + "FROM PokePrevos " + "WHERE PokePrevos.pokeName = ?;");
		prep.setString(1, targetName);
		rs = prep.executeQuery();
		if (rs.next())
			firstPrevoSpecies = rs.getString("prevoName");

		prep = c.prepareStatement("SELECT Learnsets.learnInfo, Pokemon.Species, pokePrevos.prevoName FROM Learnsets "
				+ "JOIN PokePrevos ON Learnsets.pokeName = PokePrevos.prevoName "
				+ "JOIN Pokemon ON Learnsets.pokeName = Pokemon.name "
				+ "WHERE (PokePrevos.pokeName = ? OR PokePrevos.pokeName = ?) AND Learnsets.moveId = ?;");
		prep.setString(1, targetName);
		prep.setString(2, firstPrevoSpecies);
		prep.setString(3, moveId);
		rs = prep.executeQuery();
		if (rs.next()) {
			firstPrevoSpecies = rs.getString("Species");

			String decodedInfo = decodeLearnInfo(rs.getString("learnInfo"), generation);
			if (decodedInfo != null)
				evosInfo.add("Pre-Evolution " + firstPrevoSpecies + " learns it: " + decodedInfo);
		}

		// get Evo info
		String firstEvoSpecies = null;
		prep = c.prepareStatement("SELECT evoName " + "FROM PokeEvos " + "WHERE PokeEvos.pokeName = ?;");
		prep.setString(1, targetName);
		rs = prep.executeQuery();
		if (rs.next())
			firstEvoSpecies = rs.getString("evoName");

		prep = c.prepareStatement("SELECT Learnsets.learnInfo, Pokemon.Species, pokeEvos.evoName FROM Learnsets "
				+ "JOIN PokeEvos ON Learnsets.pokeName = PokeEvos.evoName "
				+ "JOIN Pokemon ON Learnsets.pokeName = Pokemon.name "
				+ "WHERE (PokeEvos.pokeName = ? OR PokeEvos.pokeName = ?) AND Learnsets.moveId = ?;");
		prep.setString(1, targetName);
		prep.setString(2, firstEvoSpecies);
		prep.setString(3, moveId);
		rs = prep.executeQuery();
		if (rs.next()) {
			firstPrevoSpecies = rs.getString("Species");

			String decodedInfo = decodeLearnInfo(rs.getString("learnInfo"), generation);
			if (decodedInfo != null)
				evosInfo.add("Evolution " + firstPrevoSpecies + " learns it: " + decodedInfo);
		}

		if (evosInfo.isEmpty())
			return null;
		else {
			String formattedInfo = new String();
			for (int i = 0; i < evosInfo.size(); i++) {
				if (i != 0)
					formattedInfo += ". ";
				formattedInfo += evosInfo.get(i);
			}

			return formattedInfo;
		}

	}

	private String decodeLearnInfo(String learnInfo, int generation) {

		// E = Egg, T = Tutor, L = Level, S = Event, M = TM

		String[] individualInfo = learnInfo.split(",");
		// String formattedInfo = new String();
		int learnCount = 0;
		boolean learnsByEvent = false;
		ArrayList<String> formattedAllLearns = new ArrayList<String>();
		String formattedInfo = new String();
		for (int i = 0; i < individualInfo.length; i++) {
			if (individualInfo[i].startsWith(Integer.toString(generation))) {

				if (individualInfo[i].contains("L"))
					formattedAllLearns.add("at level " + individualInfo[i].split("L")[1]);
				else if (individualInfo[i].contains("M"))
					formattedAllLearns.add("by TM/HM");
				else if (individualInfo[i].contains("E"))
					formattedAllLearns.add("as an Egg Move");
				else if (individualInfo[i].contains("T"))
					formattedAllLearns.add("via Move Tutor");
				else if (individualInfo[i].contains("S")) {
					if (!learnsByEvent) {
						formattedAllLearns.add("by event");
						learnsByEvent = true;
					}
				}

				learnCount++;
			}
		}
		if (learnCount == 0)
			return null;

		// the method below avoids the text being ugle if the pokemon learns the
		// move by more than 1 events
		for (int i = 0; i < formattedAllLearns.size(); i++) {
			if (i != 0)
				formattedInfo += ", ";
			formattedInfo += formattedAllLearns.get(i);
		}
		return formattedInfo;
	}

	private int getGeneration(int num) {
		if (num <= 151)
			return 1;
		else if (num <= 251)
			return 2;
		else if (num <= 386)
			return 3;
		else if (num <= 493)
			return 4;
		else if (num <= 649)
			return 5;
		else if (num <= 721)
			return 6;
		else
			return 7;
	}

	private void openDatabase() throws SQLException, ClassNotFoundException {
		System.out.println("Opening database...");
		Class.forName("org.sqlite.JDBC");
		c = DriverManager.getConnection("jdbc:sqlite:poke.db");
		c.createStatement();
		System.out.println("Opened database successfully");
	}

	// private void closeConnection() throws SQLException {
	//
	// stmt.close();
	// c.close();
	//
	// }

	private void createSimilarityMaps() throws SQLException {

		// Pokemon
		prep = c.prepareStatement("SELECT name, species FROM Pokemon;");
		rs = prep.executeQuery();
		while (rs.next())
			allPokes.put(rs.getString("name"), rs.getString("species"));

		// Abilities
		prep = c.prepareStatement("SELECT id, name FROM Abilities;");
		rs = prep.executeQuery();
		while (rs.next())
			allAbilities.put(rs.getString("id"), rs.getString("name"));

		// Moves
		prep = c.prepareStatement("SELECT id, name FROM Moves;");
		rs = prep.executeQuery();
		while (rs.next())
			allMoves.put(rs.getString("id"), rs.getString("name"));

		// Items
		prep = c.prepareStatement("SELECT id, name FROM Items;");
		rs = prep.executeQuery();
		while (rs.next())
			allItems.put(rs.getString("id"), rs.getString("name"));

	}

	public PS_SQLiteSelector() {

		try {
			openDatabase();

			createSimilarityMaps();

			// closeConnection();

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

	}

}
