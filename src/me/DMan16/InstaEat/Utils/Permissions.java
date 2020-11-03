package me.DMan16.InstaEat.Utils;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import me.DMan16.InstaEat.InstaEat.InstaEat;

public class Permissions {
	private static Permission allowPerm = new Permission(getPermission("allow"),PermissionDefault.FALSE);
	private static Permission preventPerm = new Permission(getPermission("prevent"),PermissionDefault.FALSE);
	private static Permission commandPerm = new Permission(getPermission("command"),PermissionDefault.OP);
	private static Permission vanillaFoodPerm = new Permission(getPermission("vanilla"),PermissionDefault.FALSE);

	public static boolean AllowPermission(Player sender) {
		return sender.hasPermission(allowPerm);
	}

	public static boolean PreventPermission(Player sender) {
		return sender.hasPermission(preventPerm);
	}
	
	public static boolean CommandPermission(Player sender) {
		if (sender.isOp()) return true;
		return sender.hasPermission(commandPerm);
	}
	
	public static boolean VanillaFoodPermission(Player sender) {
		return sender.hasPermission(vanillaFoodPerm);
	}
	
	public static void NoPermission(CommandSender sender) {
		Utils.chatColors(sender,"&cYou do not have permission to use this command.");
	}
	
	static String getPermission(String suffix) {
		return InstaEat.getPluginName().toLowerCase() + "." + suffix.toLowerCase();
	}
	
	public static void registerPermissions(Server server) {
		server.getPluginManager().addPermission(allowPerm);
		server.getPluginManager().addPermission(preventPerm);
		server.getPluginManager().addPermission(commandPerm);
		server.getPluginManager().addPermission(vanillaFoodPerm);
	}
	
	public static void unregisterPermissions(Server server) {
		server.getPluginManager().removePermission(allowPerm);
		server.getPluginManager().removePermission(preventPerm);
		server.getPluginManager().removePermission(commandPerm);
		server.getPluginManager().removePermission(vanillaFoodPerm);
	}
}