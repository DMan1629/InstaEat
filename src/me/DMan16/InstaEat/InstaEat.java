package me.DMan16.InstaEat;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.DMan16.Config.FoodsConfigManager;
import me.DMan16.CustomFood.CommandListener;
import me.DMan16.Utils.CheckVersion;
import me.DMan16.Utils.Permissions;
import me.DMan16.Utils.Utils;

public class InstaEat extends JavaPlugin {
	private static InstaEat main;
	private static Logger log = Bukkit.getLogger();
	private static String pluginName = "InstaEat";
	private static String pluginNameColors = "&e&lInsta&c&lEat";
	private static CheckVersion version;
	public static FoodsConfigManager foodsManager;
	
	public void onEnable() {
		main = this;
		version = new CheckVersion();
		if (version.isDead()) {
			log.info(Utils.chatColorsPlugin("&cunsupported version: " +
					Bukkit.getServer().getVersion().split("\\(MC:")[1].split("\\)")[0].trim().split(" ")[0].trim() + "!"));
			log.info(Utils.chatColorsPlugin("&csupported versions: " + version.supportedVersions() + "."));
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		foodsManager = new FoodsConfigManager();
		if (foodsManager.stop) {
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		Permissions.registerPermissions(getServer());
		new CommandListener();
		registerListeners();
		
		log.info(Utils.chatColorsPlugin("&aLoaded&f, running on version: " + 
				Bukkit.getServer().getVersion().split("\\(MC:")[1].split("\\)")[0].trim().split(" ")[0].trim() + "."));
	}

	public void onDisable() {
		Permissions.unregisterPermissions(getServer());
	}

	private void registerListeners() {
		PluginManager manager = Bukkit.getPluginManager();
		manager.registerEvents(new FoodListener(), this);
	}

	public static InstaEat getMain() {
		return main;
	}

	public static Logger getLog() {
		return log;
	}

	public static String getPluginName() {
		return pluginName;
	}

	public static String getPluginNameColors() {
		return pluginNameColors;
	}

	static CheckVersion getVersion() {
		return version;
	}
}