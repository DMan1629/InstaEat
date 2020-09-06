package me.DMan16.CustomFood;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

import me.DMan16.InstaEat.InstaEat;
import me.DMan16.Utils.Permissions;
import me.DMan16.Utils.Utils;

public class CommandListener implements CommandExecutor {
	public CommandListener() {
		PluginCommand command = InstaEat.getMain().getCommand(InstaEat.getPluginName());
		command.setExecutor(this);
		command.setTabCompleter(new TabComplete());
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player && !Permissions.CommandPermission((Player) sender)) return false;
		if (args.length == 0) {
			CustomFoods.RootCommand(sender,args);
		} else {
			if (args[0].equalsIgnoreCase(CustomFoods.customFoodName)) {
				CustomFoods.CustomFoodCommand(sender,args);
			} else if (args[0].equalsIgnoreCase(CustomFoods.customFoodEffectName)) {
				CustomFoods.CustomFoodEffectCommand(sender,args);
			} else {
				Utils.chatColors(sender,"&cUnknown command. Try again.");
				return false;
			}
		}
		return true;
	}
}