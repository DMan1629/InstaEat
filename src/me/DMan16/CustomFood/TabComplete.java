package me.DMan16.CustomFood;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import me.DMan16.Utils.Permissions;

public class TabComplete implements TabCompleter {
	List<String> empty = new ArrayList<String>();
	final List<String> base = Arrays.asList(CustomFoods.customFoodName,CustomFoods.customFoodEffectName);

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) return empty;
		if (!Permissions.CommandPermission((Player) sender)) return empty;
		List<String> reusltList = new ArrayList<String>();
		if (args.length == 1) {
			for (String cmd : base) {
				if (contains(args[0],cmd)) {
					reusltList.add(cmd.toLowerCase());
				}
			}
			return reusltList;
		}
		if (args.length == 2) {
			List<String> current = new ArrayList<String>();
			if (args[0].equalsIgnoreCase(base.get(0))) {
				current = CustomFoods.customFood;
			} else if (args[0].equalsIgnoreCase(base.get(1))) {
				current = CustomFoods.customFoodEffect;
			}
			for (String cmd : current) {
				if (contains(args[1],cmd)) {
					reusltList.add(cmd.toLowerCase());
				}
			}
			return reusltList;
		}
		if (args[0].equalsIgnoreCase(base.get(1))) {
			if (args[1].equalsIgnoreCase(CustomFoods.customFoodEffect.get(1))) {
				if (args.length == 3) {
					for (String cmd : CustomFoods.customFoodEffectAdd) {
						if (contains(args[2],cmd)) {
							reusltList.add(cmd);
						}
					}
					return reusltList;
				}
			}
			if (args.length == 3 && args[1].equalsIgnoreCase(CustomFoods.customFoodEffect.get(2))) {
				for (PotionEffectType effect : PotionEffectType.values()) {
					if (contains(args[2],effect.getName())) {
						reusltList.add(effect.getName().toUpperCase());
					}
				}
				return reusltList;
			}
			if (args.length == 4 && args[1].equalsIgnoreCase(CustomFoods.customFoodEffect.get(1)) &&
					(args[2].equalsIgnoreCase(CustomFoods.customFoodEffectAdd.get(0)) || args[2].equalsIgnoreCase(CustomFoods.customFoodEffectAdd.get(1)))) {
				for (PotionEffectType effect : PotionEffectType.values()) {
					if (contains(args[3],effect.getName())) {
						reusltList.add(effect.getName().toUpperCase());
					}
				}
				return reusltList;
			}
		}
		return empty;
	}
	
	private boolean contains(String arg1, String arg2) {
		return (arg1 == null || arg1.isEmpty() || arg2.toLowerCase().contains(arg1.toLowerCase()));
	}
}