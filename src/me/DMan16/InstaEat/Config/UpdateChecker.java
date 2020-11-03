package me.DMan16.InstaEat.Config;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Consumer;

import me.DMan16.InstaEat.Utils.Utils;

public class UpdateChecker {
	private int resourceId;
	
	public UpdateChecker(JavaPlugin plugin, int resourceId) {
		this.resourceId = resourceId;
	}
	
	public void getVersion(final Consumer<String> consumer) {
		try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream();
				Scanner scanner = new Scanner(inputStream)) {
			if (scanner.hasNext()) {
				consumer.accept(scanner.next());
			}
		} catch (IOException exception) {
			Utils.chatColorsLog(Utils.chatColorsPlugin("Error while looking for updates: " + exception.getMessage()));
		}
	}
}