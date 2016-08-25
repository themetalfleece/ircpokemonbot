/**
 * 
 */
package com.themetalfleece.pokemondb;

/**
*
* Created by themetalfleece at 16 Jun 2016
*
*/

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class PS_SQLiteGenerator {

	private PreparedStatement prep;
	private String sql;
	private Connection c = null;
	private Statement stmt = null;

	private void createDB() throws SQLException {

		try {

			/* OPEN THE FILES */
			System.out.println("Opening the files");
			BufferedReader pokemonReader = new BufferedReader(new FileReader("psdb/pokedex.js"));
			BufferedReader typesReader = new BufferedReader(new FileReader("psdb/typechart.js"));
			BufferedReader abilitiesReader = new BufferedReader(new FileReader("psdb/abilities.js"));
			BufferedReader movesReader = new BufferedReader(new FileReader("psdb/moves.js"));
			BufferedReader itemsReader = new BufferedReader(new FileReader("psdb/items.js"));
			BufferedReader learnsetsReader = new BufferedReader(new FileReader("psdb/learnsets.js"));
			System.out.println("Files opened successfully");

			// DB drop tables if already exit
			sql = "DROP TABLE IF EXISTS Types;";
			stmt.executeUpdate(sql);
			sql = "DROP TABLE IF EXISTS Abilities;";
			stmt.executeUpdate(sql);
			sql = "DROP TABLE IF EXISTS Pokemon;";
			stmt.executeUpdate(sql);
			sql = "DROP TABLE IF EXISTS Moves;";
			stmt.executeUpdate(sql);
			sql = "DROP TABLE IF EXISTS Items;";
			stmt.executeUpdate(sql);
			sql = "DROP TABLE IF EXISTS pokeAbilities;";
			stmt.executeUpdate(sql);
			sql = "DROP TABLE IF EXISTS pokeFormes;";
			stmt.executeUpdate(sql);
			sql = "DROP TABLE IF EXISTS pokeOtherFormes;";
			stmt.executeUpdate(sql);
			sql = "DROP TABLE IF EXISTS pokeEvos;";
			stmt.executeUpdate(sql);
			sql = "DROP TABLE IF EXISTS pokePrevos;";
			stmt.executeUpdate(sql);
			sql = "DROP TABLE IF EXISTS pokeEggGroups;";
			stmt.executeUpdate(sql);
			sql = "DROP TABLE IF EXISTS Learnsets;";
			stmt.executeUpdate(sql);

			// DB Create Tables
			System.out.println("Creating Tables...");

			sql = "CREATE TABLE IF NOT EXISTS Types " + "(type VARCHAR(10), " + "Bug FLOAT, " + "Dark FLOAT, "
					+ "Dragon FLOAT, " + "Electric FLOAT, " + "Fairy FLOAT, " + "Fighting FLOAT, " + "Fire FLOAT, "
					+ "Flying FLOAT, " + "Ghost FLOAT, " + "Grass FLOAT, " + "Ground FLOAT, " + "Ice FLOAT, "
					+ "Normal FLOAT, " + "Poison FLOAT, " + "Psychic FLOAT, " + "Rock FLOAT, " + "Steel FLOAT, "
					+ "Water FLOAT, " + "PRIMARY KEY (Type)" + ");";
			stmt.executeUpdate(sql);

			sql = "CREATE TABLE IF NOT EXISTS Abilities " + "(id VARCHAR(30), " + "name VARCHAR(30), "
					+ "shortDesc TEXT(100), " + "rating FLOAT DEFAULT -3, " + "PRIMARY KEY (id)" + ");";
			stmt.executeUpdate(sql);

			sql = "CREATE TABLE IF NOT EXISTS Pokemon " + "(name VARCHAR(30), " + "num INT(3), "
					+ "species VARCHAR(30), " + "type1 VARCHAR(10) REFERENCES Types(type), "
					+ "type2 VARCHAR(10) REFERENCES Types(type), " + "hp INT(3), " + "atk INT(3), " + "def INT(3), "
					+ "spa INT(3), " + "spd INT(3), " + "spe INT(3), " + "PRIMARY KEY (name)" + ");";
			stmt.executeUpdate(sql);

			sql = "CREATE TABLE IF NOT EXISTS Moves " + "(id VARCHAR(30), " + "name VARCHAR(30), "
					+ "type VARCHAR(10) REFERENCES Types(type), " + "accuracy INT(3), " + "basePower INT(3), "
					+ "category VARCHAR(10), " + "shortDesc TEXT(100), " + "pp INT(2), " + "priority INT(2), "
					+ "target VARCHAR(20), " + "PRIMARY KEY (id)" + ");";
			stmt.executeUpdate(sql);

			sql = "CREATE TABLE IF NOT EXISTS Items " + "(id VARCHAR(30), " + "name VARCHAR(30), " + "desc TEXT(100), "
					+ "gen INT (1), " + "PRIMARY KEY (id)" + ");";
			stmt.executeUpdate(sql);

			sql = "CREATE TABLE IF NOT EXISTS PokeAbilities " + "(pokeName VARCHAR(30) REFERENCES Pokemon(name), "
					+ "slot CHAR(1), " + "abilityName VARCHAR(30) REFERENCES Abilities(id), "
					+ "PRIMARY KEY (pokeName, slot)" + ");";
			stmt.executeUpdate(sql);

			sql = "CREATE TABLE IF NOT EXISTS PokeFormes " + "(pokeName VARCHAR(30) REFERENCES Pokemon(name), "
					+ "forme VARCHAR(15), " + "baseSpecies VARCHAR(30) REFERENCES Pokemon(name), "
					+ "PRIMARY KEY (pokeName, forme)" + ");";
			stmt.executeUpdate(sql);

			sql = "CREATE TABLE IF NOT EXISTS PokeOtherFormes " + "(pokeName VARCHAR(30) REFERENCES Pokemon(name), "
					+ "formeName VARCHAR(30) REFERENCES Pokemon(name), " + "PRIMARY KEY (pokeName, formeName)" + ");";
			stmt.executeUpdate(sql);

			sql = "CREATE TABLE IF NOT EXISTS PokeEvos " + "(pokeName VARCHAR(30) REFERENCES Pokemon(name), "
					+ "evoName VARCHAR(30) REFERENCES Pokemon(name), " + "PRIMARY KEY (pokeName, evoName)" + ");";
			stmt.executeUpdate(sql);

			sql = "CREATE TABLE IF NOT EXISTS PokePrevos " + "(pokeName VARCHAR(30) REFERENCES Pokemon(name), "
					+ "PrevoName VARCHAR(30) REFERENCES Pokemon(name), " + "PRIMARY KEY (pokeName, PrevoName)" + ");";
			stmt.executeUpdate(sql);

			sql = "CREATE TABLE IF NOT EXISTS PokeEggGroups " + "(pokeName VARCHAR(30) REFERENCES Pokemon(name), "
					+ "eggGroup VARCHAR(30) , " + "PRIMARY KEY (pokeName, eggGroup)" + ");";
			stmt.executeUpdate(sql);

			sql = "CREATE TABLE Learnsets " + "(pokeName VARCHAR(30) REFERENCES Pokemon(name), "
					+ "moveId VARCHAR(30) REFERENCES Moves(id), " + "learnInfo TEXT(100), "
					+ "PRIMARY KEY (pokeName, moveId)" + ");";
			stmt.executeUpdate(sql);

			System.out.println("Tables created successfully");

			// Types
			System.out.println("Inserting values into Types...");

			int indent = 0;
			String Type = null;

			String line = typesReader.readLine();
			c.setAutoCommit(false);

			while (!line.contains("};")) {

				if (indent == 1 && line.contains(":")) {
					Type = getLineId(line);
					prep = c.prepareStatement("INSERT INTO Types (type) VALUES (?);");
					prep.setString(1, Type);
					prep.addBatch();
					prep.executeBatch();
				} else if (indent == 3) {
					// make sure it's a type (not sandstorm etc)
					if (line.contains("\"")) {
						prep = c.prepareStatement(
								"UPDATE Types SET " + getValueInsideFirstQuote(line) + " = ? WHERE Type = ?;");
						float converted = 0;
						switch (Integer.parseInt(getValueWithoutQuote(line))) {
						case 0:
							converted = 1f;
							break;
						case 1:
							converted = 2f;
							break;
						case 2:
							converted = 0.5f;
							break;
						case 3:
							converted = 0f;
							break;
						}
						prep.setFloat(1, converted);
						prep.setString(2, Type);
						prep.addBatch();
						prep.executeBatch();
					}
				}

				indent = alterIndent(indent, line);

				line = typesReader.readLine();
			}
			c.commit();
			System.out.println("Types completed successfully");
			typesReader.close();

			// Abilities
			System.out.println("Inserting values into Abilities...");

			indent = 0;
			String id = null;

			line = abilitiesReader.readLine();

			while (line != null && !line.contains("// CAP")) {

				if (indent == 1 && line.contains(":")) {
					id = getLineId(line);
					prep = c.prepareStatement("INSERT INTO Abilities (id) VALUES (?);");
					prep.setString(1, id);
					prep.addBatch();
					prep.executeBatch();
				} else if (indent == 2) {
					if (line.contains("shortDesc:")) {
						prep = c.prepareStatement("UPDATE Abilities SET shortDesc = ? WHERE id = ?;");
						prep.setString(1, getValueInsideFirstQuote(line));
						prep.setString(2, id);
						prep.addBatch();
						prep.executeBatch();
					} else if (line.contains("name:")) {
						prep = c.prepareStatement("UPDATE Abilities SET name = ? WHERE id = ?;");
						prep.setString(1, getValueInsideFirstQuote(line));
						prep.setString(2, id);
						prep.addBatch();
						prep.executeBatch();
					} else if (line.contains("rating:")) {
						prep = c.prepareStatement("UPDATE Abilities SET rating = ? WHERE id = ?;");
						prep.setFloat(1, Float.parseFloat(getValueWithoutQuote(line)));
						prep.setString(2, id);
						prep.addBatch();
						prep.executeBatch();
					}
				}

				indent = alterIndent(indent, line);

				line = abilitiesReader.readLine();
			}
			c.commit();
			System.out.println("Abilities completed successfully");
			abilitiesReader.close();

			// Pokemon
			System.out.println("Inserting values into Pokemon...");

			indent = 0;
			String name = null;

			int formeCounter = 0; // when it's 2, insert forme info
			String baseSpecies = null, forme = null;

			line = pokemonReader.readLine();

			while (line != null && !line.contains("missingno")) {

				if (indent == 1 && line.contains(":")) {
					name = getLineId(line);
					prep = c.prepareStatement("INSERT INTO Pokemon (name) VALUES (?);");
					prep.setString(1, name);
					prep.addBatch();
					prep.executeBatch();
					formeCounter = 0;
				} else if (indent == 2) {
					if (line.contains("num:")) {
						prep = c.prepareStatement("UPDATE Pokemon SET num = ? WHERE name = ?;");
						prep.setInt(1, Integer.parseInt(getValueWithoutQuote(line)));
						prep.setString(2, name);
						prep.addBatch();
						prep.executeBatch();
					} else if (line.contains("species:")) {
						String species = getValueInsideFirstQuote(line);

						// convert X-Mega to Mega X
						if (species.contains("-Mega")) {
							String[] splitted = species.split("-");
							species = splitted[1] + " " + splitted[0];
							// check for -X/-Y eg. Mega Charizard Y
							if (splitted.length == 3)
								species += " " + splitted[2];
						}

						prep = c.prepareStatement("UPDATE Pokemon SET species = ? WHERE name = ?;");
						prep.setString(1, species);
						prep.setString(2, name);
						prep.addBatch();
						prep.executeBatch();
					} else if (line.contains("types:")) {
						String[] types = getValuesInQuotes(line);
						prep = c.prepareStatement("UPDATE Pokemon SET type1 = ? WHERE name = ?;");
						prep.setString(1, types[0]);
						prep.setString(2, name);
						prep.addBatch();
						prep.executeBatch();
						if (types.length == 2) {
							prep = c.prepareStatement("UPDATE Pokemon SET type2 = ? WHERE name = ?;");
							prep.setString(1, types[1]);
							prep.setString(2, name);
							prep.addBatch();
							prep.executeBatch();
						}
					} else if (line.contains("baseStats:")) {
						String[] stats = getValuesWithIndex(line);
						for (int i = 0; i < stats.length; i += 2) {
							prep = c.prepareStatement("UPDATE Pokemon SET " + stats[i] + " = ? WHERE name = ?;");
							prep.setInt(1, Integer.parseInt(stats[i + 1]));
							prep.setString(2, name);
							prep.addBatch();
							prep.executeBatch();
						}
					} else if (line.contains("abilities:")) {
						String[] abilities = getValuesWithIndex(line);
						for (int i = 0; i < abilities.length; i += 2) {
							prep = c.prepareStatement(
									"INSERT INTO PokeAbilities (pokeName, slot, abilityName) VALUES (?, ?, ?);");
							prep.setString(1, name);
							prep.setString(2, abilities[i]);
							prep.setString(3, abilities[i + 1]);
							prep.addBatch();
							prep.executeBatch();
						}
					} else if (line.contains("forme:")) {
						formeCounter++;
						forme = getValueInsideFirstQuote(line);
					} else if (line.contains("baseSpecies:")) {
						formeCounter++;
						baseSpecies = getValueInsideFirstQuote(line);
					} else if (line.contains("otherFormes:")) {
						String[] formes = getValuesInQuotes(line);
						for (int i = 0; i < formes.length; i++) {
							prep = c.prepareStatement(
									"INSERT INTO PokeOtherFormes (pokeName, formeName) VALUES (?, ?);");
							prep.setString(1, name);
							prep.setString(2, formes[i]);
							prep.addBatch();
							prep.executeBatch();
						}
					} else if (line.contains("otherFormes:")) {
						String[] formes = getValuesInQuotes(line);
						for (int i = 0; i < formes.length; i++) {
							prep = c.prepareStatement(
									"INSERT INTO PokeOtherFormes (pokeName, formeName) VALUES (?, ?);");
							prep.setString(1, name);
							prep.setString(2, formes[i]);
							prep.addBatch();
							prep.executeBatch();
						}
					} else if (line.contains("evos:")) {
						String[] evos = getValuesInQuotes(line);
						for (int i = 0; i < evos.length; i++) {
							prep = c.prepareStatement("INSERT INTO PokeEvos (pokeName, evoName) VALUES (?, ?);");
							prep.setString(1, name);
							prep.setString(2, evos[i]);
							prep.addBatch();
							prep.executeBatch();
						}
					} else if (line.contains("prevo:")) {
						String prevo = getValueInsideFirstQuote(line);
						prep = c.prepareStatement("INSERT INTO PokePrevos (pokeName, prevoName) VALUES (?, ?);");
						prep.setString(1, name);
						prep.setString(2, prevo);
						prep.addBatch();
						prep.executeBatch();
					} else if (line.contains("eggGroups:")) {
						String[] eggGroups = getValuesInQuotes(line);
						for (int i = 0; i < eggGroups.length; i++) {
							prep = c.prepareStatement("INSERT INTO PokeEggGroups (pokeName, eggGroup) VALUES (?, ?);");
							prep.setString(1, name);
							prep.setString(2, eggGroups[i]);
							prep.addBatch();
							prep.executeBatch();
						}
					}

					// is all forme data is collected, insert it and reset the
					// counter
					if (formeCounter == 2) {
						prep = c.prepareStatement(
								"INSERT INTO PokeFormes (pokeName, forme, baseSpecies) VALUES (?, ?, ?);");
						prep.setString(1, name);
						prep.setString(2, forme);
						prep.setString(3, baseSpecies);
						prep.addBatch();
						prep.executeBatch();
						formeCounter = 0;
					}

				}

				indent = alterIndent(indent, line);

				line = pokemonReader.readLine();
			}
			c.commit();
			System.out.println("Pokemon completed successfully");
			pokemonReader.close();

			// Moves
			System.out.println("Inserting values into Moves...");

			indent = 0;
			id = null;

			line = movesReader.readLine();
			boolean endOfStandard = false;

			while (line != null && !endOfStandard) {

				if (indent == 1 && line.contains(":")) {
					id = getLineId(line);
					prep = c.prepareStatement("INSERT INTO Moves (id) VALUES (?);");
					prep.setString(1, id);
					prep.addBatch();
					prep.executeBatch();
				} else if (indent == 2) {
					if (line.contains("shortDesc:")) {
						prep = c.prepareStatement("UPDATE Moves SET shortDesc = ? WHERE id = ?;");
						prep.setString(1, getValueInsideFirstQuote(line));
						prep.setString(2, id);
						prep.addBatch();
						prep.executeBatch();
					} else if (line.contains("name:")) {
						prep = c.prepareStatement("UPDATE Moves SET name = ? WHERE id = ?;");
						prep.setString(1, getValueInsideFirstQuote(line));
						prep.setString(2, id);
						prep.addBatch();
						prep.executeBatch();
					} else if (line.contains("type:")) {
						prep = c.prepareStatement("UPDATE Moves SET type = ? WHERE id = ?;");
						prep.setString(1, getValueInsideFirstQuote(line));
						prep.setString(2, id);
						prep.addBatch();
						prep.executeBatch();
					} else if (line.contains("accuracy:")) {
						prep = c.prepareStatement("UPDATE Moves SET accuracy = ? WHERE id = ?;");
						String accuracy = getValueWithoutQuote(line);
						int finalAccuracy;
						// check if accuracy is a number. If it's "true" it'll
						// always hit, so I represent it as the integer 0
						if (accuracy.matches("^-?\\d+$"))
							finalAccuracy = Integer.parseInt(accuracy);
						else
							finalAccuracy = 0;
						prep.setInt(1, finalAccuracy);
						prep.setString(2, id);
						prep.addBatch();
						prep.executeBatch();
					} else if (line.contains("basePower:")) {
						prep = c.prepareStatement("UPDATE Moves SET basePower = ? WHERE id = ?;");
						prep.setInt(1, Integer.parseInt(getValueWithoutQuote(line)));
						prep.setString(2, id);
						prep.addBatch();
						prep.executeBatch();
					} else if (line.contains("pp:")) {
						prep = c.prepareStatement("UPDATE Moves SET pp = ? WHERE id = ?;");
						prep.setInt(1, Integer.parseInt(getValueWithoutQuote(line)));
						prep.setString(2, id);
						prep.addBatch();
						prep.executeBatch();
					} else if (line.contains("priority:")) {
						prep = c.prepareStatement("UPDATE Moves SET priority = ? WHERE id = ?;");
						prep.setInt(1, Integer.parseInt(getValueWithoutQuote(line)));
						prep.setString(2, id);
						prep.addBatch();
						prep.executeBatch();
					} else if (line.contains("category:")) {
						prep = c.prepareStatement("UPDATE Moves SET category = ? WHERE id = ?;");
						prep.setString(1, getValueInsideFirstQuote(line));
						prep.setString(2, id);
						prep.addBatch();
						prep.executeBatch();
					} else if (line.contains("target:")) {
						prep = c.prepareStatement("UPDATE Moves SET target = ? WHERE id = ?;");
						prep.setString(1, getValueInsideFirstQuote(line));
						prep.setString(2, id);
						prep.addBatch();
						prep.executeBatch();
					}

					// check isNonstandard:, if so, remove it and terminate
					else if (line.contains("isNonstandard:")) {
						if (getValueWithoutQuote(line).equals("true")) {
							prep = c.prepareStatement("DELETE FROM moves WHERE id = ?;");
							prep.setString(1, id);
							prep.addBatch();
							prep.executeBatch();
							endOfStandard = true;
						}
					}
				}

				indent = alterIndent(indent, line);

				line = movesReader.readLine();
			}
			c.commit();
			System.out.println("Moves completed successfully");
			movesReader.close();

			// Items
			System.out.println("Inserting values into Items...");

			indent = 0;
			id = null;

			line = itemsReader.readLine();

			while (line != null && !line.contains("// CAP")) {

				if (indent == 1 && line.contains(":")) {
					id = getLineId(line);
					prep = c.prepareStatement("INSERT INTO Items (id) VALUES (?);");
					prep.setString(1, id);
					prep.addBatch();
					prep.executeBatch();
				} else if (indent == 2) {
					if (line.contains("desc:")) {
						prep = c.prepareStatement("UPDATE Items SET desc = ? WHERE id = ?;");
						prep.setString(1, getValueInsideFirstQuote(line));
						prep.setString(2, id);
						prep.addBatch();
						prep.executeBatch();
					} else if (line.contains("name:")) {
						prep = c.prepareStatement("UPDATE Items SET name = ? WHERE id = ?;");
						prep.setString(1, getValueInsideFirstQuote(line));
						prep.setString(2, id);
						prep.addBatch();
						prep.executeBatch();
					} else if (line.contains("gen:")) {
						prep = c.prepareStatement("UPDATE Items SET gen = ? WHERE id = ?;");
						prep.setInt(1, Integer.parseInt(getValueWithoutQuote(line)));
						prep.setString(2, id);
						prep.addBatch();
						prep.executeBatch();
					}
				}

				indent = alterIndent(indent, line);

				line = itemsReader.readLine();
			}
			c.commit();
			System.out.println("Items completed successfully");
			itemsReader.close();

			// Learnsets
			System.out.println("Inserting values into Learnsets...");

			indent = 0;
			String pokeName = null;
			String moveId = null;
			String learnInfo = null;

			line = learnsetsReader.readLine();

			while (line != null) {

				if (indent == 1 && line.contains(":")) {
					pokeName = getLineId(line);
				} else if (indent == 3 && line.contains(":")) {
					moveId = getLineId(line);

					String[] allInfoValues = getValuesInQuotes(line);
					learnInfo = new String();
					for (int i = 0; i < allInfoValues.length; i++) {
						if (i != 0)
							learnInfo += ",";
						learnInfo += allInfoValues[i];
					}

					prep = c.prepareStatement(
							"INSERT INTO Learnsets(pokeName, moveId, learnInfo) SELECT ?, ?, ? WHERE EXISTS (SELECT * FROM Pokemon WHERE name = ?);");
					prep.setString(1, pokeName);
					prep.setString(2, moveId);
					prep.setString(3, learnInfo);
					prep.setString(4, pokeName);
					prep.addBatch();
					prep.executeBatch();

				}

				indent = alterIndent(indent, line);

				line = learnsetsReader.readLine();
			}
			c.commit();
			System.out.println("Learnsets completed successfully");
			learnsetsReader.close();

			System.out.println("Database created successfully!");

		} catch (IOException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	private int alterIndent(int indent, String line) {
		for (char c : line.toCharArray()) {
			if (c == '{')
				indent++;
			else if (c == '}')
				indent--;
		}

		return indent;
	}

	// PS DB mining functions
	private String getValueWithoutQuote(String line) {
		return line.split(":")[1].trim().replace(",", "");
	}

	// example -> id: "zenmode", returns zenmode
	// example -> "Bug": 2, returns Bug
	private String getValueInsideFirstQuote(String line) {
		return line.split("\"")[1];
	}

	// example -> whimsicott: { returns whimsicott
	private String getLineId(String line) {
		return line.split(":")[0].replace("\"", "").trim();
	}

	// example -> types: ["Grass", "Fairy"], returns [0]Grass [1]Fairy
	private String[] getValuesInQuotes(String line) {
		String inside[] = line.split(":")[1].trim().replace("\"", "").replace("]", "").replace("[", "").split(",");
		String[] values = new String[inside.length];
		for (int i = 0; i < values.length; i++)
			values[i] = inside[i].replace(",", "").trim();
		return values;

	}

	// example -> baseStats: {hp: 60, atk: 67, def: 85, spa: 77, spd: 75, spe:
	// 116}, returns [0]hp [1]60 [2]atk [3]67 etc
	private String[] getValuesWithIndex(String line) {
		String inside = line.split("\\{")[1].split("\\}")[0];
		String[] eachPart = inside.split(",");
		String[] indexAndValues = new String[2 * eachPart.length];
		for (int i = 0; i < indexAndValues.length; i += 2) {
			indexAndValues[i] = eachPart[i / 2].split(":")[0].trim().replace("\"", "");
			// indexAndAbility[i+1] = eachPart[i/2].split("\"")[1];
			indexAndValues[i + 1] = eachPart[i / 2].split(": ")[1].trim().replace("\"", "");
		}

		return indexAndValues;

	}

	private void openDatabase() throws SQLException, ClassNotFoundException {
		System.out.println("Opening database...");
		Class.forName("org.sqlite.JDBC");
		c = DriverManager.getConnection("jdbc:sqlite:poke.db");
		stmt = c.createStatement();
		System.out.println("Opened database successfully");
	}

	private void closeConnection() throws SQLException {

		stmt.close();
		c.close();

	}

	public PS_SQLiteGenerator() {

		try {

			openDatabase();
			createDB();
			closeConnection();

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}

	}
}