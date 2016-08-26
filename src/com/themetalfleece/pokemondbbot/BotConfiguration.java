/**
 * 
 */
package com.themetalfleece.pokemondbbot;

import java.io.File;
import java.io.IOException;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;

/**
 *
 * Created by themetalfleece at 24 Aug 2016
 *
 */

public class BotConfiguration {

	private Ini ini;

	// twitch
	String channel;
	String botName;
	String serverHostname;
	int serverPort;
	String serverPassword;

	// pokemon
	int defaultGen;
	boolean modOnly;
	long cooldownMillis;
	String commandData;
	String commandEgg;
	String commandLearn;
	String commandCommands;
	String commandInfo;
	boolean whispersEnabled;

	public BotConfiguration() {

		try {
			ini = new Ini(new File("config.ini"));

			channel = ini.get("twitch", "channel").toString();
			botName = ini.get("twitch", "botName").toString();
			serverHostname = getValueWithDefault("twitch", "serverHostname", "irc.chat.twitch.tv");
			serverPort = Integer.parseInt(getValueWithDefault("twitch", "serverPort", "6667"));
			serverPassword = ini.get("twitch", "serverPassword").toString();

			defaultGen = Integer.parseInt(getValueWithDefault("pokemon", "defaultGen", "6"));
			modOnly = getValueWithDefault("pokemon", "cooldownMillis", "f").charAt(0) == 't';
			cooldownMillis = Long.parseLong(getValueWithDefault("pokemon", "cooldownMillis", "15000"));
			commandData = getValueWithDefault("pokemon", "data", "!data");
			commandEgg = getValueWithDefault("pokemon", "egg", "!egg");
			commandLearn = getValueWithDefault("pokemon", "learn", "!learn");
			commandCommands = getValueWithDefault("pokemon", "commands", "!commands");
			commandInfo = getValueWithDefault("pokemon", "info", "!info");
			whispersEnabled = getValueWithDefault("pokemon", "whispersEnabled", "t").charAt(0) == 't';

		} catch (InvalidFileFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getValueWithDefault(String key, String index, String defaultValue) {

		String value;

		try {
			value = ini.get(key, index).toString();
			if (value.isEmpty()) {
				throw new IniValueEmptyException();
			}
		} catch (java.lang.NullPointerException | IniValueEmptyException e) {
			System.out.println("Using default value (" + defaultValue + ") for " + key + "." + index);
			value = defaultValue;
		}

		return value;

	}

	class IniValueEmptyException extends Exception {
		private static final long serialVersionUID = -2231481522742091101L;
	}

}
