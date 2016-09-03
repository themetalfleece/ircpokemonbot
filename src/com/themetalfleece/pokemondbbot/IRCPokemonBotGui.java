/**
 * 
 */
package com.themetalfleece.pokemondbbot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultCaret;

import org.pircbotx.exception.IrcException;

import com.themetalfleece.pokemondb.PS_SQLiteGenerator;

/**
 *
 * Created by themetalfleece at 26 Aug 2016
 *
 */
public class IRCPokemonBotGui extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static JTextArea textArea;
	final BotConfiguration botConfig = new BotConfiguration();

	public IRCPokemonBotGui() {

		super("Pokemon Bot Main Menu");

		setLayout(new FlowLayout());

		JButton startBot = new JButton("Start Bot");
		JButton configureBot = new JButton("Configure Bot");
		JButton refreshDb = new JButton("Refresh Database");
		startBot.setPreferredSize(configureBot.getPreferredSize());

		textArea = new JTextArea();
		JFrame runFrame = new JFrame("Pokemon Bot");
		runFrame.setSize(600, 600);
		runFrame.setVisible(true);
		runFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);

		textArea.setEditable(false);
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);

		DefaultCaret caret = (DefaultCaret) textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		JScrollPane scrollPane = new JScrollPane(textArea);
		runFrame.add(scrollPane);

		new Thread(new outRedirector()).start();

		startBot.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							new PokeTwitchChatBot(botConfig);
						} catch (IOException ex) {
							ex.printStackTrace();
						} catch (IrcException ex) {
							ex.printStackTrace();
						}

					}
				}).start();

			}

		});

		configureBot.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				createConfigFrame();

			}
		});

		refreshDb.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new Thread(new Runnable() {

					@Override
					public void run() {

						new PS_SQLiteGenerator();

					}
				}).start();

			}
		});

		add(startBot);
		add(configureBot);
		add(refreshDb);

		setLocationRelativeTo(null);
		setSize(340, 110);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

	}

	private void createConfigFrame() {

		final JFrame configFrame = new JFrame("Pokemon Bot Configuration");

		configFrame.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(5, 15, 5, 15);
		gc.fill = GridBagConstraints.BOTH;
		gc.gridx = 0;
		gc.gridy = 0;

		// Connection
		final JTextField channelField = new ConfigField(botConfig.channel);
		final JTextField botNameField = new ConfigField(botConfig.botName);
		final JTextField serverHostnameField = new ConfigField(botConfig.serverHostname);
		final JTextField serverPortField = new ConfigField(Integer.toString(botConfig.serverPort));
		final JTextField serverPasswordField = new ConfigField(botConfig.serverPassword);

		configFrame.add(new InfoLabel("Connection"), gc);
		gc.gridy++;

		addLabelAndComponentToConfigFrame(configFrame, gc, new JLabel("Channel Name"), channelField);
		addLabelAndComponentToConfigFrame(configFrame, gc, new JLabel("Bot Name"), botNameField);
		addLabelAndComponentToConfigFrame(configFrame, gc, new JLabel("Server Hostname"), serverHostnameField);
		addLabelAndComponentToConfigFrame(configFrame, gc, new JLabel("Server Port"), serverPortField);
		addLabelAndComponentToConfigFrame(configFrame, gc, new JLabel("Server Password"), serverPasswordField);

		// Functionality
		final JTextField commandDataField = new ConfigField(botConfig.commandData);
		final JTextField commandEggField = new ConfigField(botConfig.commandEgg);
		final JTextField commandLearnField = new ConfigField(botConfig.commandLearn);
		final JTextField commandCommandsField = new ConfigField(botConfig.commandCommands);
		final JTextField commandInfoField = new ConfigField(botConfig.commandInfo);
		final JTextField cooldownMillisField = new ConfigField(Long.toString(botConfig.cooldownMillis));

		final JTextField defaultGenField = new JTextField(Integer.toString(botConfig.defaultGen), 2);
		final JCheckBox modOnlyBox = new JCheckBox("", botConfig.modOnly);
		final JCheckBox whispersEnabledBox = new JCheckBox("", botConfig.whispersEnabled);

		gc.gridx = 0;
		gc.gridy++;
		configFrame.add(new InfoLabel("Functionality"), gc);

		addLabelAndComponentToConfigFrame(configFrame, gc, new JLabel("Data Command"), commandDataField);
		addLabelAndComponentToConfigFrame(configFrame, gc, new JLabel("Egg Command"), commandEggField);
		addLabelAndComponentToConfigFrame(configFrame, gc, new JLabel("Learn Command"), commandLearnField);
		addLabelAndComponentToConfigFrame(configFrame, gc, new JLabel("Commands Command"), commandCommandsField);
		addLabelAndComponentToConfigFrame(configFrame, gc, new JLabel("Info Command"), commandInfoField);
		addLabelAndComponentToConfigFrame(configFrame, gc, new JLabel("Cooldown Millis"), cooldownMillisField);

		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.WEST;
		addLabelAndComponentToConfigFrame(configFrame, gc, new JLabel("Default Generation"), defaultGenField);
		addLabelAndComponentToConfigFrame(configFrame, gc, new JLabel("Mod only"), modOnlyBox);
		addLabelAndComponentToConfigFrame(configFrame, gc, new JLabel("Whispers Enabled"), whispersEnabledBox);

		// Button

		JButton saveButton = new JButton("Save Changes");
		saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				botConfig.ini.put("twitch", "channel", channelField.getText());
				botConfig.ini.put("twitch", "botName", botNameField.getText());
				botConfig.ini.put("twitch", "serverHostname", serverHostnameField.getText());
				botConfig.ini.put("twitch", "serverPort", Integer.parseInt(serverPortField.getText()));
				botConfig.ini.put("twitch", "serverPassword", serverPasswordField.getText());

				botConfig.ini.put("pokemon", "defaultGen", Integer.parseInt(defaultGenField.getText()));
				botConfig.ini.put("pokemon", "modOnly", (modOnlyBox.isSelected() ? "t" : "f"));
				botConfig.ini.put("pokemon", "cooldownMillis", Long.parseLong(cooldownMillisField.getText()));
				botConfig.ini.put("pokemon", "whispersEnabled", (whispersEnabledBox.isSelected() ? "t" : "f"));
				botConfig.ini.put("pokemon", "data", commandDataField.getText());
				botConfig.ini.put("pokemon", "egg", commandEggField.getText());
				botConfig.ini.put("pokemon", "learn", commandLearnField.getText());
				botConfig.ini.put("pokemon", "commands", commandCommandsField.getText());
				botConfig.ini.put("pokemon", "info", commandInfoField.getText());

				try {
					botConfig.ini.store();
					botConfig.assignValues();
					JOptionPane.showMessageDialog(configFrame,
							"Changes saved successfully.\nFunctionality changes have been applied, connection changes require program restart.",
							"Success", JOptionPane.INFORMATION_MESSAGE);
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(configFrame, "Error in saving changes.", "Error",
							JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
			}
		});

		gc.gridx = 1;
		gc.gridy++;
		configFrame.add(saveButton, gc);

		configFrame.setSize(560, 560);
		configFrame.setMinimumSize(new Dimension(560, 560));
		configFrame.setVisible(true);
		configFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

	}

	private void addLabelAndComponentToConfigFrame(JFrame configFrame, GridBagConstraints gc, JLabel label,
			JComponent component) {
		gc.gridy++;
		gc.gridx = 0;
		configFrame.add(label, gc);
		gc.gridx++;
		configFrame.add(component, gc);
	}

}

class ConfigField extends JTextField {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3655884632455601902L;

	public ConfigField(String title) {
		super(title);
		setColumns(30);
	}

}

class InfoLabel extends JLabel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8572365745696326918L;

	public InfoLabel(String title) {
		super(title);
		setForeground(Color.red);
	}
}

class outRedirector implements Runnable {

	@Override
	public void run() {
		redirectSystemStreams();
	}

	// The following codes set where the text get redirected. In this case,
	// jTextArea1
	private void updateTextArea(final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				IRCPokemonBotGui.textArea.append(text);
			}
		});
	}

	// Followings are The Methods that do the Redirect, you can simply Ignore
	// them.
	private void redirectSystemStreams() {
		OutputStream out = new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				updateTextArea(String.valueOf((char) b));
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				updateTextArea(new String(b, off, len));
			}

			@Override
			public void write(byte[] b) throws IOException {
				write(b, 0, b.length);
			}
		};

		System.setOut(new PrintStream(out, true));
		System.setErr(new PrintStream(out, true));
	}

}
