package me.DMan16.InstaEat.Listeners;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

import me.DMan16.InstaEat.GUI.Menu;
import me.DMan16.InstaEat.GUI.MenuType;
import me.DMan16.InstaEat.InstaEat.InstaEat;
import me.DMan16.InstaEat.Utils.Permissions;

public class CommandListener implements CommandExecutor {
	public CommandListener() {
		PluginCommand command = InstaEat.getMain().getCommand(InstaEat.getPluginName());
		command.setExecutor(this);
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player && !Permissions.CommandPermission((Player) sender)) return false;
		new Menu(MenuType.MAIN,(Player) sender).openMenu();
		return true;
	}
}