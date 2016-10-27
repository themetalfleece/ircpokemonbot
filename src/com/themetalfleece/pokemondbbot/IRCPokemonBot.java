package com.themetalfleece.pokemondbbot;
/**
 * 
 */

import java.io.IOException;

import org.pircbotx.exception.IrcException;

import com.themetalfleece.pokemondb.PS_SQLiteGenerator;

/**
 *
 * Created by themetalfleece at 12 Jun 2016
 *
 */
public class IRCPokemonBot {

	static BotConfiguration botConfig;
	private static boolean autostarted = false;
	private static boolean pathspecified = false;
	private static boolean noui = false;

	public static void main(String[] args) {

		for (int i = 0; i < args.length; i++) {
			System.out.println("****\n" + args[i] + "\n");
			switch (args[i]) {

			case "-refresh":
				new PS_SQLiteGenerator();
				break;

			case "-path":
				String path = args[++i];
				botConfig = new BotConfiguration(path);
				pathspecified = true;
				System.out.println("Path specified successfully");
				break;

			case "-autostart":
				autostarted = true;
				System.out.println("Marked for autostart");
				break;

			case "-noui":
				noui = true;
				System.out.println("Marked for no UI");
				break;

			default:
				System.out.println(
						"Illegal argument \"" + args[i] + "\". Arguments:\n" + "\"-refresh\" to refresh the database.\n"
								+ "\"-path path/to/config.ini\" to specify another path for the config file.\n"
								+ "\"-autostart\" to start the bot immediately.\n"
								+ "\"-noui\" to display no UI. Must be used along with -autostart\n");
				System.exit(0);
				break;
			}
			System.out.println("****\n");
		}

		if (!pathspecified)
			botConfig = new BotConfiguration("./config.ini");

		if (!noui)
			new IRCPokemonBotGui(botConfig);

		if (autostarted) {
			try {
				new PokeTwitchChatBot(botConfig);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (IrcException e) {
				e.printStackTrace();
			}
		}

	}
}
