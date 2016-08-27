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

	Ini ini;

	// connection
	String channel;
	String botName;
	String serverHostname;
	int serverPort;
	String serverPassword;

	// functionality
	int defaultGen;
	boolean modOnly;
	long cooldownMillis;
	String commandData;
	String commandEgg;
	String commandLearn;
	String commandCommands;
	String commandInfo;
	boolean whispersEnabled;
	
	public void assignValues() {
			
			channel = getValueWithDefault("twitch", "channel", "#channelname");
			botName = getValueWithDefault("twitch", "botName", "botname");
			serverHostname = getValueWithDefault("twitch", "serverHostname", "irc.chat.twitch.tv");
			serverPort = Integer.parseInt(getValueWithDefault("twitch", "serverPort", "6667"));
			serverPassword = getValueWithDefault("twitch", "serverPassword", "oauth:xxx");

			defaultGen = Integer.parseInt(getValueWithDefault("pokemon", "defaultGen", "6"));
			modOnly = getValueWithDefault("pokemon", "modOnly", "f").charAt(0) == 't';
			cooldownMillis = Long.parseLong(getValueWithDefault("pokemon", "cooldownMillis", "15000"));
			commandData = getValueWithDefault("pokemon", "data", "!data");
			commandEgg = getValueWithDefault("pokemon", "egg", "!egg");
			commandLearn = getValueWithDefault("pokemon", "learn", "!learn");
			commandCommands = getValueWithDefault("pokemon", "commands", "!commands");
			commandInfo = getValueWithDefault("pokemon", "info", "!info");
			whispersEnabled = getValueWithDefault("pokemon", "whispersEnabled", "t").charAt(0) == 't';


	}

	public BotConfiguration() {

		try {
//			String className = this.getClass().getName().replace('.', '/');
//			String classJar =  this.getClass().getResource("/" + className + ".class").toString();
//			if (classJar.startsWith("jar:")) 
//				ini = new Ini(getClass().getResourceAsStream("/config.ini"));
//			else
			ini = new Ini(new File("./config.ini"));
			
			assignValues();

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
