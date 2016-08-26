package com.themetalfleece.pokemondbbot;
/**
 * 
 */

import java.io.IOException;

import org.pircbotx.exception.IrcException;

/**
 *
 * Created by themetalfleece at 12 Jun 2016
 *
 */
public class IRCPokemonBot {

	public static void main(String[] args) {
		
		BotConfiguration botConfig = new BotConfiguration();
		
		IRCPokemonBotGui gui = new IRCPokemonBotGui();
		new Thread(gui).start();
		
		try {
			new PokeTwitchChatBot(botConfig);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IrcException e) {
			e.printStackTrace();
		}
		

	}
}
