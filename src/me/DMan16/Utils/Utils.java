package me.DMan16.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.command.CommandSender;

import me.DMan16.InstaEat.InstaEat;
import net.md_5.bungee.api.ChatColor;

public class Utils {
	private static List<Material> interactable = null;
	
	public static String chatColors(String str) {
		str = chatColorsStrip(str);
		return ChatColor.translateAlternateColorCodes('&', str);
	}
	
	public static void chatColors(CommandSender sender, String str) {
		sender.sendMessage(chatColors(str));
	}
	
	public static String chatColorsPlugin(String str) {
		return chatColors("&d[" + InstaEat.getPluginNameColors() + "&d]&r " + str);
	}

	public static void chatColorsPlugin(CommandSender sender, String str) {
		sender.sendMessage(chatColorsPlugin(str));
	}

	private static String chatColorsUsage(String str) {
		return chatColors("&cUsage: &r/" + InstaEat.getPluginNameColors() + "&r " + str);
	}

	public static void chatColorsUsage(CommandSender sender, String str) {
		sender.sendMessage(chatColorsUsage(str));
	}
	
	private static String chatColorsStrip(String str) {
		return ChatColor.stripColor(str);
	}
	
	public static boolean isInteractable(Material material) {
		if (interactable == null) {
			createInteractable();
		}
		return interactable.contains(material);
	}
	
	private static void createInteractable() {
		interactable = new ArrayList<Material>();
		List<Material> initialInteractable = Arrays.asList(Material.MINECART,Material.CHEST_MINECART,Material.FURNACE_MINECART,Material.HOPPER_MINECART,
				Material.CHEST,Material.ENDER_CHEST,Material.TRAPPED_CHEST,Material.NOTE_BLOCK,Material.CRAFTING_TABLE,Material.FURNACE,Material.BLAST_FURNACE,
				Material.LEVER,Material.ENCHANTING_TABLE,Material.BEACON,Material.DAYLIGHT_DETECTOR,Material.HOPPER,Material.DROPPER,Material.REPEATER,
				Material.COMPARATOR,Material.COMPOSTER,Material.CAKE,Material.BREWING_STAND,Material.LOOM,Material.BARREL,Material.SMOKER,
				Material.CARTOGRAPHY_TABLE,Material.FLETCHING_TABLE,Material.SMITHING_TABLE,Material.GRINDSTONE,Material.LECTERN,Material.STONECUTTER,
				Material.DISPENSER,Material.BELL,Material.RESPAWN_ANCHOR,Material.ITEM_FRAME);
		interactable.addAll(initialInteractable);
		interactable.addAll(new ArrayList<Material>(Tag.ANVIL.getValues()));
		interactable.addAll(new ArrayList<Material>(Tag.BUTTONS.getValues()));
		interactable.addAll(new ArrayList<Material>(Tag.FENCE_GATES.getValues()));
		interactable.addAll(new ArrayList<Material>(Tag.TRAPDOORS.getValues()));
		interactable.addAll(new ArrayList<Material>(Tag.DOORS.getValues()));
		interactable.addAll(new ArrayList<Material>(Tag.BEDS.getValues()));
		interactable.addAll(new ArrayList<Material>(Tag.SHULKER_BOXES.getValues()));
		interactable.addAll(new ArrayList<Material>(Tag.CAMPFIRES.getValues()));
	}
	
	public static NamespacedKey namespacedKey(String name) {
		return new NamespacedKey(InstaEat.getMain(),name);
	}
}