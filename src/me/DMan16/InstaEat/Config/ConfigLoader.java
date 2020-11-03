package me.DMan16.InstaEat.Config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;

import me.DMan16.InstaEat.InstaEat.InstaEat;
import me.DMan16.InstaEat.Utils.Utils;

@SuppressWarnings("rawtypes")
public class ConfigLoader {
	private final String header;
	private boolean updateChecker;
	private boolean instaEat;
	private boolean fullHungerEat;
	private boolean fullSaturationEat;
	
	private ArrayList<ConfigOption<?>> configOptionsList;

	public ConfigLoader() {
		configOptionsList = new ArrayList<ConfigOption<?>>();
		header = InstaEat.getPluginName() + " config file";
		makeConfig();
	}

	private void makeConfig() {
		String[] updateCheckerComment = {"Check if a new update was released (true/false)"};
		String[] instaEatComment = {"InstaEat system","InstaEat - Enable InstaEat for everyone (true/false)"};
		String[] fullHungerComment = {"full-hunger-eat - Allow eating even when the hunger bar is full (true/false)"};
		String[] fullSaturationComment = {"full-saturation-eat - Allow eating even when the player's saturation is full, " +
				"**NOT recommended to enable!!!** (true/false)"};

		FileConfiguration config = InstaEat.getMain().getConfig();

		updateChecker = ((Boolean) addNewConfigOption(config,"update-checker",Boolean.valueOf(true),updateCheckerComment)).booleanValue();
		instaEat = ((Boolean) addNewConfigOption(config,"InstaEat",Boolean.valueOf(true),instaEatComment)).booleanValue();
		fullHungerEat = ((Boolean) addNewConfigOption(config,"full-hunger-eat",Boolean.valueOf(true),fullHungerComment)).booleanValue();
		fullSaturationEat = ((Boolean) addNewConfigOption(config,"full-saturation-eat",Boolean.valueOf(false),fullSaturationComment)).booleanValue();
		
		writeConfig();
	}

	private <T> T addNewConfigOption(FileConfiguration config, String optionName, T defaultValue, String[] comment) {
		ConfigOption<T> option = new ConfigOption<T>(config,optionName,defaultValue,comment);

		configOptionsList.add(option);
		return (T) option.getValue();
	}

	private void writeConfig() {
		try {
			File dataFolder = InstaEat.getMain().getDataFolder();
			if (!dataFolder.exists()) {
				dataFolder.mkdir();
			}
			File saveTo = new File(InstaEat.getMain().getDataFolder(),"config.yml");
			if (!saveTo.exists()) {
				saveTo.createNewFile();
			} else {
				saveTo.delete();
				saveTo.createNewFile();
			}
			FileWriter fw = new FileWriter(saveTo,true);
			PrintWriter pw = new PrintWriter(fw);
			if (header != null) {
				pw.print("## " + header + "\n");
			}
			for (int i = 0; i < configOptionsList.size(); i++) {
				pw.print("\n" + ((ConfigOption) configOptionsList.get(i)).toString()
						+ ((i == configOptionsList.size() - 1) || ((i < configOptionsList.size() - 1)
								&& (((ConfigOption) configOptionsList.get(i + 1)).getComment() == null)) ? "" : "\n"));
			}
			pw.flush();
			pw.close();
		} catch (IOException e) {
			InstaEat.getLog().log(Level.SEVERE,Utils.chatColorsPlugin("Could not save config.yml! "
					+ "Please contact an admin for the following error: "));
			e.printStackTrace();
		}
	}

	public boolean getUpdateChecker() {
		return updateChecker;
	}

	public boolean getInstaEat() {
		return instaEat;
	}

	public boolean getFullHungerEat() {
		return fullHungerEat;
	}

	public boolean getFullSaturationEat() {
		return fullSaturationEat;
	}
}