/**
 * 
 */
package com.themetalfleece.pokemondbbot;

import java.io.IOException;
import java.util.HashMap;

import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.UnknownEvent;

import com.themetalfleece.pokemondb.PS_SQLiteSelector;

/**
 *
 * Created by themetalfleece at 24 Aug 2016
 *
 */
public class PokeTwitchChatBot extends GenericBot {

	private PS_SQLiteSelector selector;
	private BotConfiguration botConfig;

	private HashMap<String, Long> cooldown = new HashMap<String, Long>();

	// messages in chat
	@Override
	public void onMessage(MessageEvent event) {

		// ~ symbol to allow comments
		String message = event.getMessage().split("~")[0];

		// non-modOnly always meets the condition. Being OP or whitelisted too
		if (!botConfig.modOnly || isOpByEvent(event) || botConfig.whitelist.contains(event.getUser().getNick())) {

			// data command
			if (message.startsWith(botConfig.commandData + " ")) {

				String name = message.split(" ", 2)[1];
				String[] info = selector.getAnyInfoByName(name);

				boolean send = true;
				long currentMillis = System.currentTimeMillis();
				if (cooldown.containsKey(info[0])) {
					if (currentMillis - cooldown.get(info[0]) > botConfig.cooldownMillis)
						cooldown.replace(info[0], currentMillis);
					else
						send = false;
				} else {
					cooldown.put(info[0], currentMillis);
				}
				if (send)
					sendMessageWithRequested(event, info[1]);
			}
			// egg command
			else if (message.startsWith(botConfig.commandEgg + " ")) {

				String[] names = message.split(" ", 2)[1].split(",");
				String info;
				if (names.length == 2) {
					info = selector.getCommonEggGroupsByNames(names[0].trim(), names[1].trim());
				} else {
					info = String.format("Command: %s PokeName1, PokeName2", botConfig.commandEgg);
				}

				sendMessageWithRequested(event, info);
			}
			// learn command
			else if (message.startsWith(botConfig.commandLearn + " ")) {

				String[] names = message.split(" ", 2)[1].split(",");
				String info;
				if (names.length == 2) {
					info = selector.getLearnInfoByNamesInGivenGeneration(names[0].trim(), names[1].trim(),
							botConfig.defaultGen);
				} else if (names.length == 3) {
					info = selector.getLearnInfoByNamesInGivenGeneration(names[0].trim(), names[1].trim(),
							Integer.parseInt(names[2].trim()));
				} else {
					info = String.format(
							"Command: For current generation: %s PokeName, MoveName OR for given generation: %s PokeName, MoveName, GenerationNumber",
							botConfig.commandLearn, botConfig.commandLearn);
				}

				sendMessageWithRequested(event, info);
			}
			// commands command
			else if (message.equals(botConfig.commandCommands)) {
				String commands = String.format(
						"Available Pokemon Commands | 1) Pokemon/Item/Move/Ability Info: %s Name | "
								+ " 2) Same Egg Groups: %s PokeName1, PokeName2 | "
								+ " 3) If a Pokemon can learn a move: For current generation: %s PokeName, MoveName OR for given generation: %s PokeName, MoveName, GenerationNumber | "
								+ "Mod-only commands: https://github.com/themetalfleece/ircpokemonbot/blob/master/README.md#mod-commands-for-modifying-the-config-file",
						botConfig.commandData, botConfig.commandEgg, botConfig.commandLearn, botConfig.commandLearn);
				sendMessageWithRequested(event, commands);
			}
			// info command
			else if (message.equals(botConfig.commandInfo)) {
				String info = "Pokemon Database Twitch Chat Bot. Type " + botConfig.commandCommands
						+ " for available commands. Created by themetalfleece. Source and executable: https://github.com/themetalfleece/ircpokemonbot";
				sendMessageWithRequested(event, info);
			}
			// commands for mods modifying the ini
			// !pbconfig modOnly, t
			else if (isOpByEvent(event)) {
				String info = null;
				if (message.startsWith("!pbconfig")) {
					String key = null;
					String value = null;
					try {
						key = message.split(" ")[1];
						value = message.split(" ")[2].trim();
					} catch (java.lang.ArrayIndexOutOfBoundsException e) {
						sendMessageWithRequested(event, "Invalid format. Correct format: !pbconfig field value | "
								+ "Notice that you should include a space between \"pbconfig\", the field and the value. More info: https://github.com/themetalfleece/ircpokemonbot/blob/master/README.md#mod-commands-for-modifying-the-config-file");
						return;
					}
					if (key.equals("add")) {
						if (botConfig.whitelist.contains(value)) {
							sendMessageWithRequested(event, "User " + value + " already in whitelist.");
							return;
						}
						botConfig.ini.put("pokemon", "whitelist", botConfig.whitelistRaw + ", " + value);
						info = "Successfully added user " + value + " to the whitelist.";
					}
					// TODO messy much?
					else if (key.equals("remove")) {
						String whitelistRaw = new String();
						boolean first = true;
						if (!botConfig.whitelist.contains(value.toLowerCase())){
							sendMessageWithRequested(event, "User " + value + " is not whitelisted.");
							return;
						}
						for (String name : botConfig.whitelist) {
							if (!name.toLowerCase().equals(value)) {
								whitelistRaw += (first ? "" : ", ") + name;
								first = false;
							}
						}
						
						botConfig.ini.put("pokemon", "whitelist", whitelistRaw);
						info = "Successfully removed user " + value + " from the whitelist.";
					} else {
						info = "Successfully modified field: " + key;
						if (botConfig.ini.get("pokemon", key) != null) {
							botConfig.ini.put("pokemon", key, value);

						} else {
							sendMessageWithRequested(event,
									"Failed to modify field: " + key + ". Check if its case matches.");
							return;
						}
					}

					try {
						botConfig.ini.store();
						// TODO would be faster if assigned that single
						// value
						botConfig.assignValues();
						sendMessageWithRequested(event, info);
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}

		}

	}

	// WHISPERS
	/*
	 * the code is rather messy since I couldn't figure out a better way for the
	 * bot to get whispers, it should be working like this for Twitch
	 */
	@Override
	public void onUnknown(UnknownEvent event) {
		// super.onUnknown(event);
		if (botConfig.whispersEnabled) {
			if (event.getLine().toString().contains("WHISPER")) {
				String messageText = event.getLine().toString().split("WHISPER")[1].split(":", 2)[1];
				String user = event.getLine().toString().split(":")[1].split("!")[0];

				if (messageText.startsWith("!data ")) {

					String name = messageText.split(" ", 2)[1];
					String[] info = selector.getAnyInfoByName(name);

					sendWhisper(user, info[1]);
				} else if (messageText.startsWith("!egg ")) {

					String[] names = messageText.split(" ", 2)[1].split(",");
					String info;
					if (names.length == 2) {
						info = selector.getCommonEggGroupsByNames(names[0].trim(), names[1].trim());
					} else {
						info = "Command: !egg PokeName1, PokeName2";
					}

					sendWhisper(user, info);
				} else if (messageText.startsWith("!learn ")) {

					String[] names = messageText.split(" ", 2)[1].split(",");
					String info;
					if (names.length == 2) {
						info = selector.getLearnInfoByNamesInGivenGeneration(names[0].trim(), names[1].trim(),
								botConfig.defaultGen);
					} else if (names.length == 3) {
						info = selector.getLearnInfoByNamesInGivenGeneration(names[0].trim(), names[1].trim(),
								Integer.parseInt(names[2].trim()));
					} else {
						info = "Command: For current generation: !learn PokeName, MoveName OR for given generation: !learn PokeName, MoveName, GenerationNumber";
					}

					sendWhisper(user, info);
				}
			}
		}
	}

	// send a message tagging the user who requested
	private void sendMessageWithRequested(MessageEvent event, String info) {

		String signature = " [ @" + event.getUser().getNick() + " ]";
		bot.sendIRC().message(botConfig.channel, info + signature);

	}

	private void sendWhisper(String user, String message) {

		bot.sendIRC().message(botConfig.channel, "/w " + user + " " + message);

	}

	public PokeTwitchChatBot(BotConfiguration botConfig) throws IOException, IrcException {
		super(botConfig);

		this.botConfig = botConfig;

		connectToDB();

		// TODO find a more efficient way to check if it's connected
		while (!bot.isConnected())
			;

		// requests for whispers, mod checks etc
		bot.sendRaw().rawLine(("CAP REQ :twitch.tv/membership"));
		bot.sendRaw().rawLine(("CAP REQ :twitch.tv/tags"));
		bot.sendRaw().rawLine(("CAP REQ :twitch.tv/commands"));

	}

	private void connectToDB() {

		selector = new PS_SQLiteSelector(botConfig);

	}

}
