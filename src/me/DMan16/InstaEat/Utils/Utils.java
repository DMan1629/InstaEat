package me.DMan16.InstaEat.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import me.DMan16.InstaEat.Config.MCFoodsDefault;
import me.DMan16.InstaEat.InstaEat.InstaEat;
import net.md_5.bungee.api.ChatColor;

public class Utils {
	private static List<Material> interactable = null;
	
	public static String chatColors(String str) {
		str = chatColorsStrip(str);
		return ChatColor.translateAlternateColorCodes('&', str);
	}
	
	public static List<String> chatColors(List<String> list) {
		List<String> newList = new ArrayList<String>();
		for (String str : list) {
			newList.add(chatColors(str));
		}
		return newList;
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
	
	public static String chatColorsStrip(String str) {
		return ChatColor.stripColor(str);
	}
	
	public static void chatColorsLog(String str) {
		InstaEat.getLog().info(chatColors(str));
	}
	
	public static void chatColorsLogPlugin(String str) {
		InstaEat.getLog().info(chatColorsPlugin(str));
	}
	
	public static boolean isInteractable(Block block) {
		if (interactable == null) createInteractable();
		boolean telePad = false;
		try {
			telePad = me.DMan16.TelePadtation.TelePadtation.contains(block.getLocation());
		} catch (Exception e) {}
		return telePad || interactable.contains(block.getType());
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
	
	public static String splitCapitalize(String str, String splitReg) {
		if (str == null || str.isEmpty() || str == "") return "";
		String[] splitName = null;
		if (splitReg == null || splitReg.isEmpty() || splitReg == "") {
			splitName = new String [] {str};
		}
		else {
			splitName = str.split(splitReg);
		}
		String newStr = "";
		for (String sub : splitName) {
			boolean found = false;
			int i;
			for (i = 0; i < sub.length() - 1; i++) {
				try {
					if (sub.substring(i-1,i).equalsIgnoreCase("&")) continue;
				} catch (Exception e) {
				}
				if (sub.substring(i,i+1).matches("[a-zA-Z]+")) {
					found = true;
					break;
				}
			}
			if (found) {
				newStr += sub.substring(0,i) + sub.substring(i,i+1).toUpperCase() + sub.substring(i+1).toLowerCase() + " ";
			}
		}
		newStr.replace(" Of ", " of ");
		newStr.replace(" The ", " the ");
		return newStr.trim();
	}
	
	public static boolean isFood(Material material) {
		return MCFoodsDefault.isFood(material);
	}
	
	public static boolean isFood(ItemStack item) {
		return isFood(item.getType());
	}
	

	public static JString JString(String str) {
		return new JString(str);
	}

	@SuppressWarnings("unused")
	private static class JString implements java.io.Serializable{
		private static final long serialVersionUID = 1L;
		String value;
		public JString(String value) {
			super();
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		public void setValue(String value) {
			this.value = value;
		}
		
		@Override
		public String toString(){
			return this.value;
		}
	}
}