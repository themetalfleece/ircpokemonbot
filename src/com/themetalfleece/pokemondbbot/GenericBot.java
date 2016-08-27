/**
 * 
 */
package com.themetalfleece.pokemondbbot;

/**
 *
 * Created by themetalfleece at 24 Aug 2016
 *
 */

import java.io.IOException;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

public class GenericBot extends ListenerAdapter {
	
	PircBotX bot;

	@SuppressWarnings("deprecation")
	public GenericBot(BotConfiguration botConfig) throws IOException, IrcException {
		
		final Configuration configuration = new Configuration.Builder().setName(botConfig.botName)
				.setServerHostname(botConfig.serverHostname).setServerPort(botConfig.serverPort).setServerPassword(botConfig.serverPassword)
				.addAutoJoinChannel(botConfig.channel).addListener(this).buildConfiguration();

		bot = new PircBotX(configuration);
		
		new Thread(new Runnable() {
			
			public void run() {
				try {
					bot.startBot();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (IrcException e) {
					e.printStackTrace();
				}
				
			}
		}).start();
		

	}
	
	private boolean isModByEvent(MessageEvent event) {
		return event.getTags().get("mod").equals("1");
	}

	private boolean isBroadcasterByEvent(MessageEvent event) {
		return event.getTags().get("badges").contains("broadcaster/1");
	}

	protected boolean isOpByEvent(MessageEvent event) {
		return isModByEvent(event) || isBroadcasterByEvent(event);
	}
}
