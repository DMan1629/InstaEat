package me.DMan16.Utils;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import me.DMan16.InstaEat.InstaEat;

public class Permissions {
	private static Permission preventPerm = new Permission(getPermission(getPermission("prevent")));
	private static Permission commandPerm = new Permission(getPermission(getPermission("command")));

	public static boolean PreventPermission(Player sender) {
		if (sender.isOp()) return false;
		return sender.hasPermission(preventPerm);
	}
	public static boolean CommandPermission(Player sender) {
		if (sender.isOp()) return true;
		return sender.hasPermission(preventPerm);
	}
	
	public static void NoPermission(CommandSender sender) {
		Utils.chatColors(sender,"&cYou do not have permission to use this command.");
	}
	
	static String getPermission(String suffix) {
		return InstaEat.getPluginName().toLowerCase() + "." + suffix.toLowerCase();
	}
	
	public static void registerPermissions(Server server) {
		try {
			server.getPluginManager().addPermission(preventPerm);
			server.getPluginManager().addPermission(commandPerm);
		} catch (Exception e) {
		}
	}
	
	public static void unregisterPermissions(Server server) {
		try {
			server.getPluginManager().removePermission(preventPerm);
			server.getPluginManager().removePermission(commandPerm);
		} catch (Exception e) {
		}
	}
}