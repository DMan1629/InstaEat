package me.DMan16.InstaEat.InstaEat;

import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.DMan16.InstaEat.Config.CheckVersion;
import me.DMan16.InstaEat.Config.ConfigLoader;
import me.DMan16.InstaEat.Config.FoodsConfigManager;
import me.DMan16.InstaEat.Config.UpdateChecker;
import me.DMan16.InstaEat.GUI.Menu;
import me.DMan16.InstaEat.GUI.MenuListener;
import me.DMan16.InstaEat.Listeners.CommandListener;
import me.DMan16.InstaEat.Listeners.FoodListener;
import me.DMan16.InstaEat.Utils.Permissions;
import me.DMan16.InstaEat.Utils.Utils;

public class InstaEat extends JavaPlugin {
	private static InstaEat main;
	private static Logger log = Bukkit.getLogger();
	private static String pluginName = "InstaEat";
	private static String pluginNameColors = "&e&lInsta&c&lEat";
	private final int spigotID = 83645;
	private static CheckVersion version;
	private static ConfigLoader config;
	public static HashMap<Player,Menu> MenuManager;
	public static FoodsConfigManager FoodsManager;
	public static Advancement food1;
	public static Advancement food2;
	
	public void onEnable() {
		main = this;
		version = new CheckVersion();
		if (version.isDead()) {
			Utils.chatColorsPlugin("&cunsupported version: " + 
					Bukkit.getServer().getVersion().split("\\(MC:")[1].split("\\)")[0].trim().split(" ")[0].trim() + "!");
			Utils.chatColorsLogPlugin("&csupported versions: " + version.supportedVersions() + ".");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		config = new ConfigLoader();
		if (config.getUpdateChecker()) {
			new UpdateChecker(this,spigotID).getVersion(version -> {
				if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
					Utils.chatColorsLogPlugin("Running latest version!");
				} else {
					Utils.chatColorsLogPlugin("New version avilable - v" + version);
				}
			});
		}
		FoodsManager = new FoodsConfigManager();
		if (FoodsManager.stop) {
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		setFoodAdvancements();
		Permissions.registerPermissions(getServer());
		new CommandListener();
		registerListeners();
		MenuManager = new HashMap<Player,Menu>();
		
		Utils.chatColorsLogPlugin("&aLoaded&f, running on version: " + 
				Bukkit.getServer().getVersion().split("\\(MC:")[1].split("\\)")[0].trim().split(" ")[0].trim() + ".");
	}

	public void onDisable() {
		Permissions.unregisterPermissions(getServer());
	}

	private void registerListeners() {
		PluginManager manager = getServer().getPluginManager();
		manager.registerEvents(new FoodListener(), this);
		manager.registerEvents(new MenuListener(), this);
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

	public static ConfigLoader getConfigLoader() {
		return config;
	}
	
	private static void setFoodAdvancements() {
		Iterator<Advancement> advancements = Bukkit.advancementIterator();
		while (advancements.hasNext()) {
			Advancement advancement = advancements.next();
			if (advancement.getKey().getKey().toLowerCase().contains("husbandry")) {
				if (advancement.getKey().getKey().toLowerCase().contains("root")) {
					food1 = advancement;
				} else if (advancement.getKey().getKey().toLowerCase().contains("balanced_diet")) {
					food2 = advancement;
				}
			}
		}
	}
}